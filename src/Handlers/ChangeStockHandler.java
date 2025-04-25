package Handlers;

import DTO.ChangeStockDTO;
import DTO.Request;
import Node.WorkerNode;
import Entity.*;
import other.Shop;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;


public class ChangeStockHandler implements Handling{

    @Override
    public void handle(Entity entity, Socket connection, Request request, ObjectOutputStream out, ObjectInputStream in) {
        ChangeStockDTO dto = (ChangeStockDTO) request;
        String storeName = dto.getStoreName();

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

                System.out.println("Forwarded Add/Remove product request to worker " + worker.getIp() + "Awaiting Response...");

                //Need to send back the updated shop
                Shop updateShop = (Shop) handler_in.readObject(); //Receive the update store.
                out.writeObject(updateShop);
                out.flush();
                System.out.println("Updated Shop send.");

                closeConnection(socket,handler_out,handler_in); //Close the connection with the worker.

            }catch (IOException e){
                System.out.println("Error forwarding to worker: " + e.getMessage());
                //out.writeObject("Error forwarding to worker: " + e.getMessage());
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        else if(entity instanceof Worker worker){
            Shop shop = worker.getShop(storeName);

            if(shop == null){
                System.out.println("Store not found: " + storeName);
                //out.writeObject("Store not found: " + storeName);
                return;
            }
            shop.getCatalog().changeStock(dto.getProductName(), dto.getNewStock());

            //Send shop to master
            try {
                out.writeObject(shop);
                out.flush();
            } catch (IOException e) {
                System.out.println("Error sending back to master the updated Shop " + e.getMessage());
            }
        }
        else
            System.err.println("Request forwarded  to wrong entity, Entity is not a Master or Worker");
    }
}
