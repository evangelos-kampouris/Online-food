package Handlers;

import DTO.RateStoreRequestDTO;
import DTO.Request;
import Entity.Entity;
import Entity.Master;
import Node.WorkerNode;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Handles requests from clients to rate a specific store.
 * Forwards the rating request to the appropriate WorkerNode based on store name.
 */
public class RateStoreRequestHandler implements Handling{

    /**
     * Processes a RateStoreRequestDTO to update a store's rating.
     * Identifies the responsible WorkerNode using the store name and sends the rating there.
     *
     * @param entity the MasterNode handling the request
     * @param connection the socket used for the request (closed immediately)
     * @param request the rating request from the client
     * @param out the output stream (not used in this handler)
     * @param in the input stream (not used in this handler)
     */
    @Override
    public void handle(Entity entity, Socket connection, Request request, ObjectOutputStream out, ObjectInputStream in){

        Master master = (Master) entity;
        RateStoreRequestDTO dto = (RateStoreRequestDTO) request;

        String storeName = dto.getStoreName();
        WorkerNode worker = master.workers.getNodeForKey(storeName);

        if (worker == null) {
            System.out.println("No worker found for store: " + storeName);
            return;
        }

        try (Socket socket = new Socket(worker.getIp(), worker.getPort());
            ObjectOutputStream handler_out = new ObjectOutputStream(socket.getOutputStream())) {

            handler_out.writeObject(dto);
            handler_out.flush();
            System.out.println("Sent rating for store '" + storeName + "' to Worker.");

        } catch (IOException e) {
            System.out.println("Failed to send rate request: " + e.getMessage());
        }
    }
}
