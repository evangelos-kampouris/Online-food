package Inventory;

import other.Product;
import java.util.Map;

public class InventoryCart extends Inventory {

    private float cost;

    @Override
    public void addProduct(String productName, Product product, int quantity) {
        if(inventory.containsKey(productName)) {
            System.err.println("Product already in the cart.");
            return;
        }
        InventoryItem item = new InventoryItem(product, quantity);
        inventory.put(productName, item);

        //TODO consdider whether the cart must be update using updateCost()
    }

    @Override
    public void removeProduct(String productName) {
        if (!isValidName(productName)) {
            System.err.println("Invalid Product Name");
            return;
        }
        if(!inventory.containsKey(productName)){
            System.err.println("Product not in cart");
            return;
        }
        inventory.remove(productName);
    }

    public void updateCost(){
        float sum = 0;
        for (Map.Entry<String, InventoryItem> entry : inventory.entrySet()) sum += entry.getValue().getProduct().getPrice();
        setCost(sum);
    }

    //GETTER AND SETTER
    public float getCost() {
        updateCost();
        return cost;
    }

    public void setCost(float cost) {
        this.cost = cost;
    }

}
