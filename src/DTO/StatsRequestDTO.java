package DTO;

import java.io.Serializable;

public class StatsRequestDTO extends Request implements Serializable {

    private static final long serialVersionUID = 1L;

    private String type;                        // "store" ή "product"

    public StatsRequestDTO(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
