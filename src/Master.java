import com.google.gson.Gson;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Master{
    private final String IP = "localhost";
    private final int PORT = 55000;

    private ServerSocket serverSocket;

    private final String worker_config_filepath = "Worker_config.json";
    private List<Worker> workers = new ArrayList<>(); //TEMPORARY DATA STRUCTURE

    public Master() throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT); //TODO NEEDS REWORK
        System.out.println("Server started - Listening on port " + PORT); //logging.
        initiateWorkers();
    }


    /**
     * @throws IOException
     */
    private void acceptConnections() throws IOException {
        while(!serverSocket.isClosed()) {
            Socket connectionSocket = serverSocket.accept();
            System.out.println("Accepted connection from " + connectionSocket.getRemoteSocketAddress());

            //handle the connection
            Runnable handler = new Handler(this, connectionSocket);
            Thread thread = new Thread(handler);
            thread.start();

        }
    }
    /**
     * Reads who the workers are from the file
     */
    private void initiateWorkers() {
        try (FileReader reader = new FileReader(worker_config_filepath)) {
            Gson gson = new Gson();
            WorkerConfigWrapper wrapper = gson.fromJson(reader, WorkerConfigWrapper.class);
            this.workers = wrapper.getWorkers();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
