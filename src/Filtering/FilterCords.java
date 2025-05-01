package Filtering;
import other.Coordinates;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true; // same reference
        if (o == null || getClass() != o.getClass()) return false; // null or different class
        FilterCords that = (FilterCords) o;
        return Float.compare(that.distance, distance) == 0 &&
                Objects.equals(userCoordinates, that.userCoordinates);
    }

    @Override
    public int hashCode() {
        return Objects.hash(distance, userCoordinates);
    }


}
