import java.util.HashMap;
import java.util.Map;

public class ShopInventory extends Inventory{

    Map<String, Boolean> enable = new HashMap<>();
    Map<String, Integer> stock = new HashMap<>();

    @Override
    public void addProduct(String productName, Product product) {
        if(inventory.containsKey(productName)) {
            System.err.println("Product already exists");
        }
        else {
            inventory.put(productName, product);
            enable.put(productName, false);
            stock.put(productName, 0);
        }
    }

    public void addProduct(String productName, Product product, int quantity, boolean enabled) {
        if(inventory.containsKey(productName)) {
            System.err.println("Product already exists");;
        }
        else{
            inventory.put(productName, product);
            enable.put(productName, enabled);
            stock.put(productName, quantity);
        }

    }

    @Override
    public void removeProduct(String productName) {
        if(inventory.containsKey(productName)) {
            inventory.remove(productName);
            return;
        }
        System.err.println("Product does not exist");
    }


    public Map<String, Integer> getStock() {
        return stock;
    }

    public int getItemStock(String productName){
        if(inventory.containsKey(productName)) {
            return stock.get(productName);
        }
        System.err.println("Product does not exist.");
        return -1;
    }

    public void setStock(Map<String, Integer> stock) {
        this.stock = stock;
    }

    /**
     * @param productName
     * @param itemStock
     *
     * Edits the stock of an EXISTING product.
     */
    public void setItemStock(String productName, int itemStock) {
        if(inventory.containsKey(productName)) {
            stock.put(productName, itemStock);
            return;
        }
        System.err.println("Product does not exist.");
    }

    /**
     * @param productName
     * @param itemStock
     * Creates a new product stock entry.
     * Does nothing if the product already exists.
     */
    public void createItemStock(String productName, int itemStock) {
        if (!stock.containsKey(productName)) {
            stock.put(productName, itemStock);
        } else {
            System.err.println("Product already exists in inventory.");
        }
    }

    public Map<String, Boolean> getEnable() {
        return enable;
    }

    public boolean getItemEnableStatus(String productName) {
        if(enable.containsKey(productName)) {
            return enable.get(productName);
        }
        System.err.println("Product does not exist.");
        return false;
    }

    public void setEnable(Map<String, Boolean> enable) {
        this.enable = enable;
    }

    /**
     * @param productName
     * @param enable
     *
     * Edits the enable of an EXISTING product
     */
    public void setItemEnableStatus(String productName, boolean enable) {
        if(this.enable.containsKey(productName)) {
            this.enable.put(productName, enable);
        }
        else{
            System.err.println("Product does not exist.");
        }
    }

    /**
     * @param productName
     * @param enableStatus
     * Creates a new product stock entry.
     * Does nothing if the product already exists.
     */
    public void createItemEnableStatus(String productName, boolean enableStatus) {
        if (!this.enable.containsKey(productName)) {
            this.enable.put(productName, enableStatus);
        } else {
            System.err.println("Product already exists in enable map.");
        }
    }
}