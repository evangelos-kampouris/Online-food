package DTO;
import Inventory.*;
import other.Shop;
//O client για να κανει buy θα μαζευει οτι δεδομενα χρειαζεται σε ενα αντικειμενο buyrequestDTO
//και θα στελνει το buyrequestDTO στον master

/**
 * Holds the Buy Request logic. Client -> Master
 */
public class BuyRequestDTO extends Request{

    private final InventoryCart cart;
    private Shop shop;

    public BuyRequestDTO(Shop shop, InventoryCart cart) {
        super();
        this.cart = cart;
        this.shop = shop;
    }

    public InventoryCart getCart() {return cart;}

    public Shop getShop() {return shop;}

    public void setShop(Shop shop) {this.shop = shop;}

}
