package DTO;

import other.Shop;

import java.io.Serial;
import java.util.Map;

/**
 * A DTO sent from a WorkerNode to the ReducerNode containing filtered shop results.
 * Includes the corresponding request ID to support reduction.
 */
public class MapResultDTO extends Request{
    @Serial
    private static final long serialVersionUID = 1L;
    private final Map<String, Shop> mapResult;

    /**
     * Constructs a result wrapper with filtered shops and a request ID.
     *
     * @param mapResult the filtered shops mapped by name
     * @param requestId the request this result corresponds to
     */
    public MapResultDTO(Map<String, Shop> mapResult, int requestId) {
        this.mapResult = mapResult;
        this.requestId = requestId;
    }

    public Map<String, Shop> getMapResult() {
        return mapResult;
    }
}
