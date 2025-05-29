package Filtering;
import other.ProductCategory;

import java.io.Serial;

/**
 * A filtering implementation based on product food category.
 * Used to restrict results to shops that offer a specific type of food.
 */
public class FilterFoodCategory implements Filtering {
    @Serial
    private static final long serialVersionUID = 1L;
    ProductCategory selectedCategory;

    /**
     * Creates a food category filter.
     *
     * @param selectedCategory the food category to filter by
     */
    public FilterFoodCategory(ProductCategory selectedCategory) {
        this.selectedCategory = selectedCategory;
    }

    /**
     * Returns the selected food category used for filtering.
     *
     * @return the food category
     */
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
