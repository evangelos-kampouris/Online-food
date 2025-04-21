package Filtering;

import other.*;

public class FilterPrice implements Filtering {
    Price price;

    public FilterPrice(Price price) {
        this.price = price;
    }

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
