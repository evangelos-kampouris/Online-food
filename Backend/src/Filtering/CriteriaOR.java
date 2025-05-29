package Filtering;

import other.Shop;

import java.util.HashSet;
import java.util.Set;

/**
 * Combines two filtering criteria using logical OR.
 * A shop is included if it satisfies either of the two criteria.
 */
public class CriteriaOR implements Criteria{
    private final Criteria criteriaFirst, criteriaSecond;

    /**
     * Creates an OR-composite criteria with two individual criteria.
     *
     * @param criteriaFirst the first filtering criteria
     * @param criteriaSecond the second filtering criteria
     */
    public CriteriaOR(Criteria criteriaFirst, Criteria criteriaSecond) {
        this.criteriaFirst = criteriaFirst;
        this.criteriaSecond = criteriaSecond;
    }

    /**
     * Applies both criteria to the set of shops and returns the union of the results.
     *
     * @param shops the set of shops to filter
     * @return the union of shops that satisfy at least one criteria
     */
    @Override
    public Set<Shop> meetCriteria(Set<Shop> shops) {
        Set<Shop> result = new HashSet<>(criteriaFirst.meetCriteria(shops));
        result.addAll(criteriaSecond.meetCriteria(shops));
        return result;
    }
}
