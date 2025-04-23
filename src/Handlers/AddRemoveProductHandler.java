package Handlers;

import DTO.AddRemoveProductDTO;
import DTO.Request;
import Node.WorkerNode;
import Entity.Entity;
import Entity.Master;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;


public class AddRemoveProductHandler implements Handling{

    @Override
    public void handle(Entity entity, Socket connection, Request request, ObjectOutputStream out, ObjectInputStream in){
        Master master = (Master) entity;
        AddRemoveProductDTO dto = (AddRemoveProductDTO) request;

        String storeName = dto.getStoreName();

        WorkerNode worker = master.workers.getNodeForKey(storeName);

        if (worker == null) {
            System.out.println("No available worker for store: " + storeName);
            try {
                out.writeObject("Worker not found for store: " + storeName);
            } catch (IOException e) {
                System.out.println("Failed to send error response: " + e.getMessage());
            }
            return;
        }

        try (Socket socket = new Socket(worker.getIp(), worker.getPort());
             ObjectOutputStream handler_out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream handler_in = new ObjectInputStream(socket.getInputStream())) {

            handler_out.writeObject(dto);
            handler_out.flush();

            System.out.println("Product " + dto.getAction() + " request for '" + dto.getProductName() + "' sent to worker " + worker.getIp());
            out.writeObject("Product " + dto.getAction() + " request sent successfully for store: " + storeName);

        } catch (IOException e) {
            System.out.println("Failed to send product request to worker: " + e.getMessage());
            try {
                out.writeObject("Error sending product request to worker: " + e.getMessage());
            } catch (IOException ioException) {
                System.out.println("Failed to send error response: " + ioException.getMessage());
            }
        }
    }

}
