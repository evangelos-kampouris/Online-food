package DTO;

import other.ProductCategory;

import java.io.Serial;

public class AddProductDTO extends Request{
    @Serial
    private static final long serialVersionUID = 1L;
    private final String storeName;
    private final String productName;
    private final ProductCategory productCategory;
    private final double price;
    private final int quantity;

    public AddProductDTO(String storeName, String productName, ProductCategory productCategory, double price, int quantity) {
        this.storeName = storeName;
        this.productName = productName;
        this.productCategory = productCategory;
        this.price = price;
        this.quantity = quantity;
    }

    public String getStoreName() {
        return storeName;
    }

    public String getProductName() {
        return productName;
    }

    public ProductCategory getProductCategory() {
        return productCategory;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }
}
