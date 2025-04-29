package Responses;

public class ResponseDTO<T> {
    public boolean success;
    public String message;
    public T data; // optional payload

    public ResponseDTO(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

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