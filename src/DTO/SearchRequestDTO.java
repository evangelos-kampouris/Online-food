package DTO;

import Filtering.Filtering;
import java.util.List;

//Client to Master
public class SearchRequestDTO extends Request{

    List<Filtering> selectedFilters;

    public SearchRequestDTO(List<Filtering> selectedFilters) {
        super();
        this.selectedFilters = selectedFilters;
    }

    public List<Filtering> getSelectedFilters() {
        return selectedFilters;
    }

}


