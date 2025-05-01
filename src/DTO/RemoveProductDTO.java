package DTO;

import java.io.Serial;

public class RemoveProductDTO extends Request{
    @Serial
    private static final long serialVersionUID = 1L;
    private final String storeName;
    private final String productName;

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
