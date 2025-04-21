package Inventory;

import other.Product;
import java.util.Map;


public class InventoryCart extends Inventory {

    private float cost;

    @Override
    public void addProduct(String productName, Product product, int quantity) {
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
