package Inventory;

import other.Product;

import java.io.Serial;
import java.util.Map;


public class InventoryCart extends Inventory {
    @Serial
    private static final long serialVersionUID = 1L;
    private float cost;
    /**
     * Adds a product to the cart with the specified quantity.
     * <p>
     * If the product already exists in the cart, its quantity is increased accordingly.
     * </p>
     *
     * @param productName the product name key
     * @param product the product object
     * @param quantity the quantity to add; must be greater than 0
     * @throws IllegalArgumentException if quantity is non-positive or product is null
     */
    @Override
    public synchronized void addProduct(String productName, Product product, int quantity) {

        if(inventory.containsKey(productName)) {
            System.out.println("Product already in the cart - Adjusting quantity");
            InventoryItem inventoryItem = inventory.get(productName);
            inventoryItem.setQuantity(inventoryItem.getQuantity() + quantity);
        }
        else {
            InventoryItem item = new InventoryItem(product, quantity);
            inventory.put(productName, item);
        }
        //TODO consider whether the cart must be update using updateCost()
    }

    /**
     * Recalculates the total cost of items in the cart.
     * <p>
     * This method sums the price of all products currently in the inventory and updates the internal cost field.
     * </p>
     */
    public synchronized void updateCost(){
        float sum = 0;
        for (Map.Entry<String, InventoryItem> entry : inventory.entrySet()) sum += entry.getValue().getProduct().getPrice() * entry.getValue().getQuantity();
        setCost(sum);
    }
    /**
     * Calculates the total cost of all products currently in the cart.
     *
     * @return the total cost as a float value
     */
    //GETTER AND SETTER
    public float getCost() {
        updateCost();
        return cost;
    }

    public synchronized int cartQuantity(){
        int cartQuantity = 0;
        for (Map.Entry<String, InventoryItem> entry : inventory.entrySet())
            cartQuantity += entry.getValue().getQuantity();

        return cartQuantity;
    }

    public synchronized void setCost(float cost) {
        this.cost = cost;
    }

}
