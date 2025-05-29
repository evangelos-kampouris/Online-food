package Handlers;

import DTO.Request;
import Entity.Entity;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

/**
 * Common interface for handling incoming network requests.
 * Each implementation defines how a specific type of request is processed.
 */
public interface Handling extends Serializable {

    /**
     * Processes the incoming request using the appropriate logic for the given entity.
     *
     * @param entity the entity (Master, Worker, Reducer) handling the request
     * @param connection the socket through which the request was received
     * @param request the deserialized request object
     * @param out the output stream to send the response
     * @param in the input stream to read data (if needed)
     */
    void handle(Entity entity, Socket connection, Request request, ObjectOutputStream out, ObjectInputStream in);

    /**
     * Safely closes the socket and associated streams.
     *
     * @param connection the socket to close
     * @param out the output stream to close
     * @param in the input stream to close
     */
    default void closeConnection(Socket connection, ObjectOutputStream out, ObjectInputStream in){
        // Close resources safely
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (connection != null && !connection.isClosed()) connection.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
