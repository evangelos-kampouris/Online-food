package Node;

import java.io.Serial;

public class MasterNode extends Node {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a MasterNode instance with specified IP and port.
     *
     * @param IP the IP address of the master node
     * @param port the port number used by the master node
     */
    public MasterNode(String IP, int port) {
        super(IP, port);
    }

    /**
     * Checks equality between this MasterNode and another object.
     * Two MasterNodes are considered equal if their IP addresses and ports match.
     *
     * @param o the object to compare
     * @return true if equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MasterNode)) return false;
        MasterNode that = (MasterNode) o;
        return port == that.port && ip.equals(that.ip);
    }

    public String getIP() {
        return null;
    }
}
