package Filtering;

import other.StoreCategories;
import other.Shop;

import java.util.HashSet;
import java.util.Set;

public class CriteriaStoreCategories implements Criteria {

    @Override
    public Set<Shop> meetCriteria(Set<Shop> shops, Filtering filter) {
        Set<Shop> shopMeetCriteria = new HashSet<>();

        if(filter instanceof FilterFoodCategory selected_filter){
            StoreCategories foodCategory = (StoreCategories) selected_filter.getFilter();
            for (Shop shop : shops) {
                if(shop.getFoodCategories().contains(foodCategory)){
                    shopMeetCriteria.add(shop);
                }
            }
        }
        return shopMeetCriteria;
    }
}
