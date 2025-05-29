package other;

import Inventory.ShopInventory;

import java.io.Serial;
import java.io.Serializable;
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

    private Price price;
    //product sales
    private float revenue;


    private static final int PRICE_LOW_TOP_LIMIT = 5;
    private static final int PRICE_MEDIUM_TOP_LIMIT = 10;

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

    //RATING SETTER AND GETTER
    public Rating getRating() {
        return rating;
    }

    public void setRating(Rating rating) {
        this.rating = rating;
    }

    //CATALOG SETTER AND GETTER
    public ShopInventory getCatalog() {return catalog;}

    //NAME SETTER GETTERS
    public String getName() {return name;}

    public void setName(String name) {this.name = name;}

    public StoreCategories getStoreCategory() {
        return storeCategory;
    }

}