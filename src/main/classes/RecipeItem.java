package main.classes;

//辅助类，配方明细，CutFlower*需求数量
public class RecipeItem {

    private CutFlower flower;
    private int requiredQuantity;

    public RecipeItem() {}

    public RecipeItem(CutFlower flower, int requiredQuantity) {
        this.flower = flower;
        this.requiredQuantity = requiredQuantity;
    }

    public CutFlower getFlower() {
        return flower;
    }

    public void setFlower(CutFlower flower) {
        this.flower = flower;
    }

    public int getRequiredQuantity() {
        return requiredQuantity;
    }

    public void setRequiredQuantity(int requiredQuantity) {
        this.requiredQuantity = requiredQuantity;
    }
}