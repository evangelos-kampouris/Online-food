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

/**
 * Handles requests to remove (disable) a product from a shop's inventory.
 * Forwards the request from the MasterNode to the appropriate WorkerNode,
 * or updates the shop directly if executed on a Worker.
 */
public class RemoveProductHandler implements Handling {

    /**
     * Processes a RemoveProductDTO request to disable a product from a store.
     * If handled by the Master, the request is forwarded to the correct WorkerNode.
     * If handled by the Worker, the product is marked as disabled in the shop's catalog.
     *
     * @param entity the entity processing the request (Master or Worker)
     * @param connection the socket connection for communication
     * @param request the request containing the store and product to remove
     * @param out the output stream to send the response
     * @param in the input stream to receive additional data (unused)
     */
    @Override
    public void handle(Entity entity, Socket connection, Request request, ObjectOutputStream out, ObjectInputStream in) {
        System.out.println("Received a remove product request from: " + connection.getRemoteSocketAddress());
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

            try {
                Socket socket = new Socket(worker.getIp(), worker.getPort());
                ObjectOutputStream handler_out = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream handler_in = new ObjectInputStream(socket.getInputStream());

                handler_out.writeObject(dto);
                handler_out.flush();

                System.out.println("Forwarded Remove product request to worker " + worker.getIp());//debug

                // Receive the response containing the updated shop
                responseDTO = (ResponseDTO<Shop>) handler_in.readObject();
                out.writeObject(responseDTO);
                out.flush();
                System.out.println("Respond sent.");

                closeConnection(socket, handler_out, handler_in);

            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Error forwarding to worker: " + e.getMessage());
            }
        } else if (entity instanceof Worker worker) {
            synchronized (worker.getShopLock()) {
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
            }

        } else {
            System.err.println("Request forwarded to wrong entity, Entity is not a Master or Worker");
        }
    }
}