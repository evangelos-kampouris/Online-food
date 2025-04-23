package Handlers;

import DTO.Request;
import Entity.Entity;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

public interface Handling extends Serializable {

    void handle(Entity entity, Socket connection, Request request, ObjectOutputStream out, ObjectInputStream in);

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
