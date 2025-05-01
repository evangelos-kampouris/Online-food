package Filtering;

import java.io.Serial;
import java.io.Serializable;

public interface Filtering extends Serializable {
    @Serial
    static final long serialVersionUID = 1L;
    public Object getFilter();
}
