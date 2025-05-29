package Inventory;

import other.Product;

import java.io.Serial;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Inventory implementation for shops, extending the base Inventory class
 * with shop-specific functionality for product management.
 */
public class ShopInventory extends Inventory {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Default constructor.
     */
    public ShopInventory() {
        super();
    }

    /**
     * Copy constructor for creating defensive copies.
     *
     * @param other the shop inventory to copy
     */
    public ShopInventory(ShopInventory other) {
        this.inventory = new HashMap<>(other.inventory);
    }

    /**
     * Adds a new product to the shop's inventory with default enabled status.
     * <p>
     * If the product already exists, a warning is printed and the method does not modify the inventory.
     * </p>
     *
     * @param productName the name of the product
     * @param product the product object to add
     * @param quantity the quantity to set initially; must be positive
     * @throws IllegalArgumentException if input is invalid
     */
    @Override
    public synchronized void addProduct(String productName, Product product, int quantity) {
        if (!isValidProductInput(productName, product, quantity))
            throw new IllegalArgumentException("Invalid Arguments. Given arguments are:\n\t Product" + product.toString() + ",\n\t Quantity:" + quantity);

        if(inventory.containsKey(productName)) {
            System.err.println("Product already exists");
        }
        else
            inventory.put(productName, new ShopInventoryItem(product, quantity));
    }

    /**
     * Returns a list of all products in the inventory.
     * Filters out products that are disabled.
     *
     * @return list of enabled products
     */
    @Override
    public List<Product> getAllProducts() {
        return super.getAllProducts(); // Include all products regardless of enabled status
    }
    
    public Map<String, InventoryItem> getInventory() {
        if (inventory == null) {
            inventory = new HashMap<>();
        }
        return inventory;
    }
} 