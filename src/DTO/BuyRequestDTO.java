package DTO;
import Inventory.*;
//O client για να κανει buy θα μαζευει οτι δεδομενα χρειαζεται σε ενα αντικειμενο buyrequestDTO
//και θα στελνει το buyrequestDTO στον master

/**
 * Holds the Buy Request logic. Client -> Master
 */
public class BuyRequestDTO extends Request{
    InventoryCart cart;

    public BuyRequestDTO(InventoryCart cart) {
        this.cart = cart;
    }


}
