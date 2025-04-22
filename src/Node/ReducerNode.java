package Node;

public class ReducerNode extends Node{

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
