package other;

import java.io.Serial;
import java.io.Serializable;

public class Product implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private float price;
    private String name;
    private ProductCategory productCategory;

    /**
     * Constructs a product with a given name, category, and price.
     *
     * @param name the product name
     * @param productcategory the food category of the product
     * @param price the price of the product
     */
    public Product(String name, ProductCategory productcategory, double price) {
        this.name = name;
        this.productCategory = productcategory;
        this.price = (float) price;

    }
    public float getPrice() {
        return price;
    }

    public synchronized void setPrice(float price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public synchronized void setName(String name) {
        this.name = name;
    }

    public ProductCategory getFoodCategory() {
        return productCategory;
    }

    public synchronized void setFoodCategory(ProductCategory foodCategory) {
        this.productCategory = foodCategory;
    }

    @Override
    public String toString(){
        return "Name: " + name + ", Price: " + price + ", Category: " + productCategory;
    }
}
