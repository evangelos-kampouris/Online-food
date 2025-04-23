package DTO;

import other.Shop;

import java.util.Map;

public class MapResultDTO extends Request{

    private final Map<String, Shop> mapResult;

    public MapResultDTO(Map<String, Shop> mapResult, int requestId) {
        this.mapResult = mapResult;
        this.requestId = requestId;
    }

    public Map<String, Shop> getMapResult() {
        return mapResult;
    }
}
