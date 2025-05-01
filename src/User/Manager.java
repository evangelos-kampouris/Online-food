package User;


import DTO.*;
import Inventory.InventoryItem;
import Inventory.ShopInventoryItem;
import Responses.ResponseDTO;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import other.Product;
import other.ProductCategory;
import other.Shop;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Represents the Manager user who can upload shops, manage products, change stock,
 * and request sales statistics. Communicates with the MasterNode to register and update shop data.
 */
public class Manager extends User {

    public final List<Shop> shopsReceivedOnInitialaziation = new ArrayList<Shop>();

    /**
     * Initializes the Manager with an empty list of shops to be loaded from JSON.
     */
    public Manager() {
        super();

    }

    /**
     * Loads shop data from JSON files in the /Resources directory.
     * Parses and validates each shop, sets its pricing, and adds it to the local shop map.
     *
     * @param numberOfFilesToRead the number of JSON store files to read (e.g., store_1.json to store_n.json)
     */
    public void readStore(int numberOfFilesToRead){
        for(int i = 1; i <= numberOfFilesToRead; i++){
            String filename = "store_" + i + ".json";
            Path path = Paths.get("Resources", filename);
            System.out.println("reading file " + path.toString());

            try (FileReader fr = new FileReader(path.toFile())) {
                Shop shop = readShop(fr);
                float avg_price = shop.calculateAveragePrice();
                System.out.println("Average price: " + avg_price);//debug
                shop.setPrice(avg_price);

                //Prints the values read
                System.out.println("Rating: " + shop.getRating()); //debug
                System.out.println("Price: " + shop.getPrice());//debug
                System.out.println("Loaded shop: " + shop.getName());//debug
                System.out.println("Products in inventory:");//debug
                shop.getCatalog().getInventory().forEach((name, item) -> System.out.printf(" - %s: %d units (enabled=%b)%n", name, item.getQuantity(), ((ShopInventoryItem)item).isEnabled()));

                shops.put(shop.getName(), shop);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Establishes connection with the MasterNode and registers all loaded shops.
     *
     * @throws IOException if the connection fails
     */
    @Override
    public void establishConnection() throws IOException {
        System.out.println("Connecting to Master and sending necessary data....");
        performAddStoreRequest(shops);
    }

    @Override
    protected void showMenu(){
        System.out.println("\n--- Manager Menu ---");
        System.out.println("1. Add Store");
        System.out.println("2. Add/Remove Product");
        System.out.println("3. Change Stock");
        System.out.println("4. Updates stores"); //TODO
        System.out.println("5. View Sales by Store Category");
        System.out.println("6. View Sales by Product Category");
        System.out.println("0. Exit");
        System.out.print("Select an option: ");
    }

    /**
     * Prompts the user to provide a JSON filename for a new store,
     * reads and displays the shop, and sends it to the MasterNode for registration.
     */
    private void addStoreOption() {
        System.out.print("Enter the file path(fileName) of the json containing the Store: ");
        String fileName = scanner.nextLine();

        Path path = Paths.get("Eshop","Resources", fileName);
        System.out.println("reading file " + path.toString());

        try (FileReader fr = new FileReader(path.toFile())) {
            Shop shop = readShop(fr);

            //Prints the values read
            System.out.println("Loaded shop: " + shop.getName());
            System.out.println("Products in inventory:");
            shop.getCatalog().getInventory().forEach((name, item) ->
                    System.out.printf(" - %s: %d units (enabled=%b)%n", name, item.getQuantity(), ((ShopInventoryItem)item).isEnabled()));

            addStore(shop.getName(), shop);
            performAddStoreRequest(shop);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sends a single shop registration request to the MasterNode.
     *
     * @param shop the shop to be added
     */
    private void performAddStoreRequest(Shop shop) {
        AddStoreRequestDTO request = new AddStoreRequestDTO(shop);
        System.out.println("Sending new store to Master: " + shop.getName()); //debug
        ResponseDTO<Map<String, Shop>> response = (ResponseDTO<Map<String, Shop>>) sendAndReceiveRequest(request);
        if(!response.isSuccess()){
            System.out.println("Failed to add shop: " + shop.getName() + "\n\t Reason: " + response.getMessage());
        }
        else{
            System.out.println("Successfully added shop: " + shop.getName());
        }
    }

    /**
     * Sends a batch of shop registration requests to the MasterNode.
     *
     * @param shops the map of shop names to shop objects
     */
    private void performAddStoreRequest(Map<String, Shop> shopsFromInitialization) {
        boolean error_flag = false;
        for (Shop shop : shopsFromInitialization.values()) {
            AddStoreRequestDTO request = new AddStoreRequestDTO(shop);

            System.out.println("Sending new store to Master: " + shop.getName()); //debug
            ResponseDTO<Map<String, Shop>> response = (ResponseDTO<Map<String, Shop>>) sendAndReceiveRequest(request);
            if(!response.isSuccess()){
                System.out.println("Failed to add shop: " + shop.getName() + "\n\t Reason: " + response.getMessage() + "\nEnding request");
                error_flag = true;
                break;
            }
        }
        if(!error_flag){
            System.out.println("All stores have been added.");
        }
    }

    /**
     * Allows the manager to add or remove a product from a selected shop.
     * Interacts with the user to get all necessary input and sends the request to the server.
     */
    private void addRemoveProductOption() {
        System.out.print("Enter Store Name: ");
        String storeName = scanner.nextLine();

        System.out.print("Enter Product Name: ");
        String productName = scanner.nextLine();

        System.out.print("Enter Action (add/remove): ");
        String actionInput = scanner.nextLine().toLowerCase();

        Request request = null;

        if (actionInput.equals("add")) {
            System.out.print("Select Quantity: ");
            int quantity = scanner.nextInt();
            scanner.nextLine();

            ProductCategory productCategory = null;
            double price;

            while (true) {
                System.out.print("Enter Product Category: ");
                String categoryInput = scanner.nextLine().toUpperCase();
                try {
                    productCategory = ProductCategory.valueOf(categoryInput);
                    break;
                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid product category. Try again.");
                }
            }

            System.out.print("Enter Product Price: ");
            price = scanner.nextDouble();
            scanner.nextLine();

            request = new AddProductDTO(storeName, productName, productCategory, price, quantity);

        } else if (actionInput.equals("remove")) {
            request = new RemoveProductDTO(storeName, productName);
        } else {
            System.out.println("Invalid action. Please enter 'add' or 'remove'.");
            return;
        }

        if (request != null) {
            ResponseDTO<Shop> response = (ResponseDTO<Shop>) sendAndReceiveRequest(request);

            System.out.println(response.getMessage());
            if(response.isSuccess()) {
                Shop updatedShop = response.getData(); //Update the store
                shops.put(updatedShop.getName(), updatedShop);
            }
            else
                System.out.println(response.getMessage());

        }
    }

    /**
     * Allows the manager to change the stock quantity of a specific product in a shop.
     */
    private void changeStockOption() {
        System.out.print("Enter Store Name: ");
        String storeName = scanner.nextLine();

        System.out.print("Enter Product Name: ");
        String productName = scanner.nextLine();

        System.out.print("Enter New Stock Quantity: ");
        int newStock = scanner.nextInt();
        scanner.nextLine();

        ChangeStockDTO changeStockDTO = new ChangeStockDTO(storeName, productName, newStock);
        ResponseDTO<Shop> response = (ResponseDTO<Shop>) sendAndReceiveRequest(changeStockDTO);

        System.out.println(response.getMessage());
        if(response.isSuccess()) {
            System.out.println("Stock updated successfully.");
            Shop updatedShop = response.getData(); //Update the store
            shops.put(updatedShop.getName(), updatedShop);
        }
        else{
            System.out.println("Failed to update stock.");
        }
    }

    /**
     * Requests and displays sales statistics from the server, either by store category or product category.
     *
     * @param actionType either "StoreCategories" or "ProductCategories"
     */
    private void viewSalesOption(String actionType) {
        StatsRequestDTO request = new StatsRequestDTO(actionType);
        ResponseDTO<Map<?, Integer>> response = (ResponseDTO<Map<?, Integer>>) sendAndReceiveRequest(request);

        System.out.println(response.getMessage());
        if (response.isSuccess()) {
            Map<?, Integer> data = response.getData();

            if(data == null || data.isEmpty()) {
                System.out.println("No sales data found for: " + actionType);
                return;
            }

            System.out.println("Sales breakdown:");
            for (Map.Entry<?, Integer> entry : data.entrySet()) {
                System.out.println(entry.getKey() + ": " + entry.getValue());
            }
        }
        else
            System.out.println(response.getMessage());
    }

    private static final Gson GSON = new GsonBuilder()
            // ensure every InventoryItem becomes a ShopInventoryItem
            .registerTypeAdapter(InventoryItem.class,
                    (JsonDeserializer<InventoryItem>)(json, type, ctx) -> {
                        JsonObject obj = json.getAsJsonObject();
                        Product p   = ctx.deserialize(obj.get("product"), Product.class);
                        int qty     = obj.get("quantity").getAsInt();
                        boolean en  = obj.get("enabled").getAsBoolean();
                        return new ShopInventoryItem(p, qty, en);
                    })
            .create();

    /**
     * Reads a Shop from JSON.
     *
     * @param reader a Reader over JSON of the form:
     *   {
     *     "name": ...,
     *     "productCategory": [...],
     *     "storeCategory": ...,
     *     "numberOfRatings": ...,
     *     "rating": "...",
     *     "coordinates": {"lat":..., "lng":...},
     *     "logoPath": ...,
     *     "numberOfProducts": ...,
     *     "catalog": { "inventory": { ... } }
     *   }
     * @return a fully populated Shop, with ShopInventoryItems
     */
    public static Shop readShop(Reader reader) {
        return GSON.fromJson(reader, Shop.class);
    }

    /**
     * Prints the name and coordinates of all loaded shops to the console.
     */
    public void printShopsCords() {
        for (Shop shop : shops.values()) {
            System.out.println(shop.getName() + " " + shop.getCoordinates().toString());
        }
    }

    /**
     * Entry point for launching the Manager interface.
     * Loads initial shops, establishes connection, and presents a menu for user interaction.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        Manager manager = new Manager();
        manager.readStore(10);
        manager.printShopsCords();


        //Let some time pass to initialize the Server Entities
        try {
            Thread.sleep(5100);
            manager.establishConnection();
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }

        boolean running = true;

        while (running) {
            manager.showMenu();
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    manager.addStoreOption();
                    break;
                case "2":
                    manager.addRemoveProductOption();
                    break;
                case "3":
                    manager.changeStockOption();
                    break;
                case "4":
                    break;
                case "5":
                    manager.viewSalesOption("StoreCategories");
                    break;
                case "6":
                    manager.viewSalesOption("ProductCategories");
                    break;
                case "0":
                    running = false;
                    try {
                        manager.closeConnection();
                        System.out.println("Connection closed.");
                    } catch (IOException e) {
                        System.out.println("Error closing connection: " + e.getMessage());
                    }
                    System.out.println("Exiting Manager...");
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }
}