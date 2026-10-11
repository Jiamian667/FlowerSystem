package main.classes;

import main.tool.InventoryManager;

public abstract class Product{

    protected String id;
    protected String name;
    protected double originalPrice;


    public Product() {}

    public Product(String id, String name, double originalPrice) {
        this.id = id;
        this.name = name;
        this.originalPrice = originalPrice;
    }

    public abstract boolean checkStock(InventoryManager manager, int requiredQty);

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(double originalPrice) {
        this.originalPrice = originalPrice;
    }

}