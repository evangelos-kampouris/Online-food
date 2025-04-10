package Filtering;

import other.Shop;

import java.util.HashSet;
import java.util.Set;


public class CriteriaCords implements Criteria {
    @Override
    public Set<Shop> meetCriteria(Set<Shop> shops, Filtering filter) {
        return new HashSet<>(); //TODO
    }
}
