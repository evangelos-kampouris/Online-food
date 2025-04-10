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
}
