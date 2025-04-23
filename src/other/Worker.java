package other;

import Node.ReducerNode;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Worker extends Entity {

    private ReducerNode REDUCER = null;

    //Shop Name, Shop -- The shops the worker holds.  Received from Master.
    public Map<String, Shop> shops = new HashMap<>();

    public Worker(String IP, int PORT, String reducerIP, int reducerPort) {
        super(IP, PORT);
        REDUCER = new ReducerNode(reducerIP, reducerPort);
    }

    public Map<String, Shop> getShops() {
        return shops;
    }
    public ReducerNode getREDUCER() {
        return REDUCER;
    }


}
