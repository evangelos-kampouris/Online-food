package Node;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public class Node implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    protected final String ip;
    protected final int port;

    public Node(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }
    public String getIp() { return ip; }
    public int getPort() { return port; }

    public String toString() {
        return ip + ":" + port;
    }

    /**
     * 	Ensures equal objects land in the same hash bucket. Used for map data structures from java.
     * @retur
     */
    @Override
    public int hashCode() {
        return Objects.hash(ip, port);
    }
}
