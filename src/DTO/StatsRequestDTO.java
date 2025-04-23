package DTO;

import java.io.Serializable;

public class StatsRequestDTO extends Request implements Serializable {

    private String type;                        // "store" ή "product"

    public StatsRequestDTO(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

}
