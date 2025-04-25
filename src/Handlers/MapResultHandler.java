package Handlers;

import DTO.MapResultDTO;
import DTO.Request;
import Entity.*;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

//Received mapResultDTO from Workers
public class MapResultHandler implements Handling{


    @Override
    public void handle(Entity entity, Socket connection, Request request, ObjectOutputStream out, ObjectInputStream in) {
        Reducer reducer = (Reducer) entity;
        MapResultDTO mapResultDTO = (MapResultDTO) request;

        reducer.shuffle(mapResultDTO.getRequestId(), mapResultDTO.getMapResult()); //sending the final result is done by the reducer.

        closeConnection(connection, out, in); //Close the connection from worker to Reducer.
    }
}
