package Filtering;

import other.Rating;
import other.Shop;

import java.util.Set;

/**
 * A criteria implementation that filters shops by their average rating.
 * Removes shops that do not exactly match the selected rating level.
 */
public class CriteriaRating implements Criteria {

    Filtering filter;

    /**
     * Constructs a rating-based criteria using a FilterRating filter.
     *
     * @param filter the rating filter to apply
     */
    public CriteriaRating(Filtering filter){this.filter = filter;}

    /**
     * Applies the rating filter to a set of shops.
     * Removes shops that do not have the exact specified rating.
     *
     * @param shops the initial set of shops
     * @return the filtered set of shops matching the rating
     */
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
