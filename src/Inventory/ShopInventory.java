package Inventory;

import other.*;

public class ShopInventory extends Inventory{


    @Override
    public void addProduct(String productName, Product product, int quantity) {

        if (!isValidProductInput(productName, product, quantity))
            throw new IllegalArgumentException("Invalid Arguments. Given arguments are:\n\t Product" + product.toString() + ",\n\t Quantity:" + quantity);

        if(inventory.containsKey(productName)) {
            System.err.println("Product already exists");
        }
        else
            inventory.put(productName, new ShopInventoryItem(product, quantity));
    }

    public void addProduct(String productName, Product product, int quantity, boolean enabled) {
        if (!isValidProductInput(productName, product, quantity))
            throw new IllegalArgumentException("Invalid Arguments. Given arguments are:\n\t Product" + product.toString() + ",\n\t Quantity:" + quantity);

        if(inventory.containsKey(productName)) {
            System.err.println("Product already exists");
        }
        else
            inventory.put(productName, new ShopInventoryItem(product, quantity, enabled));

    }

    @Override
    public void removeProduct(String productName) {
        if (!isValidName(productName)) {
            System.err.println("Invalid Product Name");
            return;
        }

        if(! inventory.containsKey(productName)){
            System.err.println("Product does not exist");
            return;
        }
        inventory.remove(productName);
    }

    public boolean getItemEnableStatus(String productName) {
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
     * @param productName
     * @param enable
     *
     * Edits enable of an EXISTING product
     * */
    public void setItemEnableStatus(String productName, boolean enable) {
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