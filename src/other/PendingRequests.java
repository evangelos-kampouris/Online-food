package other;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Holds the input and output object streams associated with a pending request.
 * Used to maintain communication channels during asynchronous request handling.
 */
public class PendingRequests {

    private final ObjectOutputStream out;
    private final ObjectInputStream in;

    /**
     * Initializes a new pending request with the specified input and output streams.
     *
     * @param out the output stream to write responses
     * @param in the input stream to read incoming data
     */
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
