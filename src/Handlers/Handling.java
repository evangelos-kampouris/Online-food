package Handlers;

import DTO.Request;
import other.Entity;
import other.Master;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

public interface Handling extends Serializable {

    public void handle(Entity entity, Socket connection, Request request, ObjectOutputStream out, ObjectInputStream in);



}
