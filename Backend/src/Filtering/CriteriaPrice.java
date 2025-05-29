package Filtering;

import other.Price;
import other.Shop;

import java.util.Set;

/**
 * A criteria implementation that filters shops by pricing level.
 * Removes shops that do not match the specified price category.
 */
public class CriteriaPrice implements Criteria {
    Filtering filter;

    /**
     * Creates a price-based criteria using a FilterPrice filter.
     *
     * @param filter the price filter to apply
     */
    public CriteriaPrice(Filtering filter){this.filter = filter;}

    /**
     * Applies the price filter to a set of shops.
     * Removes shops whose price category does not match the selected value.
     *
     * @param shops the initial set of shops
     * @return the filtered set of shops with matching price level
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
        if(filter instanceof FilterPrice selected_filter){
            Price price = (Price) selected_filter.getFilter();
            shops.removeIf(shop -> shop.getPrice() != price);
        }
        return shops;
    }
}
