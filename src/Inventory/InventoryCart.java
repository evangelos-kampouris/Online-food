package Inventory;

import java.util.Map;

public class InventoryCart extends Inventory {

    private float cost;

    @Override
    public void addProduct(String productName, Product product) {
        if(inventory.containsKey(productName)) {
            System.err.println("Product already in the cart.");
            return;
        }
        inventory.put(productName, product);
    }

    @Override
    public void removeProduct(String productName) {
        if(inventory.containsKey(productName)) {
            inventory.remove(productName);
            return;
        }
        System.err.println("Product not in the cart.");
    }

    public void updateCost(){
        float sum = 0;
        for (Map.Entry<String, Product> entry : inventory.entrySet()) sum += entry.getValue().getPrice();
        setCost(sum);
    }

    //GETTER AND SETTER
    public float getCost() {
        return cost;
    }

    public void setCost(float cost) {
        this.cost = cost;
    }

}
