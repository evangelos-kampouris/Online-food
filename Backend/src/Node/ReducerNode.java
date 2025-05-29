package Node;

import java.io.Serial;

public class ReducerNode extends Node{
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a ReducerNode with the specified IP address and port.
     *
     * @param ip the IP address of the reducer
     * @param port the port number used by the reducer
     */
    public ReducerNode(String ip, int port) {
        super(ip, port);
    }

    /**
     * Determines whether this ReducerNode is equal to another object.
     * Two reducer nodes are considered equal if they have the same IP and port.
     *
     * @param o the object to compare
     * @return true if the nodes are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReducerNode)) return false;
        ReducerNode that = (ReducerNode) o;
        return port == that.port && ip.equals(that.ip);
    }
}
