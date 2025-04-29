package Handlers;

import DTO.AddStoreRequestDTO;
import DTO.Request;
import Node.WorkerNode;
import Entity.*;
import Responses.ResponseDTO;
import other.Shop;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Map;

public class AddStoreRequestHandler implements Handling{

    @Override
    public void handle(Entity entity, Socket connection, Request request, ObjectOutputStream out, ObjectInputStream in) {
        AddStoreRequestDTO dto = (AddStoreRequestDTO) request;
        String storeName = dto.getShop().getName();
        ResponseDTO<Map<String, Shop>> responseDTO = null;

        if(entity instanceof Master master){
            WorkerNode worker = master.workers.getNodeForKey(storeName);

            if(worker == null){
                System.out.println("No Worker found for store: " + storeName);
                responseDTO = new ResponseDTO<>(false, "No Worker found for store:");
                try {
                    out.writeObject(responseDTO);
                    out.flush();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                return;
            }

            try (Socket socket = new Socket(worker.getIp(), worker.getPort());
                 ObjectOutputStream handler_out = new ObjectOutputStream(socket.getOutputStream());
                 ObjectInputStream handler_in = new ObjectInputStream(socket.getInputStream())){

                handler_out.writeObject(dto);
                handler_out.flush();

                System.out.println("New Shop send.");
                //Wait and receive response
                ResponseDTO<Map<String, Shop>> response = (ResponseDTO<Map<String, Shop>>) handler_in.readObject();
                if(response.isSuccess())
                    responseDTO = response;
                else
                    responseDTO = new ResponseDTO<>(false, response.getMessage());

                out.writeObject(responseDTO);
                out.flush();
                closeConnection(socket,handler_out,null); //Close the connection with the worker.

            }catch (IOException e){
                System.out.println("Error forwarding to worker: " + e.getMessage());
                //out.writeObject("Error forwarding to worker: " + e.getMessage());
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        else if(entity instanceof Worker worker){
            worker.addShop(storeName, dto.getShop());
            responseDTO = new ResponseDTO<>(true, "Successfully added shop: " + storeName, worker.getShops());
            try {
                out.writeObject(responseDTO);
                out.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        else{
            System.out.println("Wrong entity type.");
            responseDTO = new ResponseDTO<>(false, "Wrong entity type");
            try {
                out.writeObject(responseDTO);
                out.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
