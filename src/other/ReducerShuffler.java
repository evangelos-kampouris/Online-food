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

/**
 * Coordinates the reduction phase in a distributed search.
 * Collects partial results from workers and sends the combined result to the MasterNode.
 * Uses a background thread to wait until all workers respond.
 */
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

    /**
     * Initializes the shuffler for a specific reducer and request.
     *
     * @param reducer the reducer that owns this shuffler
     * @param requestID the unique identifier for this search request
     * @param masterNode the master node to send final results to
     * @param totalWorkers the number of expected worker responses
     */
    public ReducerShuffler(Reducer reducer, int requestID, MasterNode masterNode, int totalWorkers) {
        this.reducer = reducer;
        this.totalWorkers = totalWorkers;
        this.masterNode = masterNode;
        this.requestID = requestID;

    }

    /**
     * Starts a background thread that waits for all worker responses
     * and sends the combined results to the master.
     */
    public void start() {
        senderThread = new Thread(this::sendResultsRequest,
                "ReducerShuffler-" + requestID);
        senderThread.setDaemon(true); //Daemon for Graceful shutdown - if the sender thread is still blocked in wait() daemon thread will be killed automatically at JVM shutdown
        senderThread.start();
    }

    /**
     * Merges partial results from a worker into the combined result set.
     * Called by worker handler threads.
     *
     * @param partial the partial map of shop results from a worker
     */
    public void collect(Map<String, Shop> partial) {
        synchronized (send_lock) {
            // merge without overwriting
            partial.forEach(results::putIfAbsent);

            receivedFromWorkersCounter++;
            send_lock.notify();    // wake up senderThread if it’s waiting
        }
    }

    /**
     * Waits until all workers have submitted results, then sends the combined list to the master node.
     * Also instructs the reducer to clean up this shuffler after completion.
     */
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
            handler_out.flush(); //TODO MISSING RESPONSE

        } catch (IOException e) {
            System.out.println("ERROR: couldn't send results request" + e.getMessage());
        }
        finally {
            //tell the reducer to drop this shuffler
            reducer.cleanupShuffler(requestID);
        }


    }
}
