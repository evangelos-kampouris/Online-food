package Entity;

import Node.ReducerNode;
import other.Shop;

import java.util.HashMap;
import java.util.Map;

public class Worker extends Entity {

    private ReducerNode REDUCER = null;

    //Shop Name, Shop -- The shops the worker holds.  Received from Master.
    private Map<String, Shop> shops = new HashMap<>();

    public Worker(String IP, int PORT, String reducerIP, int reducerPort) {
        super(IP, PORT);
        REDUCER = new ReducerNode(reducerIP, reducerPort);
    }

    public Map<String, Shop> getShops() {
        return shops;
    }

    public Shop getShop(String shopName) {
        return shops.get(shopName);
    }

    public ReducerNode getREDUCER() {
        return REDUCER;
    }

    public synchronized void addShop(String shopName, Shop shop) {
        shops.put(shopName, shop);
    }


}
