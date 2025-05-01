package Filtering;

import other.Rating;

import java.io.Serial;

/**
 * A filtering implementation based on shop rating.
 * Used to return only shops with a specific average user rating.
 */
public class FilterRating implements Filtering {
    @Serial
    private static final long serialVersionUID = 1L;
    Rating rating;

    /**
     * Creates a rating filter using the specified rating level.
     *
     * @param rating the minimum required shop rating
     */
    public FilterRating(Rating rating) {
        this.rating = rating;
    }

    /**
     * Returns the selected rating used for filtering.
     *
     * @return the rating enum value
     */
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
