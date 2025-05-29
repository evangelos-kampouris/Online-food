package DTO;

import other.Stats;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

/**
 * A request DTO used by the Manager to request sales statistics.
 * Can request either store-level or product-level statistics.
 */
public class StatsRequestDTO extends Request implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private String type;                        // "store" ή "product"
    private String category;

    /**
     * Constructs a statistics request.
     *
     * @param type the type of statistics requested ("store" or "product")
     */
    public StatsRequestDTO(String type, String category) {
        this.type = type;
        this.category = category;
    }

    public String getType() {
        return type;
    }

    public String getCategory() {
        return category;
    }


}
