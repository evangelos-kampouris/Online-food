package Inventory;

import other.Product;

import java.io.Serial;
import java.util.HashMap;
import java.util.Map;

public class ShopInventory extends Inventory{
    @Serial
    private static final long serialVersionUID = 1L;

    public ShopInventory() {}

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
     * Adds a new product to the shop's inventory with explicit enabled status.
     * <p>
     * If the product already exists, a warning is printed and the method does not modify the inventory.
     * </p>
     *
     * @param productName the name of the product
     * @param product the product object to add
     * @param quantity the quantity to set initially; must be positive
     * @param enabled whether the product should be initially enabled
     * @throws IllegalArgumentException if input is invalid
     */
    public synchronized void addProduct(String productName, Product product, int quantity, boolean enabled) throws IllegalArgumentException {
        if (!isValidProductInput(productName, product, quantity))
            throw new IllegalArgumentException("Invalid Arguments. Given arguments are:\n\t Product" + product.toString() + ",\n\t Quantity:" + quantity);

        if(inventory.containsKey(productName)) {
            System.err.println("Product already exists");
        }
        else
            inventory.put(productName, new ShopInventoryItem(product, quantity, enabled));

    }

    /**
     * Returns whether a product in the inventory is enabled.
     *
     * @param productName the name of the product
     * @return true if the product is enabled, false otherwise
     */

    public synchronized boolean getItemEnableStatus(String productName) {
        if (!isValidName(productName)) {
            System.err.println("Invalid Product Name");
            return false;
        }

        InventoryItem item = inventory.get(productName);
        if (item == null)
            System.err.println("NULL value in inventory");

        if (item instanceof ShopInventoryItem) {
            return ((ShopInventoryItem) item).isEnabled();
        }

        System.err.println("Product is not a ShopInventoryItem.");
        return false;
    }

    /**
     * Sets the enabled status of an existing product in the inventory.
     *
     * @param productName the name of the product
     * @param enable true to enable the product, false to disable it
     */

    public synchronized void setItemEnableStatus(String productName, boolean enable) {
        if (!isValidName(productName))
            System.err.println("Invalid Product Name");

        InventoryItem item = inventory.get(productName);
        if (item == null)
            System.err.println("NULL value in inventory");

        if (item instanceof ShopInventoryItem) {
            ((ShopInventoryItem) item).setEnabled(enable);
        }

        else {
            System.err.println("Product is not a ShopInventoryItem.");
        }
    }
}