package Handlers;

import DTO.AddStoreRequestDTO;
import DTO.Request;
import Node.WorkerNode;
import Entity.*;
import other.Shop;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class AddStoreRequestHandler implements Handling{

    @Override
    public void handle(Entity entity, Socket connection, Request request, ObjectOutputStream out, ObjectInputStream in) {
        AddStoreRequestDTO dto = (AddStoreRequestDTO) request;
        String storeName = dto.getShop().getName();

        if(entity instanceof Master master){
            WorkerNode worker = master.workers.getNodeForKey(storeName);

            if(worker == null){
                System.out.println("No Worker found for store: " + storeName);
                //out.writeObject("No worker found for store: " + storeName);
                return;
            }

            try (Socket socket = new Socket(worker.getIp(), worker.getPort());
                 ObjectOutputStream handler_out = new ObjectOutputStream(socket.getOutputStream())){

                handler_out.writeObject(dto);
                handler_out.flush();

                System.out.println("New Shop send.");

                closeConnection(socket,handler_out,null); //Close the connection with the worker.

            }catch (IOException e){
                System.out.println("Error forwarding to worker: " + e.getMessage());
                //out.writeObject("Error forwarding to worker: " + e.getMessage());
            }

        }
        else if(entity instanceof Worker worker){
            worker.addShop(storeName, dto.getShop());
        }
        else{
            //out.writeObject("Invalid action type.");
            System.out.println("Wrong entity type.");
        }










//        if (worker == null) {
//            System.out.println("No available worker for store: " + storeName);
//            return;
//        }
//        try (Socket socket = new Socket(worker.getIp(), worker.getPort());
//             ObjectOutputStream handler_out = new ObjectOutputStream(socket.getOutputStream());
//             ObjectInputStream handler_in = new ObjectInputStream(socket.getInputStream())) {
//
//            handler_out.writeObject(shop);
//            handler_out.flush();
//
//            System.out.println("Store '" + storeName + "' sent to worker at " + worker.getIp() + ":" + worker.getPort());
//            //out.writeObject("Store added successfully: " + storeName);
//
//        } catch (IOException e) {
//            System.out.println("Failed to send store to worker: " + e.getMessage());
//            try {
//                //out.writeObject("Error sending store to worker: " + e.getMessage());
//            } catch (IOException ex) {
//                throw new RuntimeException(ex);
//            }
//        }
    }
}
