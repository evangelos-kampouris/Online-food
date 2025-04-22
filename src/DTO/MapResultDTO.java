package DTO;

import other.Shop;

import java.util.Map;

public class MapResultDTO extends Request{
    Map<String, Shop> mapResult;

    public MapResultDTO(Map<String, Shop> mapResult, int requestId) {
        this.mapResult = mapResult;
        this.requestId = requestId;
    }
}
