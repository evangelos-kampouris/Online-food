package com.online.food.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages store images by mapping store names to online image URLs.
 * Uses high-quality food and restaurant images for better UX.
 */
public class StoreImageManager {
    
    private static final Map<String, String> STORE_IMAGE_MAP = new HashMap<>();
    
    // Default fallback image for unknown stores
    private static final String DEFAULT_RESTAURANT_IMAGE = 
            "https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=800&q=80";
    
    static {
        // Greek/Mediterranean restaurants
        STORE_IMAGE_MAP.put("Souvlaki Stou Psyrri", 
                "https://images.unsplash.com/photo-1529059997568-3d847b1154f0?w=800&q=80"); // Greek souvlaki
        
        STORE_IMAGE_MAP.put("Patras Souvlaki", 
                "https://images.unsplash.com/photo-1599487488170-d11ec9c172f0?w=800&q=80"); // Greek food
        
        // Pizza restaurants
        STORE_IMAGE_MAP.put("Athens Pizza Lovers", 
                "https://images.unsplash.com/photo-1513104890138-7c749659a591?w=800&q=80"); // Pizza
        
        // Burger restaurants
        STORE_IMAGE_MAP.put("Athens Burger House", 
                "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=800&q=80"); // Burger
        
        // Brunch/breakfast places
        STORE_IMAGE_MAP.put("Brunch Koukaki", 
                "https://images.unsplash.com/photo-1482049016688-2d3e1b311543?w=800&q=80"); // Brunch table
        
        // Coffee shops
        STORE_IMAGE_MAP.put("Coffee in Kolonaki", 
                "https://images.unsplash.com/photo-1501339847302-ac426a4a7cbb?w=800&q=80"); // Coffee shop
        
        // Dessert/crepes
        STORE_IMAGE_MAP.put("Thessaloniki Crepes", 
                "https://images.unsplash.com/photo-1506084868230-bb9d95c24759?w=800&q=80"); // Crepes
        
        STORE_IMAGE_MAP.put("Sweet Waffles Gazi", 
                "https://images.unsplash.com/photo-1578985545062-69928b1d9587?w=800&q=80"); // Waffles with berries
        
        // Sushi restaurants
        STORE_IMAGE_MAP.put("Sushi Akropolis", 
                "https://images.unsplash.com/photo-1579584425555-c3ce17fd4351?w=800&q=80"); // Sushi
        
        // Vegan restaurants
        STORE_IMAGE_MAP.put("Vegan Pie Exarcheia", 
                "https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=800&q=80"); // Healthy food
    }
    
    /**
     * Gets the image URL for a given store name.
     * Returns a default restaurant image if store is not found.
     * 
     * @param storeName the name of the store
     * @return the image URL for the store
     */
    public static String getStoreImageUrl(String storeName) {
        return STORE_IMAGE_MAP.getOrDefault(storeName, DEFAULT_RESTAURANT_IMAGE);
    }
} 