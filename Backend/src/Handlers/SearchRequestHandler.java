package Handlers;

import DTO.FilterMapDTO;
import DTO.Request;
import DTO.SearchRequestDTO;
import Entity.Entity;
import Entity.Master;
import Filtering.Filtering;
import Node.WorkerNode;
import other.PendingRequests;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

/**
 * Handles search requests from clients.
 * Distributes the filtering request to all WorkerNodes and registers the pending request
 * to associate the final response from the Reducer.
 */
public class SearchRequestHandler implements Handling{

    /**
     * Processes a SearchRequestDTO containing filtering criteria.
     * Registers the client's request for later response and forwards the filters to all WorkerNodes.
     *
     * @param entity the MasterNode receiving the request
     * @param connection the socket from the client (kept open)
     * @param request the search request with filters
     * @param out the client's output stream to be stored for later reply
     * @param in the client's input stream to be stored for later reply
     */
    @Override
    public void handle(Entity entity, Socket connection, Request request, ObjectOutputStream out, ObjectInputStream in) {
        System.out.println("Received a search request from: " + connection.getRemoteSocketAddress());
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
                System.out.println("an error occured on searchRequestHandler" + e.getMessage());
            }
        }
    }
}
