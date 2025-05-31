package com.online.food.network;

import android.util.Log;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import DTO.SearchRequestDTO;
import DTO.BuyRequestDTO;
import DTO.RateStoreRequestDTO;
import DTO.ReducerResultDTO;
import Responses.ResponseDTO;
import Inventory.InventoryCart;
import other.Product;
import Filtering.Filtering;
import Filtering.FilterCords;
import other.Shop;
import other.Coordinates;

/**
 * NetworkManager handles communication with the distributed food ordering server.
 * Singleton implementation ensures single connection point for all network operations.
 */
public class NetworkManager {

    private static final String TAG = "NetworkManager";
    
    // Network configuration constants
    private static final String DEFAULT_SERVER_IP = "10.0.2.2";  //Android emulator localhost
    private static final int DEFAULT_MASTER_PORT = 9000;
    private static final int SOCKET_TIMEOUT_MS = 10000;
    private static final float DEFAULT_SEARCH_RADIUS_KM = 5.0f;
    
    // Singleton instance
    private static NetworkManager instance;

    // Connection state
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private boolean isConnected = false;
    
    // User data and local cache
    private Coordinates userCoordinates;
    private final Map<String, Shop> shops = new HashMap<>(); //Κρατάει τα καταστήματα που έλαβε από τον server
    private final InventoryCart cart = new InventoryCart(); //Το καλάθι του χρήστη

    // Private constructor for singleton
    private NetworkManager() {}

    /**
     * Get singleton instance
     */
    public static synchronized NetworkManager getInstance() {
        if (instance == null) {
            instance = new NetworkManager();
        }
        return instance;
    }
    
    /**
     * Establish connection to Master server and perform initial 5km search
     */
    public boolean establishConnection(String serverIP, int serverPort, Coordinates coordinates) {
        if (coordinates == null) {
            Log.e(TAG, "User coordinates cannot be null");
            return false;
        }
        
        this.userCoordinates = coordinates;
        
        try {
            Log.d(TAG, "Connecting to Master at " + serverIP + ":" + serverPort);
            
            socket = new Socket(serverIP, serverPort);
            socket.setSoTimeout(SOCKET_TIMEOUT_MS);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            isConnected = true;
            
            Log.d(TAG, "Connected to server, performing auto 5km search...");
            
            //Αυτόματη αναζήτηση στα 5km after connection
            performAutoSearch();
            
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error establishing connection", e);
            isConnected = false;
            return false;
        }
    }

    /**
     * Close connection and cleanup resources
     */
    public void disconnect() {
        isConnected = false;
        
        try {
            if (out != null) {
                out.close();
                out = null;
            }
            if (in != null) {
                in.close();
                in = null;
    }
            if (socket != null && !socket.isClosed()) {
                socket.close();
                socket = null;
            }
            Log.d(TAG, "Disconnected from server");
        } catch (IOException e) {
            Log.e(TAG, "Error during disconnection", e);
        }
    }
    
    /**
     * Perform initial 5km search on connection
     */
    private void performAutoSearch() {
        try {
            //Δημιουργία φίλτρου 5km radius
            FilterCords km5Filter = new FilterCords(DEFAULT_SEARCH_RADIUS_KM, userCoordinates);
            
            logSearchParameters(km5Filter);
            
            List<Filtering> filters = new ArrayList<>();
            filters.add(km5Filter);
            
            //Aναζήτηση και ενημέρωση cache
            List<Shop> receivedShops = performSearch(filters);
            updateLocalShopsCache(receivedShops);
            
        } catch (Exception e) {
            Log.e(TAG, "Error in auto search", e);
        }
    }
    
    /**
     * Execute search request with filters
     */
    public List<Shop> performSearch(List<Filtering> filters) throws IOException, ClassNotFoundException {
        if (filters == null) {
            Log.w(TAG, "Filters list is null, using empty list");
            filters = new ArrayList<>();
        }
        
        SearchRequestDTO searchRequestDTO = new SearchRequestDTO(filters);
        List<Shop> receivedShops = null;
        
        //Δημιουργία νέας σύνδεσης για αναζήτηση
        Socket searchSocket = null;
        ObjectOutputStream searchOut = null;
        ObjectInputStream searchIn = null;
        
        try {
            searchSocket = createSearchSocket();
            searchOut = new ObjectOutputStream(searchSocket.getOutputStream());
            searchIn = new ObjectInputStream(searchSocket.getInputStream());
            
            //Request & Response
            searchOut.writeObject(searchRequestDTO);
            searchOut.flush();
            
            receivedShops = processSearchResponse(searchIn);
            updateLocalShopsCache(receivedShops);
            
        } finally {
            closeSearchConnection(searchOut, searchIn, searchSocket);
        }
        
        return receivedShops;
    }
    
