package DTO;

import java.io.Serial;
import java.io.Serializable;

/**
 * A request DTO used to update the stock quantity of a product in a specific store.
 * Typically issued by a Manager to adjust inventory.
 */
public class ChangeStockDTO extends Request implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final String storeName;
    private final String productName;
    private final int newStock;

    /**
     * Constructs a request to change the stock of a product in a store.
     *
     * @param storeName the name of the store
     * @param productName the product whose stock will be updated
     * @param newStock the new quantity of the product
     */
    public ChangeStockDTO(String storeName, String productName, int newStock){
        this.storeName = storeName;
        this.productName = productName;
        this.newStock = newStock;
    }

    public String getStoreName() {
        return storeName;
    }

    public String getProductName() {
        return productName;
    }

    public int getNewStock() {
        return newStock;
    }
}
