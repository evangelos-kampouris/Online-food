package User;


import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import Inventory.InventoryItem;
import Inventory.ShopInventoryItem;
import Responses.ResponseDTO;
import com.google.gson.Gson;
import DTO.*;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import other.ActionType;
import other.Product;
import other.ProductCategory;
import other.Shop;

public class Manager extends User {

    public final List<Shop> shopsReceivedOnInitialaziation = new ArrayList<Shop>();

    public Manager() {
        super();

    }

    public void readStore(int numberOfFilesToRead){
        for(int i = 1; i <= numberOfFilesToRead; i++){
            String filename = "store_" + i + ".json";
            Path path = Paths.get("Eshop","Resources", filename);
            System.out.println("reading file " + path.toString());

            try (FileReader fr = new FileReader(path.toFile())) {
                Shop shop = readShop(fr);

                //Prints the values read
                System.out.println("Loaded shop: " + shop.getName());
                System.out.println("Products in inventory:");
                shop.getCatalog().getInventory().forEach((name, item) ->
                        System.out.printf(" - %s: %d units (enabled=%b)%n", name, item.getQuantity(), ((ShopInventoryItem)item).isEnabled()));

                shops.put(shop.getName(), shop);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
//    public Shop readStoreByName(String fileName){
//        Path path = Paths.get("Eshop","Resources", fileName);
//        System.out.println("reading file " + path.toString());
//
//        try (FileReader fr = new FileReader(path.toFile())) {
//            Shop shop = readShop(fr);
//
//            //Prints the values read
//            System.out.println("Loaded shop: " + shop.getName());
//            System.out.println("Products in inventory:");
//            shop.getCatalog().getInventory().forEach((name, item) ->
//                    System.out.printf(" - %s: %d units (enabled=%b)%n", name, item.getQuantity(), ((ShopInventoryItem)item).isEnabled()));
//
//            addStore(shop.getName(), shop);
//            return shop;
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }

    @Override
    public void establishConnection() throws IOException {
        System.out.println("Initializing connection to Master...");

        connectionSocket = new java.net.Socket(MASTER_IP, MASTER_PORT);
        out = new java.io.ObjectOutputStream(connectionSocket.getOutputStream());
        in = new java.io.ObjectInputStream(connectionSocket.getInputStream());

        System.out.println("Connection to Master Achieved.");
        System.out.println("Sending all saved stores...");
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


//            Gson gson = new Gson();
//            AddStoreRequestDTO addStoreRequestDTO = gson.fromJson(reader, AddStoreRequestDTO.class);
//            sendRequest(addStoreRequestDTO);
//
//            //Wait and read response from server.
//            ResponseDTO<Map<String, Shop>> response = (ResponseDTO<Map<String, Shop>>) in.readObject();
//            System.out.println(response.getMessage());
//            if(response.isSuccess()) { //if successful update the store list
//                shops = response.getData();
//            }
//            else
//                System.out.println(response.getMessage());

    }

    private void performAddStoreRequest(Shop shop) {
        AddStoreRequestDTO request = new AddStoreRequestDTO(shop);
        try {
            sendRequest(request);
            System.out.println("Sending new store to Master: " + shop.getName());
            //Wait and read response from server.
            ResponseDTO<Map<String, Shop>> response = (ResponseDTO<Map<String, Shop>>) in.readObject();
            if(!response.isSuccess()){
                System.out.println("Failed to add shop: " + shop.getName() + "\n\t Reason: " + response.getMessage());
            }
            else{
                System.out.println("Successfully added shop: " + shop.getName());
            }

        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    private void performAddStoreRequest(Map<String, Shop> shopsFromInitialization) {
        for (Shop shop : shopsFromInitialization.values()) {
            AddStoreRequestDTO request = new AddStoreRequestDTO(shop);
            try {
                sendRequest(request);
                //Wait and read response from server.
                ResponseDTO<Map<String, Shop>> response = (ResponseDTO<Map<String, Shop>>) in.readObject();
                if(!response.isSuccess()){
                    System.out.println("Failed to add shop: " + shop.getName() + "\n\t Reason: " + response.getMessage() + "\nEnding request");
                    break;
                }
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println("All stores have been added.");
    }

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
            try {
                sendRequest(request);

                //Wait and read response from server.
                ResponseDTO<Shop> response = (ResponseDTO<Shop>) in.readObject();
                System.out.println(response.getMessage());

                if(response.isSuccess()) {
                    Shop updatedShop = response.getData(); //Update the store
                    shops.put(updatedShop.getName(), updatedShop);
                }
                else
                    System.out.println(response.getMessage());


            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Failed product request: \n" + e.getMessage());
            }
        }
    }

    private void changeStockOption() {
        System.out.print("Enter Store Name: ");
        String storeName = scanner.nextLine();

        System.out.print("Enter Product Name: ");
        String productName = scanner.nextLine();

        System.out.print("Enter New Stock Quantity: ");
        int newStock = scanner.nextInt();
        scanner.nextLine();

        ChangeStockDTO changeStockDTO = new ChangeStockDTO(storeName, productName, newStock);

        try {
            sendRequest(changeStockDTO);
            ResponseDTO<Shop> response = (ResponseDTO<Shop>) in.readObject();
            System.out.println(response.getMessage());

            if(response.isSuccess()) {
                System.out.println("Stock updated successfully.");
                Shop updatedShop = response.getData(); //Update the store
                shops.put(updatedShop.getName(), updatedShop);
            }
            else{
                System.out.println("Failed to update stock.");
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Failed to send stock change request: " + e.getMessage());
        }
    }

    private void viewSalesOption(String actionType) {

        StatsRequestDTO request = new StatsRequestDTO(actionType);

        try {
            sendRequest(request);

            ResponseDTO<Map<?, Integer>> response = (ResponseDTO<Map<?, Integer>>) in.readObject();
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
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Failed to send stats request: " + e.getMessage());
        }
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

    public static void main(String[] args) {
        Manager manager = new Manager();
        manager.readStore(10);

        //Let some time pass to initialize the Server Entities
        try {
            Thread.sleep(1100);
            manager.establishConnection();
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }

        boolean running = true;

//        while (running) {
//            manager.showMenu();
//            String choice = scanner.nextLine();
//
//            switch (choice) {
//                case "1":
//                    manager.addStoreOption();
//                    break;
//                case "2":
//                    manager.addRemoveProductOption();
//                    break;
//                case "3":
//                    manager.changeStockOption();
//                    break;
//                case "4":
//                    break;
//                case "5":
//                    manager.viewSalesOption("StoreCategories");
//                    break;
//                case "6":
//                    manager.viewSalesOption("ProductCategories");
//                    break;
//                case "0":
//                    running = false;
//                    try {
//                        manager.closeConnection();
//                        System.out.println("Connection closed.");
//                    } catch (IOException e) {
//                        System.out.println("Error closing connection: " + e.getMessage());
//                    }
//                    System.out.println("Exiting Manager...");
//                    break;
//                default:
//                    System.out.println("Invalid option. Please try again.");
//            }
//        }
    }
}