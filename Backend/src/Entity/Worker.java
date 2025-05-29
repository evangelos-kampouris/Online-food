package Entity;

import Node.ReducerNode;
import other.Shop;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a WorkerNode that stores and manages a subset of shops.
 * Applies filtering logic and communicates results to the assigned ReducerNode.
 */
public class Worker extends Entity {

    private ReducerNode REDUCER = null;
    private final Object shopLock = new Object();

    //Shop Name, Shop -- The shops the worker holds.  Received from Master.
    private Map<String, Shop> shops = new HashMap<>();

    /**
     * Initializes the WorkerNode with networking configuration and a reference to its ReducerNode.
     *
     * @param IP the IP address of the worker
     * @param PORT the port to listen on
     * @param reducerIP the IP of the associated ReducerNode
     * @param reducerPort the port of the associated ReducerNode
     */
    public Worker(String IP, int PORT, String reducerIP, int reducerPort) {
        super(IP, PORT);
        REDUCER = new ReducerNode(reducerIP, reducerPort);
    }

    public Map<String, Shop> getShops() {
        synchronized (shopLock) {
            return shops;
        }
    }

    public Shop getShop(String shopName) {
        synchronized (shopLock) {
            return shops.get(shopName);
        }
    }

    public ReducerNode getREDUCER() {
        return REDUCER;
    }

    /**
     * Adds a new shop to the worker's local storage.
     *
     * @param shopName the name of the shop
     * @param shop the shop object to add
     */
    public void addShop(String shopName, Shop shop) {
        synchronized (shopLock) {
            shops.put(shopName, shop);
        }
    }

    public Object getShopLock() {
        return shopLock;
    }

    public static void main(String[] args) {
        Worker worker = new Worker(args[0], Integer.parseInt(args[1]), args[2], Integer.parseInt(args[3]));
        try {
            worker.acceptConnections();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
