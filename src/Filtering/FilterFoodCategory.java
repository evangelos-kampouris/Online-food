package Filtering;

import other.ProductCategory;


public class FilterFoodCategory implements Filtering {
    ProductCategory selectedCategory;

    public FilterFoodCategory(ProductCategory selectedCategory) {
        this.selectedCategory = selectedCategory;
    }

    @Override
    public Object getFilter() {
        return selectedCategory;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        FilterFoodCategory that = (FilterFoodCategory) obj;
        return this.selectedCategory == that.selectedCategory;
    }

    @Override
    public int hashCode() {
        return selectedCategory.hashCode();
    }
}
