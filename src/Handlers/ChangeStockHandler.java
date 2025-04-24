package Handlers;

import DTO.ChangeStockDTO;
import DTO.Request;
import Node.WorkerNode;
import Entity.Entity;
import Entity.Master;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;


public class ChangeStockHandler implements Handling{

    @Override
    public void handle(Entity entity, Socket connection, Request request, ObjectOutputStream out, ObjectInputStream in) {
        Master master = (Master) entity;
        ChangeStockDTO dto = (ChangeStockDTO) request;

        String storeName = dto.getStoreName();

        WorkerNode worker = master.workers.getNodeForKey(storeName);

        if (worker == null) {
            System.out.println("No available worker for store: " + storeName);
            return;
        }

        try (Socket socket = new Socket(worker.getIp(), worker.getPort());
             ObjectOutputStream handler_out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream handler_in = new ObjectInputStream(socket.getInputStream())) {

            handler_out.writeObject(dto);
            handler_out.flush();

            Object response = handler_in.readObject();

            //out.writeObject(response);

            System.out.println("Stock change for '" + dto.getProductName() + "' sent to worker " + worker.getIp());

        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Failed to send stock change to worker: " + e.getMessage());
            try {
                //out.writeObject("Error: " + e.getMessage());
            } catch (IOException ex) {
                System.out.println("Failed to notify client.");
            }
        }
    }
}
