package Node;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public class Node implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    protected final String ip;
    protected final int port;

    /**
     * Creates a network node with a specific IP address and port number.
     *
     * @param ip the IP address of the node
     * @param port the port number the node listens to
     */
    public Node(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }
    public String getIp() { return ip; }
    public int getPort() { return port; }

    /**
     * Returns a string representation of the node in the format "ip:port".
     *
     * @return the string representation of the node
     */
    public String toString() {
        return ip + ":" + port;
    }

    /**
     * Computes the hash code based on IP and port.
     *
     * This ensures that nodes with the same IP and port are placed in the same hash bucket when used in hashed collections.
     *
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(ip, port);
    }
}
