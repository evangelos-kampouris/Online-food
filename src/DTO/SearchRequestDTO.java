package DTO;

import Filtering.Filtering;

import java.io.Serial;
import java.util.List;

//Client to Master
public class SearchRequestDTO extends Request{
    @Serial
    private static final long serialVersionUID = 1L;
    List<Filtering> selectedFilters;

    public SearchRequestDTO(List<Filtering> selectedFilters) {
        super();
        this.selectedFilters = selectedFilters;
    }

    public List<Filtering> getSelectedFilters() {
        return selectedFilters;
    }

}


