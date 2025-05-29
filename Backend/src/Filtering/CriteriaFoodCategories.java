package Filtering;

import other.ProductCategory;
import other.Shop;

import java.util.Set;

/**
 * A criteria implementation that filters shops based on their offered food categories.
 * Removes any shop that does not include the specified category in its product offerings.
 */
public class CriteriaFoodCategories implements Criteria {

    Filtering filter;

    /**
     * Constructs a criteria object using a food category filter.
     *
     * @param filter the food category filter to apply
     */
    public CriteriaFoodCategories(Filtering filter){this.filter = filter;}

    /**
     * Applies the food category filter to a set of shops.
     * Removes shops that do not contain the specified product category.
     *
     * @param shops the initial set of shops to filter
     * @return the filtered set containing only relevant shops
     */
    @Override
    public Set<Shop> meetCriteria(Set<Shop> shops) {
        if(filter == null) {
            System.out.println("No filter Provided in the criteria.");
            return shops;
        }
        if(shops.isEmpty()) {
            System.out.println("No Shops  to filter.");
            return shops;
        }
        if(filter instanceof FilterFoodCategory selected_filter){
            ProductCategory foodCategory = (ProductCategory) selected_filter.getFilter();
            shops.removeIf(shop -> !shop.getFoodCategories().contains(foodCategory));
        }
        return shops;
    }
}
