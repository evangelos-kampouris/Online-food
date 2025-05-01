package Handlers;

import DTO.Request;
import DTO.UpdateBuyDataRequestDTO;
import Entity.Entity;
import Entity.Worker;
import Exceptions.NoValidStockInput;
import Responses.ResponseDTO;
import other.Shop;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Handles internal updates to a shop's inventory after a successful purchase.
 * Executed on the WorkerNode upon receiving confirmation from the MasterNode.
 */
public class UpdateBuyDataHandler implements Handling{

    /**
     * Processes an UpdateBuyDataRequestDTO to deduct stock and register the sale on a WorkerNode.
     * Validates the shop existence and executes the sell operation on the inventory.
     *
     * @param entity the WorkerNode executing the inventory update
     * @param connection the socket used for communication with the Master
     * @param request the DTO containing cart and shop data
     * @param out the output stream to send confirmation back to the Master
     * @param in the input stream (closed after execution)
     */
    @Override
    public void handle(Entity entity, Socket connection, Request request, ObjectOutputStream out, ObjectInputStream in) {
        Worker worker = (Worker) entity;
        UpdateBuyDataRequestDTO DTO = (UpdateBuyDataRequestDTO) request;
        String storeName = DTO.data.getShop().getName();

        Shop savedShop = worker.getShop(storeName);
        ResponseDTO<UpdateBuyDataRequestDTO> responseDTO = null;
        if(savedShop == null){
            responseDTO = new ResponseDTO<>(false, "Shop not found", DTO);
        }
        else{
            try {
                savedShop.sell(DTO.data.getCart()); //sell
            } catch (NoValidStockInput | IllegalArgumentException e) {
                responseDTO = new ResponseDTO<>(false, e.getMessage(), DTO);
            }
            responseDTO = new ResponseDTO<>(true, "Purchase Proceeded successfully");
        }
        //Send the response back to Master
        try {
            out.writeObject(responseDTO);
            out.flush();
        } catch (IOException e) {
            System.err.println("Error writing to output stream when sending response from buy request to master.");
        }

        //Close connection
        closeConnection(connection, out, in);
    }
}
