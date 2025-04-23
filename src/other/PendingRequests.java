package other;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class PendingRequests {

    private final ObjectOutputStream out;
    private final ObjectInputStream in;

    public PendingRequests(ObjectOutputStream out, ObjectInputStream in) {
        this.out = out;
        this.in = in;
    }
    public ObjectOutputStream getOut() {
        return out;
    }

    public ObjectInputStream getIn() {
        return in;
    }
}
