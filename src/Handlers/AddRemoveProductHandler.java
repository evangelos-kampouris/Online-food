package Handlers;

import DTO.AddRemoveProductDTO;
import DTO.Request;
import Node.WorkerNode;
import Entity.Entity;
import Entity.Master;
import Entity.Worker;
import other.ActionType;
import other.Product;
import other.ProductCategory;
import other.Shop;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;


public class AddRemoveProductHandler implements Handling{

    @Override
    public void handle(Entity entity, Socket connection, Request request, ObjectOutputStream out, ObjectInputStream in){

        AddRemoveProductDTO dto = (AddRemoveProductDTO) request;
        String storeName = dto.getStoreName();

        try{
            if(entity instanceof Master master){
                WorkerNode worker = master.workers.getNodeForKey(storeName);

                if(worker == null){
                    System.out.println("No Worker found for store: " + storeName);
                    //out.writeObject("No worker found for store: " + storeName);
                    return;
                }

                try (Socket socket = new Socket(worker.getIp(), worker.getPort());
                     ObjectOutputStream handler_out = new ObjectOutputStream(socket.getOutputStream());
                     ObjectInputStream handler_in = new ObjectInputStream(socket.getInputStream())) {

                    handler_out.writeObject(dto);
                    handler_out.flush();

                    System.out.println("Forwarded Add/Remove product request to worker " + worker.getIp());
                    //out.writeObject("Request forwarded to worker.");

                }catch (IOException e){
                    System.out.println("Error forwarding to worker: " + e.getMessage());
                    //out.writeObject("Error forwarding to worker: " + e.getMessage());
                }
            }
            else if(entity instanceof Worker worker){
                Shop shop = worker.getShop(storeName);

                if(shop == null){
                    System.out.println("Store not found: " + storeName);
                    //out.writeObject("Store not found: " + storeName);
                    return;
                }

                if(dto.getAction() == ActionType.ADD){
                    ProductCategory category = ProductCategory.valueOf(dto.getProductCategory().toUpperCase());
                    Product product = new Product(dto.getProductName(), category, dto.getPrice());
                    shop.getCatalog().addProduct(product.getName(), product, 100);

                    System.out.println("Added product to store: " + dto.getProductName());
                    //out.writeObject("Product added successfully: " + dto.getProductName());
                }
                else if(dto.getAction() == ActionType.REMOVE){
                    shop.getCatalog().setItemEnableStatus(dto.getProductName(), false);
                    System.out.println("Removed product from store: " + dto.getProductName());
                    //out.writeObject("Product removed successfully: " + dto.getProductName());
                }
                else{
                    //out.writeObject("Invalid action type.");
                }
            }
        }catch (IOException e){
            System.out.println("Exception in AddRemoveProductHandler: " + e.getMessage());
            try {
                out.writeObject("Handler error: " + e.getMessage());
            } catch (IOException ex) {
                System.out.println("Failed to send error message back to client.");
            }
        }
    }
}
