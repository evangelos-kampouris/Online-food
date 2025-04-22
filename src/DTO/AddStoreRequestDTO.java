package DTO;

import java.io.Serializable;
import other.Shop;

public class AddStoreRequestDTO extends Request implements Serializable {

    private Shop shop;

    public AddStoreRequestDTO(Shop shop) {
        this.shop = shop;
    }

    public Shop getShop() {
        return shop;
    }

}
