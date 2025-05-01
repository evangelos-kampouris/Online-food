package DTO;

import other.Shop;

import java.io.Serial;
import java.io.Serializable;

/**
 * A request DTO used to register a new shop in the system.
 * Sent by the Manager and routed through the Master to the appropriate Worker.
 */
public class AddStoreRequestDTO extends Request implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private Shop shop;

    /**
     * Constructs a new request to add the specified shop.
     *
     * @param shop the shop to be added to the system
     */
    public AddStoreRequestDTO(Shop shop) {
        this.shop = shop;
    }

    public Shop getShop() {
        return shop;
    }

}
