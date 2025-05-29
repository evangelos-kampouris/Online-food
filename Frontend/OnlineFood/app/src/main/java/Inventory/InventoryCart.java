package Inventory;

import other.Product;

import java.io.Serial;
import java.util.Map;

/**
 * Inventory implementation for shopping carts, extending the base Inventory class
 * with cart-specific functionality for customer purchases.
 */
public class InventoryCart extends Inventory {
    @Serial
    private static final long serialVersionUID = 1L;
    private float cost;
    
    /**
     * Default constructor.
     */
    public InventoryCart() {
        super();
    }

    /**
     * Adds a product to the shopping cart with the specified quantity.
     *
     * @param productName the name of the product
     * @param product the product object to add
     * @param quantity the quantity to add
     * @throws IllegalArgumentException if input is invalid
     */
    @Override
    public synchronized void addProduct(String productName, Product product, int quantity) {
        if (!isValidProductInput(productName, product, quantity)) {
            throw new IllegalArgumentException("Invalid product input for cart");
        }

        if (inventory.containsKey(productName)) {
            // Update existing quantity
            InventoryItem existingItem = inventory.get(productName);
            int newQuantity = existingItem.getQuantity() + quantity;
            existingItem.setQuantity(newQuantity);
        } else {
            // Add new item
            inventory.put(productName, new InventoryItem(product, quantity));
        }
    }

    /**
     * Recalculates the total cost of items in the cart.
     * <p>
     * This method sums the price of all products currently in the inventory and updates the internal cost field.
     * </p>
     */
    public void updateCost(){
        float sum = 0;
        for (Map.Entry<String, InventoryItem> entry : inventory.entrySet()) sum += entry.getValue().getProduct().getPrice() * entry.getValue().getQuantity();
        setCost(sum);
    }
    
    /**
     * Calculates the total cost of all items in the cart.
     *
     * @return the total cost as a float
     */
    public float getCost() {
        updateCost();
        return cost;
    }

    /**
     * Returns the number of items in the cart.
     *
     * @return the size of the cart
     */
    public int size() {
        return inventory.size();
    }

    public void setCost(float cost) {
        this.cost = cost;
    }
} 