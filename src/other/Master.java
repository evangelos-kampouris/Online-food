package other;

import Node.WorkerNode;
import com.google.gson.Gson;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Master extends Entity{

    //Networking
    private final String IP = "localhost";
    private final int PORT = 55000;
    private final ServerSocket serverSocket;

    //Workers
    private final String worker_config_filepath = "Worker_config.json";
    private List<WorkerNode> workersList;
    public HashRing workers;
    private final int VIRTUAL_NODES_OF_WORKER = 2;

    //stats
    Map<StoreCategories, Integer> storeCategoryStat = new HashMap<>();
    Map<ProductCategory, Integer> productCategoryStat = new HashMap<>();



    //Constructor
    public Master() throws IOException {
        this.serverSocket = new ServerSocket(PORT); //TODO NEEDS REWORK
        System.out.println("Server started - Listening on port " + PORT); //logging.
        initiateWorkers();
        workers = new HashRing(workersList, VIRTUAL_NODES_OF_WORKER);
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
            this.workersList = wrapper.getWorkers();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * @param storeCategory
     * @param sales
     *
     * Adds the store related statistics. Sale Should be equal to 1, expect editing in volume.
     */
    public void addStatsStoreCategory(StoreCategories storeCategory, int sales) {
        if(storeCategoryStat.containsKey(storeCategory)) {
            storeCategoryStat.put(storeCategory, storeCategoryStat.get(storeCategory) + sales);
        }
        else {
            storeCategoryStat.put(storeCategory, sales);
        }
    }

    /**
     * @param storeCategory
     * Adds the store related statistics.
     */
    public void addStatsStoreCategory(StoreCategories storeCategory){addStatsStoreCategory(storeCategory, 1);}

    /**
     * @param productCategory
     * @param sales
     * Adds the product related statistics. Sale Should be equal to 1, expect editing in volume.
     */
    public void addStatsProductCategory(ProductCategory productCategory, int sales) {
        if(productCategoryStat.containsKey(productCategory)) {
            productCategoryStat.put(productCategory, productCategoryStat.get(productCategory) + sales);
        }
        else {
            productCategoryStat.put(productCategory, sales);
        }
    }

    /**
     * @param productCategory
     * Adds the product related statistics.
     */
    public void addStatsProductCategory(ProductCategory productCategory){addStatsProductCategory(productCategory, 1);}

    public List<WorkerNode> getWorkersList() {return workersList;}
}