    /**
     * Submit purchase request for selected shop
     */
    public boolean performPurchase(Shop selectedShop) {
        if (selectedShop == null) {
            Log.e(TAG, "Selected shop cannot be null");
            return false;
        }
        
        if (cart.getInventory().isEmpty()) {
            Log.w(TAG, "Cart is empty, cannot perform purchase");
            return false;
        }
        
        try {
            BuyRequestDTO buyRequestDTO = new BuyRequestDTO(selectedShop, cart);

            //Νέα σύνδεση για αγορά
            Socket purchaseSocket = null;
            ObjectOutputStream purchaseOut = null;
            ObjectInputStream purchaseIn = null;
            
            try {
                purchaseSocket = createPurchaseSocket();
                purchaseOut = new ObjectOutputStream(purchaseSocket.getOutputStream());
                purchaseIn = new ObjectInputStream(purchaseSocket.getInputStream());
                
                purchaseOut.writeObject(buyRequestDTO);
                purchaseOut.flush();
                
                boolean success = processPurchaseResponse(purchaseIn);
                
                // Clear cart and refresh shop data after successful purchase
                if (success) {
                    cart.clearInventory();
                    Log.d(TAG, "Cart cleared after successful purchase");
                    
                    // Refresh shop data to get updated stock quantities
                    try {
                        Log.d(TAG, "Refreshing shop data to get updated stock quantities...");
                        List<Filtering> filters = new ArrayList<>();
                        filters.add(new FilterCords(DEFAULT_SEARCH_RADIUS_KM, userCoordinates));
                        
                        List<Shop> updatedShops = performSearch(filters);
                        if (updatedShops != null) {
                            Log.d(TAG, "Successfully refreshed " + updatedShops.size() + " shops with updated stock");
                        }
                    } catch (Exception e) {
                        Log.w(TAG, "Failed to refresh shop data after purchase", e);
                        // Don't fail the purchase if refresh fails
                    }
                }
                
                return success;
                
            } finally {
                closePurchaseConnection(purchaseOut, purchaseIn, purchaseSocket);
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error during purchase", e);
            return false;
        }
    }
    
    /**
     * Submit store rating
     */
    public boolean performRating(String storeName, float rating) {
        if (storeName == null || storeName.trim().isEmpty()) {
            Log.e(TAG, "Store name cannot be null or empty");
            return false;
        }
        
        if (rating < 0 || rating > 5) {
            Log.e(TAG, "Rating must be between 0 and 5");
            return false;
        }
        
        Log.d(TAG, "=== RATING DEBUG START ===");
        Log.d(TAG, "Attempting to rate store: " + storeName + " with rating: " + rating);
        
        //Καταγραφή τρέχουσας βαθμολογίας
        Shop currentShop = shops.get(storeName);
        if (currentShop != null && currentShop.getRating() != null) {
            Log.d(TAG, "Current rating before submission: " + currentShop.getRating().getValue());
        } else {
            Log.d(TAG, "Current shop not found in local cache or has no rating");
        }
        
        try {
            RateStoreRequestDTO rateStoreRequestDTO = new RateStoreRequestDTO(storeName, rating);
            Log.d(TAG, "Sending rating request to server...");
            Object response = sendAndReceiveRequest(rateStoreRequestDTO);
            Log.d(TAG, "Received response from server: " + response);
            
            if (response instanceof ResponseDTO<?>) {
                ResponseDTO<?> responseDTO = (ResponseDTO<?>) response;
                boolean success = responseDTO.isSuccess();
                Log.d(TAG, "Rating submission success: " + success);
                Log.d(TAG, "Response message: " + responseDTO.getMessage());
                Log.d(TAG, "Response data type: " + (responseDTO.getData() != null ? responseDTO.getData().getClass().getSimpleName() : "null"));
                
                if (success && responseDTO.getData() instanceof Shop) {
                    //Ενημέρωση local cache με την server response
                    Shop updatedShop = (Shop) responseDTO.getData();
                    Log.d(TAG, "Updated shop received from server:");
                    Log.d(TAG, "  - Name: " + updatedShop.getName());
                    Log.d(TAG, "  - New rating: " + (updatedShop.getRating() != null ? updatedShop.getRating().getValue() : "null"));
                    Log.d(TAG, "  - Rating enum: " + (updatedShop.getRating() != null ? updatedShop.getRating() : "null"));
                    
                    shops.put(updatedShop.getName(), updatedShop);
                    Log.d(TAG, "Local cache updated successfully");
                    
                    //Validate cache
                    Shop verifyShop = shops.get(storeName);
                    if (verifyShop != null && verifyShop.getRating() != null) {
                        Log.d(TAG, "Verification - cached shop rating: " + verifyShop.getRating().getValue());
                    }
                } else if (success) {
                    Log.w(TAG, "Rating submission successful but no updated shop data received");
                } else {
                    Log.w(TAG, "Rating submission failed: " + responseDTO.getMessage());
                }
                
                Log.d(TAG, "=== RATING DEBUG END ===");
                return success;
            }
            
            Log.e(TAG, "Invalid response type received");
            Log.d(TAG, "=== RATING DEBUG END ===");
            return false;
        } catch (Exception e) {
            Log.e(TAG, "Error during rating submission", e);
            Log.d(TAG, "=== RATING DEBUG END ===");
            return false;
        }
    }
    
    // Cart management methods
    public synchronized void addToCart(Product product, int quantity) {
        if (product != null && quantity > 0) {
            cart.addProduct(product.getName(), product, quantity);  //Add to cart
            Log.d(TAG, "Added " + quantity + "x " + product.getName() + " to cart");
        }
    }

    
    public synchronized void removeFromCart(String productName) {
        if (productName != null && !productName.trim().isEmpty()) {
            cart.removeProductCompletely(productName); //Remove from cart
            Log.d(TAG, "Removed " + productName + " from cart");
        }
    }

    
    // Getters
    public Map<String, Shop> getShops() {
        return new HashMap<>(shops);
                }
    
    public InventoryCart getCart() {
        return cart;
    }
    
    // Helper methods
    private void logSearchParameters(FilterCords km5Filter) {
        Log.d(TAG, "Creating FilterCords with:");
        Log.d(TAG, "  - radius: " + DEFAULT_SEARCH_RADIUS_KM);
        Log.d(TAG, "  - userCoordinates: " + userCoordinates);
        Log.d(TAG, "  - latitude: " + userCoordinates.getLatitude());
        Log.d(TAG, "  - longitude: " + userCoordinates.getLongitude());
        Log.d(TAG, "FilterCords.getFilter() returns: " + km5Filter.getFilter());
    }
    
    private void updateLocalShopsCache(List<Shop> receivedShops) {
        shops.clear();
        if (receivedShops != null) {
            Log.d(TAG, "=== SHOPS CACHE UPDATE DEBUG START ===");
            Log.d(TAG, "Received " + receivedShops.size() + " shops from server");
            
            for (Shop shop : receivedShops) {
                if (shop != null && shop.getName() != null) {
                    shops.put(shop.getName(), shop);     //Προσθήκη καταστήματος στην cache
                    Log.d(TAG, "Cached shop: " + shop.getName());
                    
                    //=== DEBUGGING: Έλεγχος προϊόντων σε κάθε κατάστημα ===
                    if (shop.getCatalog() != null) {
                        if (shop.getCatalog().getAllProducts() != null) {
                            int productCount = shop.getCatalog().getAllProducts().size();
                            Log.d(TAG, "  -> Products: " + productCount);
                            
                            if (productCount > 0) {
                                Log.d(TAG, "  -> Sample products:");
                                for (int i = 0; i < Math.min(3, productCount); i++) {
                                    other.Product p = shop.getCatalog().getAllProducts().get(i);
                                    Log.d(TAG, "    - " + p.getName() + " ($" + p.getPrice() + ")");
                                }
                            }
                        } else {
                            Log.w(TAG, "  -> Products list is null!");
                        }
                        
                        //Έλεγχος inventory
                        if (shop.getCatalog().getInventory() != null) {
                            Log.d(TAG, "  -> Inventory size: " + shop.getCatalog().getInventory().size());
                        } else {
                            Log.w(TAG, "  -> Inventory is null!");
                        }
                    } else {
                        Log.w(TAG, "  -> Catalog is null!");
                    }
                }
            }
            Log.d(TAG, "=== SHOPS CACHE UPDATE DEBUG END ===");
        } else {
            Log.w(TAG, "Received null shops list from server!");
        }
    }
    
    private Socket createSearchSocket() throws IOException {
        Socket searchSocket = new Socket(DEFAULT_SERVER_IP, DEFAULT_MASTER_PORT);
        searchSocket.setSoTimeout(SOCKET_TIMEOUT_MS);
        return searchSocket;
    }
    
    private Socket createPurchaseSocket() throws IOException {
        Socket purchaseSocket = new Socket(DEFAULT_SERVER_IP, DEFAULT_MASTER_PORT);
        purchaseSocket.setSoTimeout(SOCKET_TIMEOUT_MS);
        return purchaseSocket;
    }
    
    private List<Shop> processSearchResponse(ObjectInputStream searchIn) 
            throws IOException, ClassNotFoundException {
        Object receivedObject = searchIn.readObject();
        
        if (receivedObject instanceof ResponseDTO<?>) {
            ResponseDTO<?> searchResponse = (ResponseDTO<?>) receivedObject;
            ReducerResultDTO dto = (ReducerResultDTO) searchResponse.getData();
            if (dto != null) {
                return dto.getResults();
            }
        }
        
        return null;
    }
    
    private boolean processPurchaseResponse(ObjectInputStream purchaseIn) 
            throws IOException, ClassNotFoundException {
        Object receivedObject = purchaseIn.readObject();
        
        if (receivedObject instanceof ResponseDTO<?>) {
            ResponseDTO<?> purchaseResponse = (ResponseDTO<?>) receivedObject;
            boolean success = purchaseResponse.isSuccess();
            Log.d(TAG, "Purchase result: " + success);
            return success;
        }
        
        return false;
    }
    
    private void closeSearchConnection(ObjectOutputStream searchOut, 
                                     ObjectInputStream searchIn, 
                                     Socket searchSocket) {
        try {
            if (searchOut != null) searchOut.close();
            if (searchIn != null) searchIn.close();
            if (searchSocket != null) searchSocket.close();
        } catch (IOException e) {
            Log.e(TAG, "Error closing search connection", e);
        }
    }
    
    private void closePurchaseConnection(ObjectOutputStream purchaseOut, 
                                       ObjectInputStream purchaseIn, 
                                       Socket purchaseSocket) {
        try {
            if (purchaseOut != null) purchaseOut.close();
            if (purchaseIn != null) purchaseIn.close();
            if (purchaseSocket != null) purchaseSocket.close();
        } catch (IOException e) {
            Log.e(TAG, "Error closing purchase connection", e);
        }
    }
    
    /**
     * Generic request-response method using fresh connection
     */
    private Object sendAndReceiveRequest(Object request) throws IOException, ClassNotFoundException {
        Socket tempSocket = null;
        ObjectOutputStream tempOut = null;
        ObjectInputStream tempIn = null;
        
        try {
            tempSocket = new Socket(DEFAULT_SERVER_IP, DEFAULT_MASTER_PORT);
            tempSocket.setSoTimeout(SOCKET_TIMEOUT_MS);
            tempOut = new ObjectOutputStream(tempSocket.getOutputStream());
            tempIn = new ObjectInputStream(tempSocket.getInputStream());
            
            tempOut.writeObject(request);
            tempOut.flush();
            
            return tempIn.readObject();
            
        } finally {
            try {
                if (tempOut != null) tempOut.close();
                if (tempIn != null) tempIn.close();
                if (tempSocket != null) tempSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Error closing temporary connection", e);
            }
        }
    }
} 