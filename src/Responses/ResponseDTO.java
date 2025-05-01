package Responses;

import java.io.Serial;
import java.io.Serializable;

/**
 * A generic response wrapper used to return results from server to client.
 * Includes success status, an optional message, and an optional data payload.
 *
 * @param <T> the type of the payload contained in the response
 */
public class ResponseDTO<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    public boolean success;
    public String message;
    public T data; // optional payload

    /**
     * Creates a full response object with status, message, and payload.
     *
     * @param success whether the request was successful
     * @param message additional information or error message
     * @param data the payload returned with the response
     */
    public ResponseDTO(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    /**
     * Creates a response with status and message, without payload.
     *
     * @param success whether the request was successful
     * @param message additional information or error message
     */
    public ResponseDTO(boolean success, String message) {
        this(success, message, null);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }
}