package DTO;

import other.Shop;

import java.io.Serial;
import java.util.List;

/**
 * A DTO used by the ReducerNode to return the final aggregated shop results to the MasterNode.
 * The Master will then forward these results to the original client.
 */
//Needs to be sent back to the master, master will forward it to the client
public class ReducerResultDTO extends Request{
    @Serial
    private static final long serialVersionUID = 1L;
    private final List<Shop> results;

    /**
     * Constructs a reducer result object with a request ID and the list of final matching shops.
     *
     * @param requestID the ID of the original client search request
     * @param results the final aggregated list of shops
     */
    public ReducerResultDTO(int requestID, List<Shop> results) {
        this.requestId = requestID;
        this.results = results;
    }

    public List<Shop> getResults() {
        return results;
    }

    public String toString() {
        return "Request ID: " + requestId + " Results " + results;
    }
}
