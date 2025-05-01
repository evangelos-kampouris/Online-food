package Entity;

import Node.MasterNode;
import other.ReducerShuffler;
import other.Shop;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents the ReducerNode responsible for aggregating filtered results from WorkerNodes.
 * Maintains a map of active shuffle operations and forwards final results to the MasterNode.
 */
public class Reducer extends Entity {

    private final MasterNode masterNode;

    //MapResult Data
    //private final Object lock_shuffle = new Object(); //Used to lock updates in the data structure used to combine different results from the Workers.
    private final int totalWorkers; //The total workers in the infrastructure.

    private final Map<Integer, ReducerShuffler> reducerShufflers = new HashMap<>(); //RequestID - shuffle operation

    /**
     * Initializes the ReducerNode with network settings and reference to the MasterNode.
     *
     * @param IP the IP address to bind
     * @param PORT the port to listen on
     * @param masterNode the master node to send final reduced results to
     * @param totalWorkers the number of expected workers contributing to each shuffle
     */
    public Reducer(String IP, int PORT, MasterNode masterNode, int totalWorkers) {
        super(IP, PORT);
        this.masterNode = masterNode;
        this.totalWorkers = totalWorkers;
    }

    /**
     * Receives partial results from a WorkerNode and registers them to the appropriate shuffle operation.
     * Starts a new ReducerShuffler thread if this is the first result for the given request.
     *
     * @param requestID the unique ID of the original search request
     * @param partialResult the filtered shops returned from a WorkerNode
     */
    public void shuffle(int requestID, Map<String, Shop> partialResult) {
        ReducerShuffler shuffler;
        synchronized (reducerShufflers) {
            shuffler = reducerShufflers.computeIfAbsent(requestID, id -> {
                ReducerShuffler rs = new ReducerShuffler(this, requestID, masterNode, totalWorkers);
                rs.start();                   // explicit start of its waiter thread
                return rs;
            });
        }
        shuffler.collect(partialResult);
    }

    /**
     * Called by a ReducerShuffler after completing its operation.
     * Removes the completed shuffle from the active map.
     *
     * @param requestId the ID of the completed request
     */
    public void cleanupShuffler(int requestId) {
        synchronized (reducerShufflers) {
            reducerShufflers.remove(requestId);
        }
    }

    public int getMASTER_PORT() {return masterNode.getPort();}

    public String getMASTER_IP() {return masterNode.getIp();}

    public int getTotalWorkers() {
        return totalWorkers;
    }

    public static void main(String[] args) {
        MasterNode masterNode = new MasterNode(args[2], Integer.parseInt(args[3]));
        Reducer reducer = new Reducer(args[0], Integer.parseInt(args[1]), masterNode, 3);
        try {
            reducer.acceptConnections();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
