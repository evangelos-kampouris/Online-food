package DTO;

import Filtering.Filtering;

import java.io.Serial;
import java.util.List;

public class FilterMapDTO extends Request{
    List<Filtering> selectedFilters;
    @Serial
    private static final long serialVersionUID = 1L;
    public FilterMapDTO(List<Filtering> selectedFilters) {
        super();
        this.selectedFilters = selectedFilters;
    }

    public FilterMapDTO(List<Filtering> selectedFilters, int requestId) {
        this.selectedFilters = selectedFilters;
        this.requestId = requestId;
    }

    public List<Filtering> getSelectedFilters() {
        return selectedFilters;
    }
}
