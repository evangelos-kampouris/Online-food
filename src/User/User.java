package User;

import DTO.Request;
import Responses.ResponseDTO;
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
    protected static final String MASTER_IP = "127.0.0.1";
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

    protected ResponseDTO<?> sendAndReceiveRequest(Request request){
        ResponseDTO<?> response = null;
        try {
            connectionSocket = new Socket(MASTER_IP, MASTER_PORT);
            out = new java.io.ObjectOutputStream(connectionSocket.getOutputStream());
            in = new java.io.ObjectInputStream(connectionSocket.getInputStream());

            System.out.println("Sending request to Port:" + connectionSocket.getPort());
            out.writeObject(request);
            out.flush();

            //Wait and read response from server.
            System.out.println("Awaiting response..."); //debug
            response = (ResponseDTO<?>) in.readObject();
            System.out.println("Response: " + response.isSuccess() + " " + response.getMessage()); //debug
            System.out.println("Closing connection...");
            closeConnection();

            return response;
        }
        catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return new ResponseDTO<>(false, getClass() + "] An error occured either while sending or receiving a request.");
    }

    protected void closeConnection() throws IOException {
        if (out != null) out.close();
        if (in != null) in.close();
        if (connectionSocket != null && !connectionSocket.isClosed()) connectionSocket.close();
    }

    public void addStore(String storeName, Shop shop) {
        if (shop == null) {
            System.err.println("Shop is null");
            return;
        }
        if(shops.get(storeName) != null) {
            System.err.println("Shop already exists: " + storeName);
        }
        shops.put(storeName, shop);
    }
}
