package Handlers;

import DTO.FilterMapDTO;
import DTO.Request;
import DTO.SearchRequestDTO;
import Filtering.Filtering;
import other.Entity;
import other.Master;
import Node.WorkerNode;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

public class SearchRequestHandler implements Handling{

    @Override
    public void handle(Entity entity, Socket connection, Request request, ObjectOutputStream out, ObjectInputStream in) {
        Master master = (Master) entity;
        SearchRequestDTO dto = (SearchRequestDTO) request;
        List<Filtering> filters = dto.getSelectedFilters();

        List<WorkerNode> workers = master.getWorkersList();

        //TODO DO IT WITH THREADS https://chatgpt.com/c/6808fc2c-b11c-8008-9b35-9f8e4dc00705

        for(WorkerNode worker : workers){
            try(Socket socket = new Socket(worker.getIp(), worker.getPort())){
                ObjectOutputStream outTOWorker = new ObjectOutputStream(socket.getOutputStream());
                FilterMapDTO filterMapDTO = new FilterMapDTO(filters, dto.getRequestId());

                outTOWorker.writeObject(filterMapDTO);
                outTOWorker.flush();

                closeConnection(socket,outTOWorker,null);
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }


    }
}
