package Handlers;

import DTO.ReducerResultDTO;
import DTO.Request;
import Entity.*;
import Responses.ResponseDTO;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ReducerResultHandler implements Handling{

    @Override
    public void handle(Entity entity, Socket connection, Request request, ObjectOutputStream out, ObjectInputStream in) {
        //All the information has been sent from the reducer, and it's connection is no longer needed.
        closeConnection(connection, out, in);

        //Initialize values
        Master master = (Master) entity;
        ReducerResultDTO reducerResultDTO = (ReducerResultDTO) request;

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
