package main.thread;

import main.classes.CutFlower;
import main.exception.StockNotEnoughException;
import main.tool.InventoryManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// 外卖并发线程
public class OnlineOrderThread extends Thread {

    private final InventoryManager inventoryManager; // 库存管理器，用于操作全局库存

    // 有参构造函数，接收并初始化库存管理器对象
    public OnlineOrderThread(InventoryManager inventoryManager) {
        this.inventoryManager = inventoryManager;
    }

    // 线程的核心执行方法，进入死循环以持续模拟外卖订单的到来@Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(20000);

                List<CutFlower> allFlowers = new ArrayList<>(inventoryManager.getGlobalStock().values());

                CutFlower rose = allFlowers.get(0);
                int qty = 1;

                Map<String, Integer> deductMap = new HashMap<>();
                deductMap.put(rose.getId(), qty);

                inventoryManager.deductStock(deductMap);

                System.out.println("外卖平台自动扣减库存成功，当前剩余库存: " + rose.getStock());

            } catch (StockNotEnoughException e) {
                System.err.println("外卖订单取消，库存不足" + e.getMessage());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}