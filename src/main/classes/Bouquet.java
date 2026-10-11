package main.classes;
import main.tool.InventoryManager;
import java.util.ArrayList;
import java.util.List;

//成品花束，包含List<RecipeItem>，加手工费，遍历查配方库存
public class Bouquet extends Product {

    private List<RecipeItem> items = new ArrayList<>();
    private double manualFee;

    public Bouquet() {
        super();
    }

    // 有参构造，初始化花束的ID名称原价
    public Bouquet(String id, String name, double originalPrice) {
        super(id, name, originalPrice);
    }

    // 向花束的配方中添加材料,InventoryManager里将目标材料加进去
    public void addRecipeItem(RecipeItem item) {
        this.items.add(item);
    }

    // 检查花束所需的所有配方材料库存是否充足
    @Override
    public boolean checkStock(InventoryManager manager, int requiredQty) {
        for (RecipeItem item : items) {
            int needPerMaterial = item.getRequiredQuantity() * requiredQty;
            String flowerId = item.getFlower().getId();

            boolean enough = manager.hasStock(flowerId, needPerMaterial);
            if (!enough) {
                return false;
            }
        }
        return true;
    }

    // get花束配方材料列表
    public List<RecipeItem> getItems() {
        return items;
    }

    // set花束配方材料列表
    public void setItems(List<RecipeItem> items) {
        this.items = items;
    }

    // get手工费
    public double getManualFee() {
        return manualFee;
    }

    // set手工费
    public void setManualFee(double manualFee) {
        this.manualFee = manualFee;
    }
}