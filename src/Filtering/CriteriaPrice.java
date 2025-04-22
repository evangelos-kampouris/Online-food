package Filtering;

import other.Price;
import other.Shop;

import java.util.HashSet;
import java.util.Set;

public class CriteriaPrice implements Criteria {
    Filtering filter;

    public CriteriaPrice(Filtering filter){this.filter = filter;}

    @Override
    public Set<Shop> meetCriteria(Set<Shop> shops) {
        if(filter == null)
            throw new IllegalArgumentException("No filter Provided in the criteria.");
        if(filter instanceof FilterPrice selected_filter){
            Price price = (Price) selected_filter.getFilter();
            shops.removeIf(shop -> shop.getPrice() != price);
        }
        return shops;
    }
}
