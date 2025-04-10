package Filtering;
import other.*;


public class FilterRating implements Filtering {
    Rating rating;

    public FilterRating(Rating rating) {
        this.rating = rating;
    }

    @Override
    public Object getFilter() {
        return rating;
    }
}
