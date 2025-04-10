package Inventory;
import other.Product;

public class ShopInventoryItem extends InventoryItem{
    private boolean enabled;

    public ShopInventoryItem(Product product, int quantity) {
        super(product, quantity);
        if(quantity > 0){
            enabled = true;
        }
    }
    public ShopInventoryItem(Product product, int quantity, boolean enabled) {
        super(product, quantity);
        if(quantity <= 0 && enabled){
            throw new IllegalArgumentException("Stock <= 0 but enabled is true");
        }
        this.enabled = enabled;
    }


    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
