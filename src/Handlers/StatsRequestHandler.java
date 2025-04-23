package Handlers;

import DTO.Request;
import DTO.StatsRequestDTO;
import Node.WorkerNode;
import other.Entity;
import other.Master;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class StatsRequestHandler implements Handling{


    @Override
    public void handle(Entity entity, Socket connection, Request request, ObjectOutputStream out, ObjectInputStream in) {
        Master master = (Master) entity;
        StatsRequestDTO dto = (StatsRequestDTO) request;

        String type = dto.getType(); // "store" or "product"

        Map<String, Integer> aggregatedStats = new HashMap<>();

        Collection<WorkerNode> workers = master.workers.getAllNodes();
        for(WorkerNode worker : master.workers.getAllNodes()){
            try (Socket socket = new Socket(worker.getIp(), worker.getPort());
                 ObjectOutputStream handler_out = new ObjectOutputStream(socket.getOutputStream());
                 ObjectInputStream handler_in = new ObjectInputStream(socket.getInputStream())) {

                handler_out.writeObject(dto);
                handler_out.flush();

                Object response = handler_in.readObject();
                if(response instanceof Map<?, ?> workerStats){
                    for(Map.Entry<?, ?> entry : workerStats.entrySet()){
                        String key = (String) entry.getKey();
                        Integer value = (Integer) entry.getValue();
                        if (aggregatedStats.containsKey(key)) {
                            aggregatedStats.put(key, aggregatedStats.get(key) + value);
                        } else {
                            aggregatedStats.put(key, value);
                        }
                    }
                }
            }catch(IOException | ClassNotFoundException e){
                System.out.println("Failed to get stats from worker: " + worker.getIp() + " - " + e.getMessage());
            }
        }
        try {
            out.writeObject(aggregatedStats);
        } catch (IOException e) {
            System.out.println("Failed to send aggregated stats to manager: " + e.getMessage());
        }
    }
}
