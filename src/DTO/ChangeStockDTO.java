package DTO;

import java.io.Serializable;

public class ChangeStockDTO extends Request implements Serializable {

    private String storeName;
    private String productName;
    private int newStock;

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
