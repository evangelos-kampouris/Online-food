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
    private Float ratingVar;

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

    /**
     * Constructs a Shop instance with all necessary attributes.
     *
     * @param name the name of the shop
     * @param catalog the shop's product catalog
     * @param rating the average rating of the shop
     * @param numberOfRatings the total number of ratings the shop has received
     * @param productCategory the food categories offered by the shop
     * @param storeCategory the type of store (e.g., Pizzeria, Burger Store)
     * @param coordinates the geographical location of the shop
     */
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

    /**
     * Calculates the average price of all products in the shop's catalog.
     *
     * @return the average price as a float
     */
    public float calculateAveragePrice(){
        List<Product> products = catalog.getAllProducts();
        float total_price = 0.0f;
        for (Product product : products) {
            total_price += product.getPrice();
        }
        return total_price/numberOfProducts;
    }

    /**
     * Adds the specified amount of revenue to the shop's total revenue.
     *
     * @param revenue the amount to add
     */
    public synchronized void addRevenue(float revenue){ this.revenue += revenue; }

    /**
     * Completes a sale using the given inventory cart.
     * Adds the cost of the cart to the shop's revenue and deducts stock for each item.
     *
     * @param cart the customer's shopping cart
     * @throws IllegalArgumentException if the cart is invalid
     * @throws NoValidStockInput if stock deduction fails due to invalid quantity
     */
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
     * Sets the shop's price category (LOW, MEDIUM, HIGH) based on average product price.
     *
     * @param averagePrice the calculated average price of the shop's products
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

    public void updateRating(float newRating){
        if (ratingVar == null) {
            ratingVar = rating.getValue();
        }
        ratingVar = (ratingVar * numberOfRatings + newRating) / (numberOfRatings + 1);
        numberOfRatings++;
        rating = Rating.fromValue(ratingVar);
    }

    @Override
    public String toString() {
        return  "{name='" + name + '\'' +
                ", productCategory=" + productCategory +
                ", storeCategory=" + storeCategory +
                ", numberOfRatings=" + numberOfRatings +
                ", rating=" + rating +
                ", coordinates=" + coordinates +
                ", logoPath='" + logoPath + '\'' +
                ", numberOfProducts=" + numberOfProducts +
                ", price=" + price +
                ", revenue=" + revenue +
                ", products=" + catalog.toString() +
                '}';
    }

    /**
     * Returns the price category of the shop.
     *
     * @return the price category enum
     */
    public Price getPrice(){
        return price;
    }

    /**
     * Prints the shop's price category description to the console.
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
