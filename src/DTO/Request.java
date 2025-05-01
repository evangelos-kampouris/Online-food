package DTO;

import java.io.Serial;
import java.io.Serializable;

/**
 * Base class for all request DTOs exchanged between clients, master, and workers.
 * Each request is assigned a unique identifier for tracking purposes.
 */
public class Request implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private static int requestCounter = 0;
    protected int requestId; //REQUEST FOR ONLY PRIMARY REQUESTS. SECONDARY REQUEST SHOULD HAVE SOMEONE ELSE SET THEIR REQUEST ID.

    /**
     * Initializes a new request and assigns a unique request ID.
     * Used for identifying and tracking distributed operations.
     */
    public Request() {
        requestId = ++requestCounter;
    }
    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }
}
