package other;

import java.util.Objects;

public class WorkerNode {
    private final String ip;
    private final int port;

    public WorkerNode(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public String getIp() { return ip; }
    public int getPort() { return port; }

    @Override
    public String toString() {
        return ip + ":" + port;
    }

    /**
     *
     * @param o
     * @return true or false
     *
     * Check if two WorkerNodes are logically the same.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WorkerNode)) return false;
        WorkerNode that = (WorkerNode) o;
        return port == that.port && ip.equals(that.ip);
    }

    /**
     * 	Ensures equal objects land in the same hash bucket. Used for map data structures from java.
     * @return
     */
    @Override
    public int hashCode() {
        return Objects.hash(ip, port);
    }
}

