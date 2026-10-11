package main.tool;

import main.exception.StockNotEnoughException;
import main.classes.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.HashMap;

// 库存管理
public class InventoryManager {

    private Map<String, CutFlower> globalStock = new HashMap<>(); // 全局鲜切花库存
    private List<Bouquet> bouquetList = new ArrayList<>();        // 系统中所有花束列表

    // get花束列表
    public List<Bouquet> getBouquetList() { return bouquetList; }

    // get全局鲜切花库存
    public Map<String, CutFlower> getGlobalStock() { return globalStock; }

    // 线程安全，检查指定鲜切花库存
    public synchronized boolean hasStock(String flowerId, int needQty) {
        CutFlower flower = globalStock.get(flowerId);

        if (flower == null) {
            return false;
        } else {
            if (flower.getStock() >= needQty) {
                return true;
            } else {
                return false;
            }
        }
    }

    // 线程安全，从数据库中初始化CutFlower Bouquet Recipe
    public synchronized void InitDataFromDB() {
        globalStock.clear();
        bouquetList.clear();

        // CutFlower
        String flowerSql = "SELECT id, name, stock, price FROM cut_flowers";
        try (Connection conn = ConSQL.getConnection();
             PreparedStatement ps = conn.prepareStatement(flowerSql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String id = rs.getString("id");
                String name = rs.getString("name");
                int stock = rs.getInt("stock");
                double price = rs.getDouble("price");

                globalStock.put(id, new CutFlower(id, name, price, stock));
            }
        } catch (SQLException e) {
            System.err.println("从数据库读取鲜花数据失败！");
            e.printStackTrace();
        }

        // Bouquet
        String bouquetSql = "SELECT id, name, price FROM bouquets";
        try (Connection conn = ConSQL.getConnection();
             PreparedStatement ps = conn.prepareStatement(bouquetSql);
             ResultSet rs = ps.executeQuery()) //三行都是用来检查数据的
        {

            while (rs.next()) {
                String id = rs.getString("id");
                String name = rs.getString("name");
                double price = rs.getDouble("price");

                bouquetList.add(new Bouquet(id, name, price));
            }
        } catch (SQLException e) {
            System.out.println("从数据库读取花束数据失败！");
            e.printStackTrace();
        }

        // Recipe
        String recipeSql = "SELECT bouquet_id, flower_id, required_qty FROM bouquet_recipe";
        try (Connection conn = ConSQL.getConnection();
             PreparedStatement ps = conn.prepareStatement(recipeSql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String bouquetId = rs.getString("bouquet_id");
                String flowerId = rs.getString("flower_id");
                int requiredQty = rs.getInt("required_qty");

                Bouquet targetBouquet = findBouquetById(bouquetId);
                CutFlower targetFlower = globalStock.get(flowerId);

                if (targetBouquet != null && targetFlower != null) {
                    targetBouquet.addRecipeItem(new RecipeItem(targetFlower, requiredQty));
                }
            }
        } catch (SQLException e) {
            System.err.println("从数据库读取花束配方失败！");
            e.printStackTrace();
        }
    }

    //根据ID在Reecipe List查找对应的Bouquet对象
    private Bouquet findBouquetById(String id) {
        for (Bouquet bouquet : bouquetList) {
            if (bouquet.getId().equals(id)) {
                return bouquet;
            }
        }
        return null;
    }

    // 计算整个订单总CutFlower，返回一个汇总的 Map
    public Map<String, Integer> calRequiredStock(Order order) {
        Map<String, Integer> requiredStockMap = new HashMap<>();//MakeOrder里复用
        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            int orderQuantity = item.getQuantity();

            if (product instanceof CutFlower) {
                //instance 类型检查符号 判断是否是某一类
                CutFlower flower = (CutFlower) product;
                requiredStockMap.put(flower.getId(),
                        requiredStockMap.getOrDefault(flower.getId(), 0) + orderQuantity);
            } else if (product instanceof Bouquet) {
                Bouquet bouquet = (Bouquet) product;
                for (RecipeItem recipe : bouquet.getItems()) {
                    String flowerId = recipe.getFlower().getId();
                    int needtotal = recipe.getRequiredQuantity() * orderQuantity;
                    requiredStockMap.put(flowerId,
                            requiredStockMap.getOrDefault(flowerId, 0) + needtotal);
                }
            }
        }
        return requiredStockMap;
    }

    // 线程安全,根据汇总Map，先检查再扣减库存
    public synchronized void deductStock(Map<String, Integer> requiredStockMap) throws StockNotEnoughException {
        // 检查花材库存
        for (Map.Entry<String, Integer> entry : requiredStockMap.entrySet()) {
            String flowerId = entry.getKey();
            int totalNeeded = entry.getValue();

            if (!hasStock(flowerId, totalNeeded)) {
                CutFlower flower = globalStock.get(flowerId);
                String flowerName = (flower != null) ? flower.getName() : flowerId;
                int currentStock = (flower != null) ? flower.getStock() : 0;
                throw new StockNotEnoughException("商品 [" + flowerName + "] 库存不足！当前: "
                        + currentStock + "，所需: " +entry.getValue());
            }
        }

        for (Map.Entry<String, Integer> entry : requiredStockMap.entrySet()) {
            String flowerId = entry.getKey();
            int totalNeeded = entry.getValue();
            CutFlower flower = globalStock.get(flowerId);
            if (flower != null) {
                flower.setStock(flower.getStock() - entry.getValue());
            }
        }
    }
}