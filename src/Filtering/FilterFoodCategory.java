package Filtering;

public class FilterFoodCategory implements Filtering {
    FoodCategories selectedCategory;

    public FilterFoodCategory(FoodCategories selectedCategory) {
        this.selectedCategory = selectedCategory;
    }

    @Override
    public Object getFilter() {
        return selectedCategory;
    }
}
