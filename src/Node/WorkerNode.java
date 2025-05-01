package Node;

import java.io.Serial;
import java.util.Objects;

public class WorkerNode extends Node{
    @Serial
    private static final long serialVersionUID = 1L;
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

