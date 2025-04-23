package Entity;

import Node.MasterNode;
import other.ReducerShuffler;
import other.Shop;

import java.util.HashMap;
import java.util.Map;

public class Reducer extends Entity {

    private final MasterNode masterNode;

    //MapResult Data
    //private final Object lock_shuffle = new Object(); //Used to lock updates in the data structure used to combine different results from the Workers.
    private final int totalWorkers; //The total workers in the infrastructure.



    private final Map<Integer, ReducerShuffler> reducerShufflers = new HashMap<>(); //RequestID - shuffle operation

    public Reducer(String IP, int PORT, MasterNode masterNode, int totalWorkers) {
        super(IP, PORT);
        this.masterNode = masterNode;
        this.totalWorkers = totalWorkers;
    }

    /**
     * Called by each MapResultHandler as results come in.
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

    /** Called by a shuffler when it’s completely done. */
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
}
