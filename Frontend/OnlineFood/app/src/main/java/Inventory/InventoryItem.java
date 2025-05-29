package Inventory;

import other.Product;

import java.io.Serial;
import java.io.Serializable;

/**
 * Represents an item in an inventory, containing a product and its quantity.
 * Base class for different types of inventory items with specific behaviors.
 */
public class InventoryItem implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    
    private Product product;
    private int quantity;

    /**
     * Default constructor for serialization.
     */
    public InventoryItem() {}

    /**
     * Constructs an inventory item with a product and quantity.
     *
     * @param product the product to store
     * @param quantity the initial quantity
     */
    public InventoryItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    /**
     * Copy constructor for creating defensive copies.
     *
     * @param other the inventory item to copy
     */
    public InventoryItem(InventoryItem other) {
        this.product = other.product;
        this.quantity = other.quantity;
    }

    // Getters and setters
    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "InventoryItem{" +
                "product=" + product +
                ", quantity=" + quantity +
                '}';
    }
} 