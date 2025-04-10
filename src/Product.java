public class Product {
    private int price;
    private String name;
    private FoodCategories foodCategory;

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public FoodCategories getFoodCategory() {
        return foodCategory;
    }

    public void setFoodCategory(FoodCategories foodCategory) {
        this.foodCategory = foodCategory;
    }
}
