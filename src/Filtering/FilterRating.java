package Filtering;
import other.*;

import java.io.Serial;


public class FilterRating implements Filtering {
    @Serial
    private static final long serialVersionUID = 1L;
    Rating rating;

    public FilterRating(Rating rating) {
        this.rating = rating;
    }

    @Override
    public Object getFilter() {
        return rating;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        FilterRating that = (FilterRating) obj;
        return this.rating == that.rating;
    }

    @Override
    public int hashCode() {
        return rating.hashCode();
    }

}
