package Handlers;

import DTO.RemoveProductDTO;
import DTO.Request;
import Entity.Entity;
import Entity.Master;
import Entity.Worker;
import Node.WorkerNode;
import Responses.ResponseDTO;
import other.Shop;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class RemoveProductHandler implements Handling {

    @Override
    public void handle(Entity entity, Socket connection, Request request, ObjectOutputStream out, ObjectInputStream in) {
        RemoveProductDTO dto = (RemoveProductDTO) request;
        String storeName = dto.getStoreName();

        ResponseDTO<Shop> responseDTO = null;

        if (entity instanceof Master master) {
            WorkerNode worker = master.workers.getNodeForKey(storeName);

            if (worker == null) {
                System.out.println("No Worker found for store: " + storeName);
                responseDTO = new ResponseDTO<>(false, "No Worker found for store: " + storeName);
                try {
                    out.writeObject(responseDTO);
                    out.flush();
                } catch (IOException e) {
                    System.out.println(responseDTO.getMessage());
                }
                return;
            }

            try (Socket socket = new Socket(worker.getIp(), worker.getPort());
                 ObjectOutputStream handler_out = new ObjectOutputStream(socket.getOutputStream());
                 ObjectInputStream handler_in = new ObjectInputStream(socket.getInputStream())) {

                handler_out.writeObject(dto);
                handler_out.flush();

                System.out.println("Forwarded Remove product request to worker " + worker.getIp());

                // Receive the response containing the updated shop
                responseDTO = (ResponseDTO) handler_in.readObject();
                out.writeObject(responseDTO);
                out.flush();
                System.out.println("Respond sent.");

                closeConnection(socket, handler_out, handler_in);

            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Error forwarding to worker: " + e.getMessage());
            }
        } else if (entity instanceof Worker worker) {
            Shop shop = worker.getShop(storeName);

            if (shop == null) {
                System.out.println("Store not found: " + storeName);
                responseDTO = new ResponseDTO<>(false, "Store not found", null);
            }
            else{
                shop.getCatalog().setItemEnableStatus(dto.getProductName(), false);
                System.out.println("Removed product from store: " + dto.getProductName());
                responseDTO = new ResponseDTO<>(true, "successfully removed product from the store", shop);
            }
            try {
                out.writeObject(responseDTO);
                out.flush();
            } catch (IOException e) {
                System.out.println("Error sending back to master the updated Shop " + e.getMessage());
            }
        } else {
            System.err.println("Request forwarded to wrong entity, Entity is not a Master or Worker");
        }
    }
}