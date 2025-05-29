package DTO;

import other.Rating;

import java.io.Serial;
import java.io.Serializable;

/**
 * A request DTO sent by a client to rate a specific store.
 * Contains the store name and the rating value.
 */
public class RateStoreRequestDTO extends Request implements Serializable{
    @Serial
    private static final long serialVersionUID = 1L;
    private String storeName;
    private float rating;

    /**
     * Constructs a store rating request.
     *
     * @param storeName the name of the store to rate
     * @param rating the rating to assign to the store
     */
    public RateStoreRequestDTO(String storeName, float rating) {
        this.storeName = storeName;
        this.rating = rating;
    }

    public String getStoreName() {
        return storeName;
    }

    public float getRating(){
        return rating;
    }
}
