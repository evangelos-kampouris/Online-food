package Node;

import java.io.Serial;

public class WorkerNode extends Node{
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a WorkerNode with a given IP address and port number.
     *
     * @param ip the IP address of the worker
     * @param port the port number the worker listens to
     */
    public WorkerNode(String ip, int port) {
        super(ip, port);
    }

    /**
     * Checks if this WorkerNode is equal to another object.
     *
     * Two WorkerNodes are considered equal if they have the same IP and port.
     *
     *
     * @param o the object to compare
     * @return true if the nodes are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WorkerNode)) return false;
        WorkerNode that = (WorkerNode) o;
        return port == that.port && ip.equals(that.ip);
    }
}

