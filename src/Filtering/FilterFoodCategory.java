package Filtering;

import other.StoreCategories;


public class FilterFoodCategory implements Filtering {
    StoreCategories selectedCategory;

    public FilterFoodCategory(StoreCategories selectedCategory) {
        this.selectedCategory = selectedCategory;
    }

    @Override
    public Object getFilter() {
        return selectedCategory;
    }
}
