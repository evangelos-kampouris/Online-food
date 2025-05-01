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
        ObjectOutputStream handler_out = null;
        ObjectInputStream handler_in = null;

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

            try {
                Socket socket = new Socket(worker.getIp(), worker.getPort());
                handler_out = new ObjectOutputStream(socket.getOutputStream());
                handler_in = new ObjectInputStream(socket.getInputStream());

                handler_out.writeObject(dto);
                handler_out.flush();

                System.out.println("New Shop send."); //debug
            }
            catch(IOException e){
                System.out.println("Error forwarding to worker: " + e.getMessage());
                responseDTO = new ResponseDTO<>(false, "Error forwarding to worker: " + e.getMessage());
                try {
                    out.writeObject(responseDTO);
                    out.flush();
                } catch (IOException ex) {
                    throw new RuntimeException("[MASTER] Something went bad when writing to worker: " + ex.getMessage());
                }
            }

            try{
                System.out.println("Pending Response from Worker."); //debug
                //Wait and receive response
                ResponseDTO<Map<String, Shop>> response = (ResponseDTO<Map<String, Shop>>) handler_in.readObject();
                System.out.println("Response from the worker: " + response.isSuccess() + " " + response.getMessage()); //debug
                if(response.isSuccess())
                    responseDTO = response;
                else
                    responseDTO = new ResponseDTO<>(false, response.getMessage());

                out.writeObject(responseDTO);
                out.flush();
                System.out.println("Sending response back to manager.");
                //closeConnection(socket,handler_out,null); //Close the connection with the worker.
            }catch (IOException| ClassNotFoundException e) {
                try {
                    System.out.println("Somehting went wrong when expecting a new response. from the worker: " + e.getMessage());
                    responseDTO = new ResponseDTO<>(false, e.getMessage());
                    out.writeObject(responseDTO);
                    out.flush();
                    closeConnection(connection,out,in);
                } catch (IOException ex) {
                    System.out.println("Error sending back the error to the worker: " + ex.getMessage());
                }
            }
        }
        else if(entity instanceof Worker worker){
            if(worker.getShop(storeName) != null){
                responseDTO = new ResponseDTO<>(false, "Shop already exists");
            }
            else{
                worker.addShop(storeName, dto.getShop());
                responseDTO = new ResponseDTO<>(true, "Successfully added shop: " + storeName, worker.getShops());
            }

            try {
                System.out.println("Sending response back to Master."); //debug
                out.writeObject(responseDTO);
                out.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            finally{
                closeConnection(connection,out,in);
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
            finally{
                closeConnection(connection,out,in);
            }
        }
    }
}
