package Filtering;
import other.Coordinates;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

public class FilterCords implements Filtering {
    @Serial
    private static final long serialVersionUID = 1L;
    float distance;
    Coordinates userCoordinates;

    public FilterCords(float distance, Coordinates userCoards) {
        this.distance = distance;
        this.userCoordinates = userCoards;
    }


    @Override
    public Object getFilter() {
        List<Object> filter = new ArrayList<>();
        filter.add(distance);
        filter.add(userCoordinates);
        return filter;
    }


}
