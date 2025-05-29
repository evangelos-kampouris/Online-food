package Filtering;

import other.Shop;

import java.util.Set;

/**
 * Represents a filtering strategy applied to a set of shops.
 * Implementations define specific logic for selecting relevant shops.
 */
public interface Criteria {

    /**
     * Applies the criteria to the provided set of shops and returns a filtered subset.
     *
     * @param shops the initial set of shops
     * @return a subset of shops that meet the criteria
     */
    public Set<Shop> meetCriteria(Set<Shop> shops);
}