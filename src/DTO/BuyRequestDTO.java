package DTO;

import Inventory.InventoryCart;
import other.Shop;

import java.io.Serial;
//O client για να κανει buy θα μαζευει οτι δεδομενα χρειαζεται σε ενα αντικειμενο buyrequestDTO
//και θα στελνει το buyrequestDTO στον master

/**
 * A request DTO used by the client to initiate a purchase.
 * Contains both the selected shop and the cart contents.
 * Sent from the client to the MasterNode for processing.
 */
public class BuyRequestDTO extends Request{
    @Serial
    private static final long serialVersionUID = 1L;
    private final InventoryCart cart;
    private Shop shop;

    /**
     * Constructs a new buy request containing the shop and the products in the cart.
     *
     * @param shop the shop from which the user wants to buy
     * @param cart the cart with the selected products
     */
    public BuyRequestDTO(Shop shop, InventoryCart cart) {
        super();
        this.cart = cart;
        this.shop = shop;
    }

    public InventoryCart getCart() {return cart;}

    public Shop getShop() {return shop;}

    public void setShop(Shop shop) {this.shop = shop;}

}
