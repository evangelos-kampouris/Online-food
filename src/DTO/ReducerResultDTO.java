package DTO;

import other.Shop;

import java.util.List;

//Needs to be sent back to the master, master will forward it to the client
public class ReducerResultDTO extends Request{
    private final List<Shop> results;

    public ReducerResultDTO(int requestID, List<Shop> results) {
        this.requestId = requestID;
        this.results = results;
    }

    public List<Shop> getResults() {
        return results;
    }
}
