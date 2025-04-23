package Filtering;

import other.Coordinates;
import other.ProductCategory;
import other.Shop;

import java.util.HashSet;
import java.util.List;
import java.util.Set;



public class CriteriaCords implements Criteria {

    Filtering filter;

    public CriteriaCords(Filtering filter){this.filter = filter;}

    @Override
    public Set<Shop> meetCriteria(Set<Shop> shops) {
        if(filter == null)
            throw new IllegalArgumentException("No filter Provided in the criteria.");
        if(filter instanceof FilterCords selected_filter){
            List<Object> receivedFilter = (List<Object>) selected_filter.getFilter();
            float selectedDistance = (float) receivedFilter.get(0);
            Coordinates selectedCoordinates = (Coordinates) receivedFilter.get(1);

            for(Shop shop : shops){
                double distance = Coordinates.haversine(selectedCoordinates.getLatitude(),selectedCoordinates.getLongitude(), shop.getCoordinates().getLatitude(), shop.getCoordinates().getLongitude());
                if(distance > selectedDistance){
                    shops.remove(shop);
                }
            }
        }
        return shops;
    }
}
