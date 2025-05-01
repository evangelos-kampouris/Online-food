package other;

import DTO.ReducerResultDTO;
import Entity.Reducer;
import Node.MasterNode;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ReducerShuffler {

    //Networking
    MasterNode masterNode;
    int requestID;

    private final Reducer reducer;
    private final Object send_lock = new Object();
    private final int totalWorkers; //The total workers in the infrastructure.
    private int receivedFromWorkersCounter = 0;
    Map<String, Shop> results = new HashMap<>(); //Data structure used to combine.

    private Thread senderThread;

    public ReducerShuffler(Reducer reducer, int requestID, MasterNode masterNode, int totalWorkers) {
        this.reducer = reducer;
        this.totalWorkers = totalWorkers;
        this.masterNode = masterNode;
        this.requestID = requestID;

    }

    /**
     * Kicks off the background thread that will wait for all workers
     * and then send the combined results.
     */
    public void start() {
        senderThread = new Thread(this::sendResultsRequest,
                "ReducerShuffler-" + requestID);
        senderThread.setDaemon(true); //Daemon for Graceful shutdown - if the sender thread is still blocked in wait() daemon thread will be killed automatically at JVM shutdown
        senderThread.start();
    }

    /**
     * Called by worker‐handler threads to merge partial results.
     */
    public void collect(Map<String, Shop> partial) {
        synchronized (send_lock) {
            // merge without overwriting
            partial.forEach(results::putIfAbsent);

            receivedFromWorkersCounter++;
            send_lock.notify();    // wake up senderThread if it’s waiting
        }
    }

    private void sendResultsRequest() {
        synchronized (send_lock) {
            while (receivedFromWorkersCounter < totalWorkers) {
                try {
                    send_lock.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }

        if(receivedFromWorkersCounter > totalWorkers) {
            System.err.println("ERROR: received " + receivedFromWorkersCounter + " workers from master");
        }
        List<Shop> resutlsList = results.values().stream().toList();
        try{
            Socket socket = new Socket(masterNode.getIP(), masterNode.getPort());
            ObjectOutputStream handler_out = new ObjectOutputStream(socket.getOutputStream());
            ReducerResultDTO resultRequest = new ReducerResultDTO(requestID, resutlsList);
            handler_out.writeObject(resultRequest);
            handler_out.flush();

        } catch (IOException e) {
            System.out.println("ERROR: couldn't send results request" + e.getMessage());
        }
        finally {
            //tell the reducer to drop this shuffler
            reducer.cleanupShuffler(requestID);
        }


    }
}
