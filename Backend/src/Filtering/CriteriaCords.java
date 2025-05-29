package Filtering;

import other.Coordinates;
import other.Shop;

import java.util.Iterator;
import java.util.List;
import java.util.Set;


/**
 * A criteria implementation that filters shops based on geographic distance.
 * Uses the Haversine formula to remove shops outside the specified radius.
 */
public class CriteriaCords implements Criteria {

    Filtering filter;

    /**
     * Constructs a distance-based criteria using a FilterCords filter.
     *
     * @param filter the coordinate-based filter
     */
    public CriteriaCords(Filtering filter){this.filter = filter;}

    /**
     * Applies the distance filter to a set of shops.
     * Removes shops that are farther than the allowed radius from the reference coordinates.
     *
     * @param shops the initial set of shops
     * @return the filtered set containing only nearby shops
     */
    @Override
    public Set<Shop> meetCriteria(Set<Shop> shops) {
        if(filter == null) {
            System.out.println("No filter Provided in the criteria.");
            return shops;
        }
        if(shops.isEmpty()) {
            System.out.println("No Shops  to filter.");
            return shops;
        }
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
