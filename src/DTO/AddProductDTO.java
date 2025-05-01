package DTO;

import other.ProductCategory;

import java.io.Serial;

/**
 * A request DTO used to add a new product to an existing shop.
 * Sent by the Manager to the MasterNode and then forwarded to the responsible WorkerNode.
 */
public class AddProductDTO extends Request{
    @Serial
    private static final long serialVersionUID = 1L;
    private final String storeName;
    private final String productName;
    private final ProductCategory productCategory;
    private final double price;
    private final int quantity;

    /**
     * Constructs a new AddProductDTO with full product details.
     *
     * @param storeName the name of the shop to which the product is added
     * @param productName the name of the product
     * @param productCategory the food category of the product
     * @param price the price of the product
     * @param quantity the available stock quantity
     */
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
