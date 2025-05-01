package Handlers;

import DTO.FilterMapDTO;
import DTO.Request;
import DTO.SearchRequestDTO;
import Filtering.Filtering;
import Entity.Entity;
import Entity.Master;
import Node.WorkerNode;
import other.PendingRequests;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class SearchRequestHandler implements Handling{

    @Override
    public void handle(Entity entity, Socket connection, Request request, ObjectOutputStream out, ObjectInputStream in) {
        Master master = (Master) entity;
        SearchRequestDTO dto = (SearchRequestDTO) request;
        List<Filtering> filters = dto.getSelectedFilters();

        synchronized (master.pendingRequests) {
            master.pendingRequests.put(dto.getRequestId(), new PendingRequests(out, in));
        }
        List<WorkerNode> workers = master.getWorkersList();


        for(WorkerNode worker : workers){
            try{
                Socket socket = new Socket(worker.getIp(), worker.getPort());
                ObjectOutputStream outTOWorker = new ObjectOutputStream(socket.getOutputStream());
                FilterMapDTO filterMapDTO = new FilterMapDTO(filters, dto.getRequestId());

                outTOWorker.writeObject(filterMapDTO);
                outTOWorker.flush();
                socket.shutdownOutput();
            }
            catch(Exception e){
                System.out.println("an error occured on searchRequestHandler");
            }
        }
    }
}
