package Handlers;

import DTO.BuyRequestDTO;
import DTO.Request;
import DTO.UpdateBuyDataRequestDTO;
import Entity.Entity;
import Entity.Master;
import other.ProductCategory;
import Node.WorkerNode;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class BuyRequestHandler implements Handling {

    @Override
    public void handle(Entity entity, Socket connection, Request request, ObjectOutputStream request_out, ObjectInputStream request_in) {
        Master master = (Master) entity;
        BuyRequestDTO buyRequestDTO = (BuyRequestDTO) request;
        String storeName = buyRequestDTO.getShop().getName();

        WorkerNode worker = master.workers.getNodeForKey(storeName);

        //Initiating connection and forwarding the request to the worker.
        try(Socket workerConnectionSocket = new Socket(worker.getIp(), worker.getPort()) ;
            ObjectOutputStream handler_out = new ObjectOutputStream(workerConnectionSocket.getOutputStream())){

            UpdateBuyDataRequestDTO updateBuyDataRequestDTO = new UpdateBuyDataRequestDTO(buyRequestDTO);   //Update
            handler_out.writeObject(updateBuyDataRequestDTO);
            handler_out.flush();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //statistics
        master.addStatsStoreCategory(buyRequestDTO.getShop().getStoreCategory()); //Store

        for(ProductCategory category : buyRequestDTO.getCart().getProductCategories()){ //Product
            master.addStatsProductCategory(category);
        }
        //Closing the connection from the client
        closeConnection(connection, request_out, request_in);
    }
}
