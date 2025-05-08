package Entity;

import Node.WorkerNode;
import Wrappers.WorkerConfigWrapper;
import com.google.gson.Gson;
import other.HashRing;
import other.PendingRequests;
import other.ProductCategory;
import other.StoreCategories;
import other.Stats;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Accepts incoming socket connections in a loop.
 * For each connection, a new thread is created using a Handler instance.
 *
 * @throws IOException if the server socket fails to accept a connection
 */
public class Master extends Entity {
    //Workers
    private String worker_config_filepath;
    private List<WorkerNode> workersList;
    public HashRing workers;                        //Ο Master χρησιμοποιεί έναν HashRing για να κάνει κατανομή καταστημάτων στους Workers
    private final int VIRTUAL_NODES_OF_WORKER = 2;  /*Για κάθε Worker, ο HashRing βάζει δύο αντίγραφα του worker στο δαχτυλίδι,
                                                      για να έχουμε καλύτερη κατανομή φορτίου.*/

    //stats
    Map<StoreCategories, Stats> storeCategoryStat = new HashMap<>();       //Κρατάει πόσες πωλήσεις έγιναν ανά τύπο καταστήματος (π.χ. "Pizzeria" → 100 πωλήσεις).
    Map<ProductCategory, Stats> productCategoryStat = new HashMap<>();     //Κρατάει πόσες πωλήσεις έγιναν ανά τύπο προϊόντος (π.χ. "Pizza" → 300 πωλήσεις).

    public final Map<Integer, PendingRequests> pendingRequests = new HashMap<>(); // <RequestID, ObjectInput/OutputStreams>

    //Constructor
    /**
     * Initializes the MasterNode with the specified IP and port.
     * Loads the worker configuration and sets up the hash ring.
     *
     * @param IP the IP address of the master
     * @param PORT the port number to listen on
     */
    public Master(String IP, int PORT) {
        super(IP, PORT);
        initiateWorkers();
        workers = new HashRing(workersList, VIRTUAL_NODES_OF_WORKER);
    }


    public Map<StoreCategories, Stats> getStoreCategoryStats() {
        return storeCategoryStat;
    }
    public Map<ProductCategory, Stats> getProductCategoryStats() {
        return productCategoryStat;
    }

    /**
     * Loads worker node configuration from the JSON file and stores it in a list.
     * Used during initialization to populate the hash ring.
     */
    private void initiateWorkers() {

        System.out.println(System.getProperty("user.dir"));//debugging.
        Path path = Paths.get("Eshop", "Resources", "WorkerConfig.json");
        System.out.println(path);//debugging.

        try (FileReader reader = new FileReader(path.toFile())) {
            Gson gson = new Gson();                                               //μετατροπή JSON σε Java αντικείμενα
            WorkerConfigWrapper wrapper = gson.fromJson(reader, WorkerConfigWrapper.class); //τα μετατρέπει σε ένα Java αντικείμενο τύπου WorkerConfigWrapper
            this.workersList = wrapper.getWorkers();    //Παίρνει από το wrapper τη λίστα των Workers
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates the store-level sales statistics by adding the given number of sales.
     *
     * @param storeCategory the type of store (e.g., PIZZERIA)
     * @param sales the number of sales to add
     */
    public void addStatsStoreCategory(StoreCategories storeCategory, String store_name, int sales) {
        if(storeCategoryStat.containsKey(storeCategory)) {
            storeCategoryStat.get(storeCategory).addStat(store_name, sales);
        }
        else {
            storeCategoryStat.put(storeCategory, new Stats(store_name, sales));
        }
    }

    /**
     * Increments the sales count for the given store category by one.
     *
     * @param storeCategory the store category to update
     */
    //public void addStatsStoreCategory(StoreCategories storeCategory){addStatsStoreCategory(storeCategory, 1);}

    /**
     * Updates the product-level sales statistics by adding the given number of sales.
     *
     * @param productCategory the type of product (e.g., PIZZA, SUSHI)
     * @param sales the number of units sold to add
     */
    public void addStatsProductCategory(ProductCategory productCategory, String store_name, int sales) {
        Stats stats = new Stats(store_name, sales);
        if(productCategoryStat.containsKey(productCategory)) {
            productCategoryStat.get(productCategory).addStat(store_name, sales);
        }
        else {
            productCategoryStat.put(productCategory, new Stats(store_name, sales));
        }
    }

    /**
     * Increments the sales count for the given product category by one.
     *
     * @param productCategory the product category to update
     */
    //public void addStatsProductCategory(ProductCategory productCategory){addStatsProductCategory(productCategory, 1);}

    public List<WorkerNode> getWorkersList() {return workersList;}

    public static void main(String[] args) {
        Master master = new Master(args[0], Integer.parseInt(args[1]));
        try {
            master.acceptConnections();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
