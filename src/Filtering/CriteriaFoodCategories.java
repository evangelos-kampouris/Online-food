package Filtering;

import other.ProductCategory;
import other.Shop;

import java.util.Set;

public class CriteriaFoodCategories implements Criteria {

    Filtering filter;

    public CriteriaFoodCategories(Filtering filter){this.filter = filter;}
    @Override
    public Set<Shop> meetCriteria(Set<Shop> shops) {
        if(filter == null)
            throw new IllegalArgumentException("No filter Provided in the criteria.");
        if(filter instanceof FilterFoodCategory selected_filter){
            ProductCategory foodCategory = (ProductCategory) selected_filter.getFilter();
            shops.removeIf(shop -> !shop.getFoodCategories().contains(foodCategory));
        }
        return shops;
    }
}
