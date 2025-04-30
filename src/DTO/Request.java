package DTO;

import java.io.Serial;
import java.io.Serializable;

public class Request implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private static int requestCounter = 0;
    protected int requestId; //REQUEST FOR ONLY PRIMARY REQUESTS. SECONDARY REQUEST SHOULD HAVE SOMEONE ELSE SET THEIR REQUEST ID.

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
