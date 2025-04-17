package Inventory;

import other.*;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public abstract class Inventory implements Serializable {

    Map<String, InventoryItem> inventory = new HashMap<String, InventoryItem>(); // Selected DataStructure <name, product>

    public abstract void addProduct(String productName, Product product, int quantity);

    public abstract void removeProduct(String productName);

    public Map<String, InventoryItem> getInventory() {
        return inventory;
    }

    public int getItemQuantity(String productName){
        if (!isValidName(productName)) {
            System.err.println("Invalid Product Name.");
            return -1;
        }

        if(! inventory.containsKey(productName)) {
            System.err.println("Product does not exist.");
            return -1;
        }

        InventoryItem item = inventory.get(productName);
        if (item == null) {
            System.err.println("NULL value in inventory.");
            return -1;
        }
        return item.getQuantity();
    }

    public void listProducts() {
        for (Map.Entry<String, InventoryItem> entry : inventory.entrySet()) {
            System.out.println(entry.getValue().getProduct().toString());
        }
    }

    public Product getProduct(String productName) {
        return inventory.get(productName).getProduct();
    }

    /**
     * @param productName
     * @param itemStock
     *
     * Edits the stock of an EXISTING product.
     */
    public void setItemStock(String productName, int itemStock) {
        if (!isValidName(productName)) return;

        if (itemStock < 0) {
            System.err.println("Stock cannot be negative.");
            return;
        }

        InventoryItem item = inventory.get(productName);
        if (item != null) {
            item.setQuantity(itemStock);
        } else {
            System.err.println("NULL value in inventory");
        }
    }



    // ====== PRIVATE HELPERS FOR VALUE CHECKING ======
    protected boolean isValidProductInput(String productName, Product product, int quantity) {
        if (!isValidName(productName)) return false;

        if (product == null) {
            System.err.println("Product cannot be null.");
            return false;
        }

        if (quantity < 0) {
            System.err.println("Quantity cannot be negative.");
            return false;
        }

        return true;
    }

    protected boolean isValidName(String productName) {
        if (productName == null) {
            System.err.println("Product name cannot be null.");
            return false;
        }
        if (productName.trim().isEmpty()) {
            System.err.println("Product name cannot be empty.");
            return false;
        }
        return true;
    }
}
