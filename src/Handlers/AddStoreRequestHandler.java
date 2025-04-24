package Handlers;

import DTO.AddStoreRequestDTO;
import DTO.Request;
import Node.WorkerNode;
import Entity.Entity;
import Entity.Master;
import other.Shop;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class AddStoreRequestHandler implements Handling{

    @Override
    public void handle(Entity entity, Socket connection, Request request, ObjectOutputStream out, ObjectInputStream in) {
        Master master = (Master) entity;
        AddStoreRequestDTO dto = (AddStoreRequestDTO) request;

        Shop shop = dto.getShop();
        String storeName = shop.getName();

        WorkerNode worker = master.workers.getNodeForKey(storeName);

        if (worker == null) {
            System.out.println("No available worker for store: " + storeName);
            try {
                //out.writeObject("Worker not found for store: " + storeName);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return;
        }
        try (Socket socket = new Socket(worker.getIp(), worker.getPort());
             ObjectOutputStream handler_out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream handler_in = new ObjectInputStream(socket.getInputStream())) {

            handler_out.writeObject(shop);
            handler_out.flush();

            System.out.println("Store '" + storeName + "' sent to worker at " + worker.getIp() + ":" + worker.getPort());
            //out.writeObject("Store added successfully: " + storeName);

        } catch (IOException e) {
            System.out.println("Failed to send store to worker: " + e.getMessage());
            try {
                //out.writeObject("Error sending store to worker: " + e.getMessage());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
