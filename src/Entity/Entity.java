package Entity;

import other.Handler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Abstract base class for all server-side entities (Master, Worker, Reducer).
 * Manages server socket initialization and connection handling.
 */
public abstract class Entity {

    //Networking
    protected final String IP;
    protected final int PORT;
    protected ServerSocket serverSocket;

    /**
     * Initializes the server entity with IP and port configuration.
     * Starts a server socket to listen for incoming connections.
     *
     * @param IP the IP address of the server
     * @param PORT the port number on which to listen
     */
    public Entity(String IP, int PORT) {
        this.IP = IP;
        this.PORT = PORT;

        try{
            this.serverSocket = new ServerSocket(PORT);
        }
        catch(IOException e){
            System.err.println("An error occurred while creating the server socket. Exiting.");
            System.exit(-1);
        }
        System.out.println("Server started - Listening on port " + PORT); //logging.
    }

    /**
     * Accepts incoming socket connections in a loop.
     * For each connection, a new thread is created using a Handler instance.
     *
     * @throws IOException if the server socket fails to accept a connection
     */
    protected void acceptConnections() throws IOException {
        while(!serverSocket.isClosed()) {
            Socket connectionSocket = serverSocket.accept();
            System.out.println("Accepted connection from " + connectionSocket.getRemoteSocketAddress());  //Τυπώνει ποιος συνδέθηκε (IP address και port του client)

            //handle the connection
            Runnable handler = new Handler(this, connectionSocket);  //
            Thread thread = new Thread(handler);                           /*καινούργιο νήμα για να τρέξει αυτόν τον Handler
                                                                             Έτσι κάθε πελάτης εξυπηρετείται σε δικό του thread*/
            thread.start();
        }
    }

    public String getIP() {return IP;}

    public int getPORT() {return PORT;}
}
