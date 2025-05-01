package DTO;

import other.Shop;

import java.io.Serial;
import java.io.Serializable;

public class AddStoreRequestDTO extends Request implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private Shop shop;

    public AddStoreRequestDTO(Shop shop) {
        this.shop = shop;
    }

    public Shop getShop() {
        return shop;
    }

}
