package other;

import Exceptions.NoValidStockInput;
import Inventory.InventoryCart;
import Inventory.InventoryItem;
import Inventory.ShopInventory;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Shop implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private String name;
    private Set<ProductCategory> productCategory;
    private StoreCategories storeCategory;

    private int numberOfRatings;
    private Rating rating;

    private Coordinates coordinates;
    private String logoPath;

    private int numberOfProducts;
    private ShopInventory catalog;

    private Price price; //Store Expensiveness
    //product sales
    private float revenue = 0.0f;



    private static final int PRICE_LOW_TOP_LIMIT = 5;
    private static final int PRICE_MEDIUM_TOP_LIMIT = 10;
    private static final int PRICE_HIGH_TOP_LIMIT = 15;



    public Shop() {}//Just the default.

    public Shop(String name, ShopInventory catalog, Rating rating, int numberOfRatings, Set<ProductCategory> productCategory, StoreCategories storeCategory, Coordinates coordinates) {
        this.name = name;
        this.storeCategory = storeCategory;
        this.numberOfRatings = numberOfRatings;
        this.productCategory = productCategory;
        this.catalog = catalog;
        this.rating = rating;
        this.coordinates = coordinates;

        setPrice(calculateAveragePrice());
    }

    public float calculateAveragePrice(){
        List<Product> products = catalog.getAllProducts();
        float total_price = 0.0f;
        for (Product product : products) {
            total_price += product.getPrice();
        }
        return total_price/numberOfProducts;
    }

    public synchronized void addRevenue(float revenue){ this.revenue += revenue; }

    public synchronized void sell(InventoryCart cart) throws IllegalArgumentException, NoValidStockInput {
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

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

}
