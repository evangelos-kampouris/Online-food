package Handlers;

import DTO.Request;
import DTO.StatsRequestDTO;
import Entity.Entity;
import Entity.Master;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Map;

public class StatsRequestHandler implements Handling{


    @Override
    public void handle(Entity entity, Socket connection, Request request, ObjectOutputStream out, ObjectInputStream in) {
        Master master = (Master) entity;
        StatsRequestDTO dto = (StatsRequestDTO) request;

        String type = dto.getType(); // "store" or "product"

        try{
            if("store".equalsIgnoreCase(type)){
                Map<?, Integer> storeStats = master.getStoreCategoryStats();
                out.writeObject(storeStats);
            }
            else if("product".equalsIgnoreCase(type)){
                Map<?, Integer> productStats = master.getProductCategoryStats();
                out.writeObject(productStats);
            }
            else{
                //out.writeObject("Invalid stats type: " + type);
            }
        }catch(IOException e){
            System.out.println("Failed to send stats to Manager: " + e.getMessage());
        }
    }
}
