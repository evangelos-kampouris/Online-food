package Handlers;

import DTO.ChangeStockDTO;
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

/**
 * Handles requests to update the stock quantity of a product in a shop's inventory.
 * Forwards the request from the MasterNode to the correct WorkerNode, or updates stock directly on the Worker.
 */
public class ChangeStockHandler implements Handling{

    /**
     * Processes a ChangeStockDTO request to update the stock of a product in a store.
     * If the request is received by the Master, it is routed to the appropriate WorkerNode.
     * If the Worker receives it, the product stock is updated directly.
     *
     * @param entity the entity processing the request (Master or Worker)
     * @param connection the socket connection used for the request
     * @param request the DTO containing store and stock update data
     * @param out the output stream for sending responses
     * @param in the input stream for reading additional data (not used here)
     */
    @Override
    public void handle(Entity entity, Socket connection, Request request, ObjectOutputStream out, ObjectInputStream in) {
        System.out.println("Received a stoke change request from: " + connection.getRemoteSocketAddress());
        ChangeStockDTO dto = (ChangeStockDTO) request;
        String storeName = dto.getStoreName();

        ResponseDTO<Shop> responseDTO = null;

        if(entity instanceof Master master){
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

                System.out.println("Forwarded change request to worker " + worker.getIp() + "Awaiting Response...");

                // Receive the response containing the updated shop
                responseDTO = (ResponseDTO) handler_in.readObject();
                out.writeObject(responseDTO);
                out.flush();
                System.out.println("Respond sent.");

                closeConnection(socket,handler_out,handler_in); //Close the connection with the worker.

            }catch (IOException e){
                System.out.println("Error forwarding to worker: " + e.getMessage());
                //out.writeObject("Error forwarding to worker: " + e.getMessage());
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        else if(entity instanceof Worker worker){
            Shop shop = worker.getShop(storeName);

            if (shop == null) {
                System.out.println("Store not found: " + storeName);
                responseDTO = new ResponseDTO<>(false, "Store not found", null);
            }
            else {
                shop.getCatalog().changeStock(dto.getProductName(), dto.getNewStock());
                responseDTO = new ResponseDTO<>(true, "Stock updated successfully", shop);
            }
            //Send shop to master
            try {
                out.writeObject(responseDTO);
                out.flush();
            } catch (IOException e) {
                System.out.println("Error sending back to master the updated Shop " + e.getMessage());
            }
        }
        else{
            responseDTO = new ResponseDTO<>(false, "Request forwarded to wrong entity, Entity is not a Master or Worker but is: " + entity.getClass().getName());
            try {
                out.writeObject(responseDTO);
                out.flush();
            } catch (IOException e) {
                System.err.println(responseDTO.getMessage());
            }
        }
    }
}
