package Filtering;

import other.Shop;

import java.util.Set;

public interface Criteria {
    public Set<Shop> meetCriteria(Set<Shop> shops);
}