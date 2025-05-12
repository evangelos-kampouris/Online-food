package Inventory;
import other.Product;

import java.io.Serial;

public class ShopInventoryItem extends InventoryItem{
    @Serial
    private static final long serialVersionUID = 1L;
    private boolean enabled;

    /**
     * Creates a shop inventory item with default enabled status based on stock availability.
     *
     * The product is considered enabled only if quantity is greater than 0.
     *
     *
     * @param product the product to associate
     * @param quantity the initial stock quantity
     */
    public ShopInventoryItem(Product product, int quantity) {
        super(product, quantity);
        if(quantity > 0){
            enabled = true;
        }
    }

    /**
     * Creates a shop inventory item with explicit enabled status.
     *
     * @param product the product to associate
     * @param quantity the initial stock quantity
     * @param enabled true if the product should be marked as enabled
     * @throws IllegalArgumentException if quantity is non-positive but enabled is true
     */
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

    public synchronized void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
