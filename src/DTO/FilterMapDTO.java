package DTO;

import Filtering.Filtering;

import java.util.List;

public class FilterMapDTO extends Request{
    List<Filtering> selectedFilters;

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
