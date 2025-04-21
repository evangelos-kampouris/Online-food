package other;

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
    protected ObjectOutputStream out;
    protected ObjectInputStream in;

    //Attributes
    private static int idCounter = 0;
    protected int id;

    public User() {
        id = ++idCounter;
    }

    protected abstract void showMenu();
}
