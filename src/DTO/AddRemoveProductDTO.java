package DTO;

import java.io.Serializable;

public class AddRemoveProductDTO extends Request implements Serializable {

    private String storeName;
    private String productName;
    private String action;                  //θα δέχετε είτε "add" ή "remove"
                                            //διόρθωσε το σε enum

    //Αυτα θα χρησιμοποιηθούν μόνο όταν το action = "add"
    private String productCategory;
    private double price;

    public AddRemoveProductDTO(String store, String productName, String action, String productCategory, double price) {
        this.storeName = storeName;
        this.productName = productName;
        this.action = action;
        this.productCategory = productCategory;
        this.price = price;
    }

    public String getStoreName() {
        return storeName;
    }

    public String getProductName() {
        return productName;
    }

    public String getAction() {
        return action;
    }

    public String getProductCategory() {
        return productCategory;
    }

    public double getPrice() {
        return price;
    }
}
