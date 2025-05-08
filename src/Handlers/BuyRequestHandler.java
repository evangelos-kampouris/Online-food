package Handlers;

import DTO.BuyRequestDTO;
import DTO.Request;
import DTO.UpdateBuyDataRequestDTO;
import Entity.Entity;
import Entity.Master;
import Node.WorkerNode;
import Responses.ResponseDTO;
import other.ProductCategory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Handles a buy request initiated by a client.
 * Forwards the request to the responsible WorkerNode and updates sales statistics upon success.
 */
public class BuyRequestHandler implements Handling {

    /**
     * Processes a BuyRequestDTO from a client.
     * The request is forwarded to the appropriate WorkerNode based on the store name.
     * If the purchase is successful, the MasterNode updates product and store sales statistics.
     *
     * @param entity the MasterNode handling the request
     * @param connection the socket connection from the client
     * @param request the request containing cart and shop data
     * @param request_out the output stream to send the response back
     * @param request_in the input stream from the client (not used here)
     */
    @Override
    public void handle(Entity entity, Socket connection, Request request, ObjectOutputStream request_out, ObjectInputStream request_in) {
        Master master = (Master) entity;
        BuyRequestDTO buyRequestDTO = (BuyRequestDTO) request;
        String storeName = buyRequestDTO.getShop().getName();

        WorkerNode worker = master.workers.getNodeForKey(storeName);

        //Initiating connection and forwarding the request to the worker.
        try(Socket workerConnectionSocket = new Socket(worker.getIp(), worker.getPort()) ;
            ObjectOutputStream handler_out = new ObjectOutputStream(workerConnectionSocket.getOutputStream());
            ObjectInputStream handler_in = new ObjectInputStream(workerConnectionSocket.getInputStream())){

            UpdateBuyDataRequestDTO updateBuyDataRequestDTO = new UpdateBuyDataRequestDTO(buyRequestDTO);   //Update
            handler_out.writeObject(updateBuyDataRequestDTO);
            handler_out.flush();

            //Wait and receive response
            ResponseDTO<Request> response = (ResponseDTO<Request>) handler_in.readObject();
            if(response.isSuccess()){
                //Add statistics
                master.addStatsStoreCategory(buyRequestDTO.getShop().getStoreCategory(), buyRequestDTO.getShop().getName(), buyRequestDTO.getCart().cartQuantity()); //Store

                for(ProductCategory category : buyRequestDTO.getCart().getProductCategories()){ //Product
                    master.addStatsProductCategory(category, buyRequestDTO.getShop().getName(), buyRequestDTO.getCart().cartQuantity());
                }
            }
            request_out.writeObject(response);
            request_out.flush();

            closeConnection(workerConnectionSocket, handler_out, handler_in);

        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }


    }
}
