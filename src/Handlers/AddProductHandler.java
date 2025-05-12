package Handlers;

import DTO.AddProductDTO;
import DTO.Request;
import Entity.Entity;
import Entity.Master;
import Entity.Worker;
import Node.WorkerNode;
import Responses.ResponseDTO;
import other.Product;
import other.ProductCategory;
import other.Shop;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Handles requests to add a product to an existing store's inventory.
 * Forwards the request from the MasterNode to the responsible WorkerNode,
 * or directly updates the store if executed on a Worker.
 */
public class AddProductHandler implements Handling {

    /**
     * Processes an AddProductDTO request.
     * If called on the Master, the request is routed to the appropriate WorkerNode using consistent hashing.
     * If called on a Worker, the product is added to the specified store if it exists.
     *
     * @param entity the entity handling the request (Master or Worker)
     * @param connection the socket through which the request was received
     * @param request the request object containing product details
     * @param out the stream to send a response back to the origin
     * @param in the stream to receive any additional data (if needed)
     */
    @Override
    public void handle(Entity entity, Socket connection, Request request, ObjectOutputStream out, ObjectInputStream in) {
        AddProductDTO dto = (AddProductDTO) request;
        String storeName = dto.getStoreName();

        ResponseDTO<?> responseDTO = null;

        if (entity instanceof Master master) {
            WorkerNode worker = master.workers.getNodeForKey(storeName);

            if (worker == null) {
                System.out.println("No Worker found for store: " + storeName);
                responseDTO = new ResponseDTO<>(false, "No Worker found for store: " + storeName);
                try {
                    out.writeObject(responseDTO);
                    out.flush();
                } catch (IOException e) {
                    System.out.println(responseDTO.getMessage() + "Exception Message: " + e.getMessage());
                }
                return;
            }

            try {
                Socket socket = new Socket(worker.getIp(), worker.getPort());
                ObjectOutputStream handler_out = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream handler_in = new ObjectInputStream(socket.getInputStream());

                handler_out.writeObject(dto);
                handler_out.flush();

                System.out.println("Forwarded Add product request to worker " + worker.getIp()); //debug

                // Receive the response containing the updated shop
                responseDTO = (ResponseDTO<?>) handler_in.readObject();
                out.writeObject(responseDTO);
                out.flush();
                System.out.println("Respond sent."); //debug

                closeConnection(socket, handler_out, handler_in);

            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Error forwarding to worker: " + e.getMessage());
                responseDTO = new ResponseDTO<>(false, "Error forwarding to worker: " + e.getMessage());
                try {
                    out.writeObject(responseDTO);
                    out.flush();
                    connection.shutdownOutput();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        } else if (entity instanceof Worker worker) {
            synchronized (worker.getShopLock()) {
                Shop shop = worker.getShop(storeName);

                if (shop == null) {
                    System.out.println("Store not found: " + storeName);
                    responseDTO = new ResponseDTO<>(false, "Store not found", null);
                }
                else {
                    Product product = new Product(dto.getProductName(), dto.getProductCategory(), dto.getPrice());
                    shop.addProduct(product.getName(), product, dto.getQuantity(), true);
                    System.out.println("Added product to store: " + dto.getProductName());//debug
                    responseDTO = new ResponseDTO<>(true, "Added product to store: " + dto.getProductName(), shop);
                }
                try {
                    out.writeObject(responseDTO);
                    out.flush();
                } catch (IOException e) {
                    System.out.println("Error sending back to master the updated Shop " + e.getMessage());
                }
            }
        }
        else {
            responseDTO = new ResponseDTO<>(false, "Request forwarded to wrong entity, Entity is not a Master or Worker but is: " + entity.getClass().getName());
            try {
                out.writeObject(responseDTO);
                out.flush();
                connection.shutdownOutput();
            } catch (IOException e) {
                System.err.println(responseDTO.getMessage());
            }
        }
    }
}
