package User;

import DTO.Request;
import other.Shop;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public abstract class User {

    //Console Menu
    protected static final Scanner scanner = new Scanner(System.in);

    //networking
    protected static final String MASTER_IP = "localhost";
    protected static final int MASTER_PORT = 9999;
    protected Socket connectionSocket;
    protected static ObjectOutputStream out;
    protected ObjectInputStream in;

    //Attributes
    private static int idCounter = 0;
    protected int id;
    //Shop Name, Shop -- Received upon  initialization - Updated on search.
    Map<String, Shop> shops = new HashMap<>();

    public User() {
        id = ++idCounter;
    }

    public abstract void establishConnection() throws IOException, ClassNotFoundException;

    protected abstract void showMenu();

    protected void sendRequest(Request request) throws IOException{
        out.writeObject(request);
        out.flush();
    }

    protected void closeConnection() throws IOException {
        if (in != null) in.close();
        if (out != null) out.close();
        if (connectionSocket != null && !connectionSocket.isClosed()) connectionSocket.close();
    }
}
