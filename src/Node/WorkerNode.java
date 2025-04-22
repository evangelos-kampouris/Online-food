package Node;

import java.util.Objects;

public class WorkerNode extends Node{

    public WorkerNode(String ip, int port) {
        super(ip, port);
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
}

