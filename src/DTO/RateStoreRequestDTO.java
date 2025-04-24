package DTO;

import java.io.Serializable;

public class RateStoreRequestDTO extends Request implements Serializable{

    private String storeName;
    private int stars;

    public RateStoreRequestDTO(String storeName, int stars) {
        this.storeName = storeName;
        this.stars = stars;
    }

    public String getStoreName() {
        return storeName;
    }

    public int getStars(){
        return stars;
    }
}
