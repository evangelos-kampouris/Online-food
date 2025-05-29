package Inventory;

import other.Product;
import other.ProductCategory;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;

/**
 * Abstract base class for inventory management systems.
 * Provides common operations for storing and managing products with quantities.
 */
public abstract class Inventory implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    
    protected Map<String, InventoryItem> inventory = new HashMap<>();

    /**
     * Default constructor.
     */
    public Inventory() {}

    /**
     * Copy constructor for creating defensive copies.
     *
     * @param other the inventory to copy
     */
    public Inventory(Inventory other) {
        this.inventory = new HashMap<>(other.inventory);
    }

    /**
     * Abstract method for adding products to the inventory.
     * Implementation varies based on inventory type.
     *
     * @param productName the name of the product
     * @param product the product object
     * @param quantity the quantity to add
     */
    public abstract void addProduct(String productName, Product product, int quantity);

    /**
     * Removes a product completely from the inventory.
     *
     * @param productName the name of the product to remove
     * @throws IllegalArgumentException if product name is invalid or product not found
     */
    public void removeProductCompletely(String productName) throws IllegalArgumentException {
        if (productName == null || !isValidName(productName)) {
            throw new IllegalArgumentException("Invalid product name.");
        }
        
        if (!inventory.containsKey(productName)) {
            throw new IllegalArgumentException("Product not found in inventory.");
        }
        
        inventory.remove(productName);
    }

    /**
     * Clears all items from the inventory.
     */
    public void clearInventory() {
        inventory.clear();
    }

    /**
     * Returns a defensive copy of the inventory map.
     *
     * @return unmodifiable view of the inventory
     */
    public Map<String, InventoryItem> getInventory() {
        return new HashMap<>(inventory);
    }

    /**
     * Returns a list of all products in the inventory.
     *
     * @return list of all products
     */
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        
        for (InventoryItem item : inventory.values()) {
            if (item != null && item.getProduct() != null) {
                products.add(item.getProduct());
            }
        }
        
        return products;
    }

    /**
     * Generates a formatted string representation of the inventory.
     *
     * @return formatted inventory listing
     */
    public String printListProducts() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n========== CURRENT INVENTORY ==========\n\n");

        if (inventory.isEmpty()) {
            sb.append("Inventory is empty.\n");
            return sb.toString();
        }

        // Header
        sb.append(String.format("%-25s %-15s %-10s %-20s\n", 
                "Product Name", "Category", "Quantity", "Price (€)"));
        sb.append("-----------------------------------------------------------------------\n");

        for (InventoryItem item : inventory.values()) {
            if (item != null && item.getProduct() != null) {
                Product product = item.getProduct();
                
                String name = product.getName() != null ? product.getName() : "Unknown";
                String category = product.getFoodCategory() != null ? 
                    product.getFoodCategory().toString() : "Unknown";
                int quantity = item.getQuantity();
                float price = product.getPrice();

                sb.append(String.format("%-25s %-15s %-10d %-20.2f\n", 
                        name, category, quantity, price));
            }
        }

        sb.append("\n=======================================\n");
        return sb.toString();
    }

    /**
     * Validates product input parameters.
     *
     * @param productName the product name to validate
     * @param product the product object to validate
     * @param quantity the quantity to validate
     * @return true if all inputs are valid
     */
    protected boolean isValidProductInput(String productName, Product product, int quantity) {
        return isValidName(productName) && product != null && quantity > 0;
    }

    /**
     * Validates a product name.
     *
     * @param productName the name to validate
     * @return true if the name is valid
     */
    protected boolean isValidName(String productName) {
        return productName != null && !productName.trim().isEmpty();
    }

    @Override
    public String toString() {
        return printListProducts();
    }
} 