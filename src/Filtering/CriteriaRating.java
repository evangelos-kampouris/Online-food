package Filtering;

import other.Rating;
import other.Shop;

import java.util.Set;

public class CriteriaRating implements Criteria {

    Filtering filter;

    public CriteriaRating(Filtering filter){this.filter = filter;}

    @Override
    public Set<Shop> meetCriteria(Set<Shop> shops) {
        if(filter == null)
            throw new IllegalArgumentException("No filter Provided in the criteria.");
        if(filter instanceof FilterRating selected_filter){
            Rating rating = (Rating) selected_filter.getFilter();
            shops.removeIf(shop -> shop.getRating() != rating);
        }
        return shops;
    }
}
