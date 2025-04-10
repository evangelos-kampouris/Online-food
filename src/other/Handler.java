package other;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Handler implements Runnable, Handling{
    Socket connection;
    Master master;
    ObjectOutputStream out;
    ObjectInputStream in;

    public Handler(Master master, Socket connection){
        this.connection = connection;
        this.master = master;
        try {
            out = new ObjectOutputStream(connection.getOutputStream());
            in = new ObjectInputStream(connection.getInputStream());
        } catch (IOException e) {
            System.out.println(e);
        }
    }
    /**
     * Holds the handling of different requests. Each handling request is a class.
     *
     */
    public void handle(Master master, Socket connection){

    }


    @Override
    public void run() {
        //handle(this.master, this.connection);
        while (connection.isConnected()) {

        }
    }
}
