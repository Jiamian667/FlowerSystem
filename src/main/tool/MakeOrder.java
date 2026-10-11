package main.tool;

import main.classes.Discountable;
import main.classes.Order;
import main.classes.User;
import main.exception.BalanceNotEnoughException;
import main.exception.StockNotEnoughException;

import java.util.Map;

// 下单，订单结算，用户余额计算，库存计算
public class MakeOrder{


    // 初始化库存管理器
    private static InventoryManager inventoryManager;
    public MakeOrder(InventoryManager inventoryManager) {
        this.inventoryManager = inventoryManager;
    }

    // 计算折后价，校验用户余额，扣减库存，扣减余额
    public static void sell(Order order, User user) throws StockNotEnoughException, IllegalArgumentException, BalanceNotEnoughException {


        double finalAmount = order.calDiscountPrice();

        if (user.getBalance() < finalAmount) {
            throw new BalanceNotEnoughException("余额不足");
        }

        // 键值对，计算总共要多少
        Map<String, Integer> requiredStock = inventoryManager.calRequiredStock(order);

        inventoryManager.deductStock(requiredStock);//扣减库存

        user.setBalance(user.getBalance() - finalAmount);//扣减余额

    }
}