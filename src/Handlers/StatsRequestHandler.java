package Handlers;

import DTO.Request;
import DTO.StatsRequestDTO;
import Entity.Entity;
import Entity.Master;
import Responses.ResponseDTO;
import other.ProductCategory;
import other.Stats;
import other.StoreCategories;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Map;

/**
 * Handles requests for sales statistics, either by store category or product category.
 * Executed on the MasterNode and responds directly to the Manager with the relevant data.
 */
public class StatsRequestHandler implements Handling{

    /**
     * Processes a StatsRequestDTO and sends back the requested statistics.
     * Supports store category and product category statistics.
     *
     * @param entity the MasterNode handling the request
     * @param connection the socket used for communication
     * @param request the request specifying the type of statistics needed
     * @param out the output stream used to send the response
     * @param in the input stream (not used)
     */
    @Override
    public void handle(Entity entity, Socket connection, Request request, ObjectOutputStream out, ObjectInputStream in) {
        Master master = (Master) entity;
        StatsRequestDTO dto = (StatsRequestDTO) request;

        String type = dto.getType(); // "store" or "product"

        try{
            if("storeCategories".equalsIgnoreCase(type)){
                Map<StoreCategories, Stats> storeStats = master.getStoreCategoryStats();
                Stats stats = storeStats.get(StoreCategories.fromValue(dto.getCategory()));
                ResponseDTO<Stats> response = new ResponseDTO<>(true, "Store sales retrieved successfully.", stats);
                out.writeObject(response);
                out.flush();
            }
            else if("productCategories".equalsIgnoreCase(type)){
                Map<ProductCategory, Stats> productCategoriesStats = master.getProductCategoryStats();
                Stats stats = productCategoriesStats.get(ProductCategory.fromValue(dto.getCategory()));
                ResponseDTO<Stats> response = new ResponseDTO<>(true, "Product category sales retrieved successfully.", stats);
                out.writeObject(response);
                out.flush();
            }
            else{
                ResponseDTO<Stats> response = new ResponseDTO<>(false, "Invalid stats type: " + type);
                out.writeObject(response);
                out.flush();
            }
        }catch(IOException e){
            System.out.println("Failed to send stats to Manager: " + e.getMessage());
        }
    }
}
