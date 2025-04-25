package Handlers;

import DTO.Request;
import DTO.UpdateBuyDataRequestDTO;
import Entity.Entity;
import Exceptions.NoValidStockInput;
import Responses.ResponseDTO;
import other.Shop;
import Entity.Worker;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class UpdateBuyDataHandler implements Handling{

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
