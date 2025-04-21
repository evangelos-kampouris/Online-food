package other;

public class Product {
    private int price;
    private String name;
    private ProductCategory productCategory;

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

    public ProductCategory getFoodCategory() {
        return productCategory;
    }

    public void setFoodCategory(ProductCategory foodCategory) {
        this.productCategory = foodCategory;
    }

    @Override
    public String toString(){
        return "Name: " + name + ", Price: " + price + ", Category: " + productCategory;
    }
}
