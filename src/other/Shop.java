package other;

import Inventory.*;

import java.util.Map;
import java.util.Set;

public class Shop {

    private String name;
    private Set<ProductCategory> productCategory;
    private StoreCategories storeCategory;

    private int numberOfRatings;
    private Rating rating;
    private Price price;

    //Missing products data
    private ShopInventory catalog;

    //product sales
    private float revenue;

    private static final int PRICE_LOW_TOP_LIMIT = 5;
    private static final int PRICE_MEDIUM_TOP_LIMIT = 10;
    private static final int PRICE_HIGH_TOP_LIMIT = 15;

    public synchronized void addRevenue(float revenue){ this.revenue += revenue; }

    public synchronized void sell(InventoryCart cart){
        //Add the revenue from the cart
        float profit = cart.getCost();
        addRevenue(profit);

        //remove stock
        for (Map.Entry<String, InventoryItem> entry : cart.getInventory().entrySet()) {
            catalog.removeProduct(entry.getKey(), entry.getValue().getQuantity());
        }
    }

    /*
     * |---------------- SETTERS AND GETTERS ---------------------|
     */

    //CATEGORIES SET AND GET
    public Set<ProductCategory> getFoodCategories() {
        return productCategory;
    }

    public void setFoodCategories(Set<ProductCategory> productCategory) {
        this.productCategory = productCategory;
    }

    // PRICES SET AND GET
    /**
     * @param averagePrice
     *
     * Sets the shops price based on the Price enum.
     */
    public void setPrice(float averagePrice){
        if(averagePrice < PRICE_LOW_TOP_LIMIT){
            price = Price.LOW;
        }
        else if (averagePrice < PRICE_MEDIUM_TOP_LIMIT) {
            price = Price.MEDIUM;
        }
        else{price = Price.HIGH; }
    }

    /**
     * @return price
     *
     * Returns the Price enum
     */
    public Price getPrice(){
        return price;
    }

    /**
     * Prints the Shops Price categorization as such: "The prices are: Cheap/Medium/Expensive".
     */
    public void printPrice(){
        System.out.println("The prices are: " + price.getDescription());
    }

    //RATING SETTER AND GETTER
    public Rating getRating() {
        return rating;
    }

    public void setRating(Rating rating) {
        this.rating = rating;
    }

    //CATALOG SETTER AND GETTER
    public ShopInventory getCatalog() {return catalog;}

    public void setCatalog(ShopInventory catalog) {this.catalog = catalog;}

    //NAME SETTER GETTERS
    public String getName() {return name;}

    public void setName(String name) {this.name = name;}

    public float getRevenue() {
        return revenue;
    }

    public void setRevenue(float revenue) {
        this.revenue = revenue;
    }

    public StoreCategories getStoreCategory() {
        return storeCategory;
    }

    public void setStoreCategory(StoreCategories storeCategory) {
        this.storeCategory = storeCategory;
    }


}
