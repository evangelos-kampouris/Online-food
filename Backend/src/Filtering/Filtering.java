package Filtering;

import java.io.Serial;
import java.io.Serializable;

/**
 * Base interface for all filtering types used in shop search operations.
 * Each filter implementation defines how filtering values are retrieved.
 */
public interface Filtering extends Serializable {
    @Serial
    static final long serialVersionUID = 1L;
    /**
     * Returns the value(s) used by this filter for comparison during search.
     *
     * @return the filter criteria value
     */
    public Object getFilter();
}
