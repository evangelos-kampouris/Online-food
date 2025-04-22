package Filtering;

import other.Shop;

import java.util.HashSet;
import java.util.Set;

public class CriteriaOR implements Criteria{
    private final Criteria criteriaFirst, criteriaSecond;

    public CriteriaOR(Criteria criteriaFirst, Criteria criteriaSecond) {
        this.criteriaFirst = criteriaFirst;
        this.criteriaSecond = criteriaSecond;
    }

    @Override
    public Set<Shop> meetCriteria(Set<Shop> shops) {
        Set<Shop> result = new HashSet<>(criteriaFirst.meetCriteria(shops));
        result.addAll(criteriaSecond.meetCriteria(shops));
        return result;
    }
}
