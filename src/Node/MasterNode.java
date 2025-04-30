package Node;

import java.io.Serial;

public class MasterNode extends Node {
    @Serial
    private static final long serialVersionUID = 1L;
    public MasterNode(String IP, int port) {
        super(IP, port);
    }

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
