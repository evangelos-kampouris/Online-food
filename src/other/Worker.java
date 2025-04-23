package other;

import Node.ReducerNode;

import java.util.HashMap;
import java.util.Map;

public class Worker extends Entity {
    private static String MASTER_IP;
    private static String MASTER_PORT;

    private ReducerNode REDUCER = new ReducerNode("localhost", 8999); //temporary data information.

    private String IP;
    private String PORT;

    //Shop Name, Shop -- The shops the worker holds.  Received from Master.
    public Map<String, Shop> shops = new HashMap<>();

    public Worker() {
        //TODO SET OWN IP AND PORT
    }

    public Worker(String IP, String PORT) {
        this.IP = IP;
        this.PORT = PORT;
    }

    public Worker(String masterIP, String masterPort, String IP, String PORT) {
        MASTER_IP = masterIP;
        MASTER_PORT = masterPort;
        this.IP = IP;
        this.PORT = PORT;
    }

    public Map<String, Shop> getShops() {
        return shops;
    }
    public ReducerNode getREDUCER() {
        return REDUCER;
    }
}
