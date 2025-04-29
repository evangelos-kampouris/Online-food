package Handlers;

import DTO.Request;
import DTO.StatsRequestDTO;
import Entity.Entity;
import Entity.Master;
import Responses.ResponseDTO;
import other.ProductCategory;
import other.StoreCategories;

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
            if("storeCategories".equalsIgnoreCase(type)){
                Map<StoreCategories, Integer> storeStats = master.getStoreCategoryStats();
                ResponseDTO<Map<StoreCategories, Integer>> response = new ResponseDTO<>(true, "Store sales retrieved successfully.", storeStats);
                out.writeObject(response);
                out.flush();
            }
            else if("productCategories".equalsIgnoreCase(type)){
                Map<ProductCategory, Integer> productCategories = master.getProductCategoryStats();
                ResponseDTO<Map<ProductCategory, Integer>> response = new ResponseDTO<>(true, "Product category sales retrieved successfully.", productCategories);
                out.writeObject(response);
                out.flush();
            }
            else{
                ResponseDTO<Object> response = new ResponseDTO<>(false, "Invalid stats type: " + type);
                out.writeObject(response);
                out.flush();
            }
        }catch(IOException e){
            System.out.println("Failed to send stats to Manager: " + e.getMessage());
        }
    }
}
