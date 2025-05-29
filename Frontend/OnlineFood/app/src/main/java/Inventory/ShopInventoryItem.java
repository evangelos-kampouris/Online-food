package Inventory;
import other.Product;

import java.io.Serial;

/**
 * Represents an inventory item specific to shops, extending the base InventoryItem
 * with an enabled/disabled state for controlling product availability.
 */
public class ShopInventoryItem extends InventoryItem {
    @Serial
    private static final long serialVersionUID = 1L;
    
    private boolean enabled;


    /**
     * Constructs a shop inventory item with default enabled state.
     *
     * @param product the product to store
     * @param quantity the initial quantity
     */
    public ShopInventoryItem(Product product, int quantity) {
        super(product, quantity);
        this.enabled = true; // Default to enabled
    }

    @Override
    public String toString() {
        return "ShopInventoryItem{" +
                "product=" + getProduct() +
                ", quantity=" + getQuantity() +
                ", enabled=" + enabled +
                '}';
    }
} 