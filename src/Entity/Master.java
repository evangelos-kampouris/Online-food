package Entity;

import Node.WorkerNode;
import Wrappers.*;
import com.google.gson.Gson;
import other.HashRing;
import other.PendingRequests;
import other.ProductCategory;
import other.StoreCategories;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Master extends Entity {
    //Workers
    private final String worker_config_filepath = "Worker_config.json";
    private List<WorkerNode> workersList;
    public HashRing workers;  //Ο Master χρησιμοποιεί έναν HashRing για να κάνει κατανομή καταστημάτων στους Workers
    private final int VIRTUAL_NODES_OF_WORKER = 2;  /*Για κάθε Worker, ο HashRing βάζει δύο αντίγραφα του worker στο δαχτυλίδι,
                                                      για να έχουμε καλύτερη κατανομή φορτίου.*/

    //stats
    Map<StoreCategories, Integer> storeCategoryStat = new HashMap<>();       //Κρατάει πόσες πωλήσεις έγιναν ανά τύπο καταστήματος (π.χ. "Pizzeria" → 100 πωλήσεις).
    Map<ProductCategory, Integer> productCategoryStat = new HashMap<>();     //Κρατάει πόσες πωλήσεις έγιναν ανά τύπο προϊόντος (π.χ. "Pizza" → 300 πωλήσεις).

    public Map<Integer, PendingRequests> pendingRequests = new HashMap<>(); // <RequestID, ObjectInput/OutputStreams>

    //Constructor
    public Master(String IP, int PORT) {
        super(IP, PORT);
        initiateWorkers();
        workers = new HashRing(workersList, VIRTUAL_NODES_OF_WORKER);
    }

    public Map<StoreCategories, Integer> getStoreCategoryStats() {
        return storeCategoryStat;
    }
    public Map<ProductCategory, Integer> getProductCategoryStats() {
        return productCategoryStat;
    }

    /**
     * Reads who the workers are from the file
     */
    private void initiateWorkers() {
        try (FileReader reader = new FileReader(worker_config_filepath)) {
            Gson gson = new Gson();                                               //μετατροπή JSON σε Java αντικείμενα
            WorkerConfigWrapper wrapper = gson.fromJson(reader, WorkerConfigWrapper.class); //τα μετατρέπει σε ένα Java αντικείμενο τύπου WorkerConfigWrapper
            this.workersList = wrapper.getWorkers();    //Παίρνει από το wrapper τη λίστα των Workers
            for(WorkerNode worker : workersList) { //Adds the workers to the Hashring
                workers.addNode(worker);
            }
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

    public static void main(String[] args) {
        Master master = new Master("127.0.0.1", 9999);
        try {
            master.acceptConnections();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
