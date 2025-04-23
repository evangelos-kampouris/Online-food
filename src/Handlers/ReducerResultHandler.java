package Handlers;

import DTO.ReducerResultDTO;
import DTO.Request;
import Entity.*;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ReducerResultHandler implements Handling{

    @Override
    public void handle(Entity entity, Socket connection, Request request, ObjectOutputStream out, ObjectInputStream in) {
        Master master = (Master) entity;
        ReducerResultDTO reducerResultDTO = (ReducerResultDTO) request;

        ObjectOutputStream handler_out = master.pendingRequests.get(reducerResultDTO.getRequestId()).getOut();
        try {
            handler_out.writeObject(reducerResultDTO.getResults());
            handler_out.flush();
            master.pendingRequests.remove(reducerResultDTO.getRequestId());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
