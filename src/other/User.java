package other;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public abstract class User {

    //Console Menu
    protected static final Scanner scanner = new Scanner(System.in);

    //networking
    protected static final String MASTER_IP = "localhost";
    protected static final int MASTER_PORT = 55000;
    protected Socket connectionSocket;
    protected static ObjectOutputStream out;
    protected ObjectInputStream in;

    //Attributes
    private static int idCounter = 0;
    protected int id;

    public User() {
        id = ++idCounter;
    }

    protected abstract void showMenu();

    public abstract void establishConnection() throws IOException;

    protected void closeConnection() throws IOException {
        if (in != null) in.close();
        if (out != null) out.close();
        if (connectionSocket != null && !connectionSocket.isClosed()) connectionSocket.close();
    }
}
