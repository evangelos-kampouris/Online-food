package Node;

import java.io.Serial;

public class ReducerNode extends Node{
    @Serial
    private static final long serialVersionUID = 1L;
    public ReducerNode(String ip, int port) {
        super(ip, port);
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReducerNode)) return false;
        ReducerNode that = (ReducerNode) o;
        return port == that.port && ip.equals(that.ip);
    }
}
