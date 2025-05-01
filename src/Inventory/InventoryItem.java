package Inventory;

import other.Product;

import java.io.Serial;
import java.io.Serializable;


public class InventoryItem implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private Product product;
    private int quantity;

    /**
     * Constructs an inventory item with a given product and quantity.
     *
     * @param product the product to associate with this inventory item
     * @param quantity the initial quantity available
     */
    public InventoryItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

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
}
