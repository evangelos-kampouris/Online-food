package Filtering;

import other.Price;

import java.io.Serial;

/**
 * A filtering implementation based on shop pricing level.
 * Used to filter results according to the general cost of products in a shop.
 */
public class FilterPrice implements Filtering {
    @Serial
    private static final long serialVersionUID = 1L;
    Price price;

    /**
     * Creates a price filter with the selected price level.
     *
     * @param price the price level to filter by
     */
    public FilterPrice(Price price) {
        this.price = price;
    }

    /**
     * Returns the selected price level used for filtering.
     *
     * @return the price enum value
     */
    @Override
    public Object getFilter() {
        return price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FilterPrice that = (FilterPrice) o;
        return this.price == that.price;
    }

    @Override
    public int hashCode() {
        return price.hashCode();
    }
}
