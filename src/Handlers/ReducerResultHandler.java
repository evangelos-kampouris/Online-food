package Handlers;

import DTO.ReducerResultDTO;
import DTO.Request;
import Entity.Entity;
import Entity.Master;
import Responses.ResponseDTO;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Handles the final search results sent from a ReducerNode back to the MasterNode.
 * Forwards the results to the original client that initiated the search request.
 */
public class ReducerResultHandler implements Handling{

    /**
     * Processes a ReducerResultDTO received from a ReducerNode.
     * Sends the results back to the client through the output stream stored in pending requests.
     *
     * @param entity the MasterNode receiving the reduced results
     * @param connection the socket from the ReducerNode (closed immediately)
     * @param request the DTO containing the aggregated search results
     * @param out unused output stream
     * @param in unused input stream
     */
    @Override
    public void handle(Entity entity, Socket connection, Request request, ObjectOutputStream out, ObjectInputStream in) {
        //All the information has been sent from the reducer, and it's connection is no longer needed.
        closeConnection(connection, out, in);

        //Initialize values
        Master master = (Master) entity;
        ReducerResultDTO reducerResultDTO = (ReducerResultDTO) request;

        synchronized (master.pendingRequests) {
            ObjectOutputStream handler_out = master.pendingRequests.get(reducerResultDTO.getRequestId()).getOut();
            try {
                ResponseDTO<Request> response = new ResponseDTO<>(true, "Search Completed", reducerResultDTO);
                handler_out.writeObject(response);
                handler_out.flush();
                master.pendingRequests.remove(reducerResultDTO.getRequestId());
            } catch (IOException e) {
                System.err.println("An error occured while handling the SEARCH request. Master to Client.");
            }
        }
    }
}
