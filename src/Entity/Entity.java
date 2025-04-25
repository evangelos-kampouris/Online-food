package Entity;

import other.Handler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public abstract class Entity {

    //Networking
    protected final String IP;
    protected final int PORT;
    protected ServerSocket serverSocket;

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
     * Accepts the Inbound connections creating a new thread for each on of them and calls handler.
     * @throws IOException
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
