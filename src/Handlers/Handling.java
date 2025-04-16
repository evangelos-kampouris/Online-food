package Handlers;

import DTO.Request;
import other.Entity;
import other.Master;

import java.net.Socket;

public interface Handling {

    public void handle(Entity entity, Socket connection, Request request);



}
