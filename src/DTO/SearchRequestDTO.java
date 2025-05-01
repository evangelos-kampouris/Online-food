package DTO;

import Filtering.Filtering;

import java.io.Serial;
import java.util.List;

//Client to Master
/**
 * A request DTO used by the client to initiate a shop search.
 * Contains a list of filters to apply during the search process.
 */
public class SearchRequestDTO extends Request{
    @Serial
    private static final long serialVersionUID = 1L;
    List<Filtering> selectedFilters;

    /**
     * Constructs a search request with a list of selected filters.
     *
     * @param selectedFilters the filters to apply in the search
     */
    public SearchRequestDTO(List<Filtering> selectedFilters) {
        super();
        this.selectedFilters = selectedFilters;
    }

    public List<Filtering> getSelectedFilters() {
        return selectedFilters;
    }

}


