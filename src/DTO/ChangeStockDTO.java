package DTO;

import java.io.Serial;
import java.io.Serializable;

public class ChangeStockDTO extends Request implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final String storeName;
    private final String productName;
    private final int newStock;

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
