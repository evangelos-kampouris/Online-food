package Filtering;

import other.Price;

import java.io.Serial;

/**
 * Implements the Filtering interface for price-based filtering.
 */
public class FilterPrice implements Filtering {
    @Serial
    private static final long serialVersionUID = 1L;
    
    private Price price;

    public FilterPrice(Price price) {
        this.price = price;
    }
    
    @Override
    public Object getFilter() {
        return price;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        FilterPrice that = (FilterPrice) obj;
        return this.price == that.price;
    }
    
    @Override
    public int hashCode() {
        return price.hashCode();
    }
    
    @Override
    public String toString() {
        return "FilterPrice{price=" + price + "}";
    }
} 