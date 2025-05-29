package Handlers;

import DTO.RateStoreRequestDTO;
import DTO.Request;
import Entity.*;
import Node.WorkerNode;
import Responses.ResponseDTO;
import other.Shop;

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
     * Handles a {@link RateStoreRequestDTO} by routing it to the appropriate node
     * (Master or Worker) to update a store’s rating.
     *
     * <ul>
     *   <li>If the current {@code entity} is a Master:
     *     <ol>
     *       <li>Determine which {@link WorkerNode} is responsible for the requested store.</li>
     *       <li>Forward the rating DTO over a new socket connection to that WorkerNode.</li>
     *       <li>Wait for and relay the Worker’s response back to the original caller.</li>
     *     </ol>
     *   </li>
     *   <li>If the current {@code entity} is a Worker:
     *     <ol>
     *       <li>Lookup the {@link Shop} by name and apply the new rating.</li>
     *       <li>Build a success or failure {@link ResponseDTO} and send it back to the Master.</li>
     *     </ol>
     *   </li>
     *   <li>If the {@code entity} is neither Master nor Worker, a failure response is returned.</li>
     * </ul>
     *
     * @param entity     the node processing the request (Master or Worker)
     * @param connection the incoming socket connection for this request
     *                   (closed immediately after dispatch)
     * @param request    the client’s rating request, cast to {@link RateStoreRequestDTO}
     * @param out        the output stream to send the {@link ResponseDTO} back
     *                   (not directly used for business logic)
     * @param in         the input stream for reading from the caller
     *                   (not used in this handler)
     */
    @Override
    public void handle(Entity entity, Socket connection, Request request, ObjectOutputStream out, ObjectInputStream in){
        System.out.println("Received a rate store request from: " + connection.getRemoteSocketAddress());
        RateStoreRequestDTO dto = (RateStoreRequestDTO) request;
        ResponseDTO<?> responseDTO = null;

        if(entity instanceof Master master){
            String storeName = dto.getStoreName();
            WorkerNode worker = master.workers.getNodeForKey(dto.getStoreName());

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
                System.out.println("Sent rating for store '" + storeName + "' to Worker.");//debug

                // Receive the response containing the updated shop
                responseDTO = (ResponseDTO<?>) handler_in.readObject();
                out.writeObject(responseDTO);
                out.flush();
                System.out.println("Respond sent."); //debug

                closeConnection(socket, handler_out, handler_in);
            } catch (IOException e){
                System.out.println("Exception Message in RateStoreRequestHandler: " + e.getMessage());
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        } else if (entity instanceof Worker worker) {
            Shop shop = worker.getShop(dto.getStoreName());

            if (shop == null) {
                System.out.println("No Shop found for store: " + dto.getStoreName());
                responseDTO = new ResponseDTO<>(false, "No Shop found for store: " + dto.getStoreName());
            }
            else{
                shop.updateRating(dto.getRating());
                responseDTO = new ResponseDTO<>(true, "Successfully updated rating for store: " + dto.getStoreName(), shop);
            }
            try{
                out.writeObject(responseDTO);
                out.flush();
                System.out.println("Sent response for store rating '" + dto.getStoreName() + "' to Master.");

            } catch (IOException e) {
                System.out.println("Error sending back to master the response: " + e.getMessage());
            }
        }
        else{
            responseDTO = new ResponseDTO<>(false, "Request forwarded to wrong entity, Entity is not a Master or Worker but is: " + entity.getClass().getName());
            try{
                out.writeObject(responseDTO);
                out.flush();
                System.out.println("Sent response for store rating '" + dto.getStoreName() + "' to Master.");

            } catch (IOException e) {
                System.out.println("Error sending back to master the response: " + e.getMessage());
            }
        }
    }
}
