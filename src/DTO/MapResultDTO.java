package DTO;

import other.Shop;

import java.io.Serial;
import java.util.Map;

public class MapResultDTO extends Request{
    @Serial
    private static final long serialVersionUID = 1L;
    private final Map<String, Shop> mapResult;

    public MapResultDTO(Map<String, Shop> mapResult, int requestId) {
        this.mapResult = mapResult;
        this.requestId = requestId;
    }

    public Map<String, Shop> getMapResult() {
        return mapResult;
    }
}
