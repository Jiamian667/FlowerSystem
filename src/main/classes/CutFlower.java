package main.classes;
import main.tool.InventoryManager;

//鲜切花，复数打折逻辑，查库存
public class CutFlower extends Product {
    private int stock;

    // 有参构造，初始化鲜切花ID名称原价库存
    public CutFlower(String id, String name, double originalPrice, int stock) {
        super(id,name,originalPrice);
        this.stock=stock;
    }

    // get库存数量
    public int getStock(){
        return stock;
    }

    // set鲜切花的库存数量
    public void  setStock(int newStock){
        this.stock=newStock;
    }

    // 检查库存是否满足所需
    @Override
    public boolean checkStock(InventoryManager manager, int requiredQty) {
        return manager.hasStock(this.getId(), requiredQty);
        //requiredQty从Product继承来，CutFlower和Bouquet均有复用
    }
}