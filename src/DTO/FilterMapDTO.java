package DTO;

import Filtering.Filtering;

import java.io.Serial;
import java.util.List;

/**
 * A request DTO sent from the MasterNode to a WorkerNode containing filter criteria.
 * Used to apply filtering logic on the worker's local shop data.
 */
public class FilterMapDTO extends Request{
    List<Filtering> selectedFilters;
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a filter map request with the given list of filters.
     *
     * @param selectedFilters the filters to apply on shops
     */
    public FilterMapDTO(List<Filtering> selectedFilters) {
        super();
        this.selectedFilters = selectedFilters;
    }

    /**
     * Constructs a filter map request with filters and a predefined request ID.
     *
     * @param selectedFilters the filters to apply
     * @param requestId the unique identifier for the request
     */
    public FilterMapDTO(List<Filtering> selectedFilters, int requestId) {
        this.selectedFilters = selectedFilters;
        this.requestId = requestId;
    }

    public List<Filtering> getSelectedFilters() {
        return selectedFilters;
    }
}
