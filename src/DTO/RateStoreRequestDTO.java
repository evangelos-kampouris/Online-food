package DTO;

import other.Rating;

import java.io.Serial;
import java.io.Serializable;

public class RateStoreRequestDTO extends Request implements Serializable{
    @Serial
    private static final long serialVersionUID = 1L;
    private String storeName;
    private Rating rating;

    public RateStoreRequestDTO(String storeName, Rating rating) {
        this.storeName = storeName;
        this.rating = rating;
    }

    public String getStoreName() {
        return storeName;
    }

    public Rating getRating(){
        return rating;
    }
}
