package other;

import Inventory.ShopInventory;

import java.util.Set;

public class Shop {

    private String name;
    private Set<FoodCategories> foodCategories;

    private int numberOfRatings;
    private Rating rating;
    private Price price;

    //Missing products data
    private ShopInventory catalog;

    //product sales
    private int revenue;

    private static final int PRICE_LOW_TOP_LIMIT = 5;
    private static final int PRICE_MEDIUM_TOP_LIMIT = 10;
    private static final int PRICE_HIGH_TOP_LIMIT = 15;












    //CATEGORIES SET AND GET
    public Set<FoodCategories> getFoodCategories() {
        return foodCategories;
    }

    public void setFoodCategories(Set<FoodCategories> foodCategories) {
        this.foodCategories = foodCategories;
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
}
