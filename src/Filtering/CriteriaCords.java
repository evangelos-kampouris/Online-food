package Filtering;

import other.Coordinates;
import other.ProductCategory;
import other.Shop;

import java.util.HashSet;
import java.util.Iterator;
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
            System.out.println("receivedFilter: " + receivedFilter);//debug
            Coordinates selectedCoordinates = (Coordinates) receivedFilter.get(1);

            Iterator<Shop> iterator = shops.iterator();
            while (iterator.hasNext()) {
                Shop shop = iterator.next();
                double distance = Coordinates.haversine(
                        selectedCoordinates.getLatitude(),
                        selectedCoordinates.getLongitude(),
                        shop.getCoordinates().getLatitude(),
                        shop.getCoordinates().getLongitude()
                );
                if (distance > selectedDistance) {
                    System.out.println("Removes shop based on distance" + distance + ", shop: " + iterator);//debug
                    iterator.remove();
                }
            }
        }
        return shops;
    }
}
