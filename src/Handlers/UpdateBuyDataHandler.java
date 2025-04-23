package Handlers;

import DTO.Request;
import DTO.UpdateBuyDataRequestDTO;
import Entity.Entity;
import other.Shop;
import Entity.Worker;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class UpdateBuyDataHandler implements Handling{

    @Override
    public void handle(Entity entity, Socket connection, Request request, ObjectOutputStream out, ObjectInputStream in) {
        Worker worker = (Worker) entity;
        UpdateBuyDataRequestDTO DTO = (UpdateBuyDataRequestDTO) request;
        String storeName = DTO.data.getShop().getName();

        worker.getShop(storeName).sell(DTO.data.getCart()); //sell

        //Close connection
        closeConnection(connection, out, in);
    }
}
