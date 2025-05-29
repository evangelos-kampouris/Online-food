package DTO;

import java.io.Serial;

/**
 * A request DTO used to remove (disable) a product from a shop.
 * Sent by the Manager to update the shop's inventory.
 */
public class RemoveProductDTO extends Request{
    @Serial
    private static final long serialVersionUID = 1L;
    private final String storeName;
    private final String productName;

    /**
     * Constructs a request to remove a product from the given store.
     *
     * @param storeName the name of the store
     * @param productName the product to be removed
     */
    public RemoveProductDTO(String storeName, String productName) {
        this.storeName = storeName;
        this.productName = productName;
    }

    public String getStoreName() {
        return storeName;
    }

    public String getProductName() {
        return productName;
    }
}
