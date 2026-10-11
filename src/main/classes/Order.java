package main.classes;

import java.util.ArrayList;
import java.util.List;

public class Order implements Discountable {
    private User user;
    private List<OrderItem> items = new ArrayList<>();

    public Order(User user) {
        this.user = user;
    }

    //Pos里复用了addItem
    public void addItem(OrderItem item) { this.items.add(item); }
    public List<OrderItem> getItems() { return items; }

    @Override
    public double calDiscountPrice() {

        double oriTotal = 0.0;//打折前总价
        for (OrderItem item : items) {
            oriTotal += item.getItemTotal();
        }

        if (user.isVip()) {
            return oriTotal * 0.88;
        }

        return oriTotal;
    }
}