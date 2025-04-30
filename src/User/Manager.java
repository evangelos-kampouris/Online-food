package User;


import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

import Responses.ResponseDTO;
import com.google.gson.Gson;
import DTO.*;
import other.ActionType;
import other.ProductCategory;
import other.Shop;

public class Manager extends User {

    public Manager() {
        super();
    }

    @Override
    public void establishConnection() throws IOException {
        System.out.println("Initializing connection to Master...");

        connectionSocket = new java.net.Socket(MASTER_IP, MASTER_PORT);
        out = new java.io.ObjectOutputStream(connectionSocket.getOutputStream());
        in = new java.io.ObjectInputStream(connectionSocket.getInputStream());

        System.out.println("Connection to Master Achieved.");

        //TODO PERFORM A SEARCH to Get all the stores

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
        System.out.print("Enter path to Store JSON file: ");
        String jsonPath = scanner.nextLine();

        try (FileReader reader = new FileReader(jsonPath)) {
            Gson gson = new Gson();
            AddStoreRequestDTO addStoreRequestDTO = gson.fromJson(reader, AddStoreRequestDTO.class);
            sendRequest(addStoreRequestDTO);

            //Wait and read response from server.
            ResponseDTO<Map<String, Shop>> response = (ResponseDTO<Map<String, Shop>>) in.readObject();
            System.out.println(response.getMessage());
            if(response.isSuccess()) { //if successful update the store list
                shops = response.getData();
            }
            else
                System.out.println(response.getMessage());
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Failed to send store: " + e.getMessage());
        }
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

    public static void main(String[] args) {
        Manager manager = new Manager();
        //Let some time pass to initialize the Server Entities
        try {
            Thread.sleep(1100);
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