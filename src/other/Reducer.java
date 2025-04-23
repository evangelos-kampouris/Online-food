package other;

import Node.ReducerNode;

public class Reducer extends Entity {

    private final String MASTER_IP;
    private final int MASTER_PORT;

    public Reducer(String IP, int PORT, String masterIP, int masterPort) {
        super(IP, PORT);
        MASTER_IP = masterIP;
        MASTER_PORT = masterPort;
    }

    public int getMASTER_PORT() {return MASTER_PORT;}

    public String getMASTER_IP() {return MASTER_IP;}
}
