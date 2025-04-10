package Filtering;

import other.*;

import java.util.HashSet;
import java.util.Set;

public class CriteriaRating implements Criteria {

    @Override
    public Set<Shop> meetCriteria(Set<Shop> shops, Filtering filter) {
        Set<Shop> ratingMeetCriteria = new HashSet<>();

        if(filter instanceof FilterRating selected_filter){
            Rating rating = (Rating) selected_filter.getFilter();
            for (Shop shop : shops) {
                if(shop.getRating() == rating){
                    ratingMeetCriteria.add(shop);
                }
            }
        }
        return ratingMeetCriteria;
    }
}
