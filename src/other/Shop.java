package other;

import Exceptions.NoValidStockInput;
import Inventory.Inventory;
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
     * Copy constructor. Creates a new Shop with the same data as the given one.
     * Performs defensive copies of mutable fields to avoid shared references.
     *
     * @param other the Shop to copy
     */
    public Shop(Shop other) {
        this.name               = other.name;
        this.productCategory    = new java.util.HashSet<>(other.productCategory);
        this.storeCategory      = other.storeCategory;
        this.numberOfRatings    = other.numberOfRatings;
        this.rating             = other.rating;
        this.ratingVar          = other.ratingVar;
        this.coordinates        = other.coordinates != null ? new Coordinates(other.coordinates.getLatitude(), other.coordinates.getLongitude()) : null;
        this.logoPath           = other.logoPath;
        this.numberOfProducts   = other.numberOfProducts;
        this.catalog            = other.catalog != null ? new ShopInventory(other.catalog) : new ShopInventory();
        this.price              = other.price;
        this.revenue            = other.revenue;
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
        //remove stock
        for (Map.Entry<String, InventoryItem> entry : cart.getInventory().entrySet()) {
            catalog.removeProduct(entry.getKey(), entry.getValue().getQuantity());
        }
        //Add the revenue from the cart
        float profit = cart.getCost();
        addRevenue(profit);
    }


    /**
     * Adds a product to the catalog with the specified name, quantity, and enabled status.
     * Also updates the product category list and recalculates the average price.
     *
     * @param productName the name of the product to add
     * @param product the {@link Product} object containing product details
     * @param quantity the number of units to add to the catalog
     * @param enabled a flag indicating whether the product is enabled or active
     */
    public synchronized void addProduct(String productName, Product product, int quantity, boolean enabled) {
        catalog.addProduct(productName, product, quantity, enabled);
        productCategory.add(product.getFoodCategory());
        setPrice(calculateAveragePrice());
    }

    /*
     * |---------------- SETTERS AND GETTERS ---------------------|
     */

    //CATEGORIES SET AND GET
    public Set<ProductCategory> getFoodCategories() {
        return productCategory;
    }

    public synchronized void setFoodCategories(Set<ProductCategory> productCategory) {
        this.productCategory = productCategory;
    }

    // PRICES SET AND GET
    /**
     * Sets the shop's price category (LOW, MEDIUM, HIGH) based on average product price.
     *
     * @param averagePrice the calculated average price of the shop's products
     */
    public synchronized void setPrice(float averagePrice){
        if(averagePrice < PRICE_LOW_TOP_LIMIT){
            price = Price.LOW;
        }
        else if (averagePrice < PRICE_MEDIUM_TOP_LIMIT) {
            price = Price.MEDIUM;
        }
        else{price = Price.HIGH; }
    }

    public synchronized void updateRating(float newRating){
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
                ",RatingVar=" + ratingVar +
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

    public synchronized void setRating(Rating rating) {
        this.rating = rating;
    }

    //CATALOG SETTER AND GETTER
    public ShopInventory getCatalog() {return catalog;}

    public synchronized void setCatalog(ShopInventory catalog) {this.catalog = catalog;}

    //NAME SETTER GETTERS
    public String getName() {return name;}

    public synchronized void setName(String name) {this.name = name;}

    public float getRevenue() {
        return revenue;
    }

    public synchronized void setRevenue(float revenue) {
        this.revenue = revenue;
    }

    public StoreCategories getStoreCategory() {
        return storeCategory;
    }

    public synchronized void setStoreCategory(StoreCategories storeCategory) {
        this.storeCategory = storeCategory;
    }

    public  Coordinates getCoordinates() {
        return coordinates;
    }

    public synchronized void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

}
