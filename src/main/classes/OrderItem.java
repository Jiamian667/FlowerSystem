package main.classes;

// 订单
public class OrderItem {
    private Product product; // 订单有的product
    private int quantity;    // 商品数量

    // 有参构造初始化
    public OrderItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public Product getProduct() { return product; }

    public int getQuantity() { return quantity; }


    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getItemTotal() {
        return product.getOriginalPrice() * quantity;
    }
}