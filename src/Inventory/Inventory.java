package Inventory;

import Exceptions.NoValidStockInput;
import other.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;

public abstract class Inventory implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    Map<String, InventoryItem> inventory = new HashMap<String, InventoryItem>(); // Selected DataStructure <name, product>

    public abstract void addProduct(String productName, Product product, int quantity);

    /**
     * Removes a specified quantity of a product from the inventory.
     * <p>
     * If the quantity to remove is not specified (i.e., {@code null}), it defaults to 1.
     * If the quantity equals the current stock, the product is removed entirely from the inventory. //TODO CHECK FOR SHOPINVENTORY NO REMOVAL STOCK EQUALS JUST TO ZERO.
     * If the quantity is less than the current stock, the quantity is reduced accordingly.
     * </p>
     *
     * @param productName the name of the product to remove; must be valid and exist in the inventory
     * @param quantity the number of units to remove; if {@code null}, defaults to 1
     * @throws IllegalArgumentException if the product name is invalid or the product is not found in inventory
     * @throws NoValidStockInput if the quantity is less than or equal to 0, or greater than the available stock
     */
    public void removeProduct(String productName, Integer quantity) throws NoValidStockInput, IllegalArgumentException {
        // Validate product name
        if (productName == null || !isValidName(productName)) {
            throw new IllegalArgumentException("Invalid product name.");
        }
        // Check if product exists in inventory
        if (!inventory.containsKey(productName)) {
            throw new IllegalArgumentException("Product not found in inventory.");
        }

        InventoryItem item = inventory.get(productName);
        int existingQuantity = item.getQuantity();

        // Default to removing 1 if quantity is not provided
        int quantityToReduce = (quantity != null) ? quantity : 1;

        // Check if quantity to remove is valid
        if (quantityToReduce <= 0) {
            throw new NoValidStockInput("Quantity must be greater than 0.");
        }

        if (quantityToReduce > existingQuantity) {
            throw new NoValidStockInput("Quantity exceeds the available stock.");
        }

        // Remove or update quantity
        if (quantityToReduce == existingQuantity) {
            inventory.remove(productName);
        } else {
            item.setQuantity(existingQuantity - quantityToReduce);
        }
    }

    /**
     * Removes a product completely from the Inventory.
     * @param productName
     * @throws IllegalArgumentException
     */
    public void removeProductCompletely(String productName) throws IllegalArgumentException {
        // Validate product name
        if (productName == null || !isValidName(productName)) {
            throw new IllegalArgumentException("Invalid product name.");
        }
        // Check if product exists in inventory
        if (!inventory.containsKey(productName)) {
            throw new IllegalArgumentException("Product not found in inventory.");
        }
        inventory.remove(productName);
    }

    /**
     * Clears the inventory
     */
    public void clearInventory() {
        inventory.clear();
    }
    public Map<String, InventoryItem> getInventory() {
        return inventory;
    }

    public List<InventoryItem> getInventoryItems() {
        return new ArrayList<InventoryItem>(inventory.values());
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

    /**
     * @return A set of the all the product categories of the cart.
     */
    public Set<ProductCategory> getProductCategories(){
        Set<ProductCategory> productCategories = new HashSet<ProductCategory>();

        for (Map.Entry<String, InventoryItem> entry : inventory.entrySet()) {
            InventoryItem item = entry.getValue();
            productCategories.add(item.getProduct().getFoodCategory());
        }
        return productCategories;
    }

    public void printListProducts() {
        for (Map.Entry<String, InventoryItem> entry : inventory.entrySet()) {
            System.out.println(entry.getValue().getProduct().toString());
        }
    }


    public Product getProduct(String productName) {
        return inventory.get(productName).getProduct();
    }

    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        for (Map.Entry<String, InventoryItem> entry : inventory.entrySet()) {
            products.add(entry.getValue().getProduct());
        }
        return products;
    }

    /**
     * @param productName
     * @param itemStock
     *
     * Edits the stock of an EXISTING product.
     */
    public void changeStock(String productName, int itemStock) {
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
