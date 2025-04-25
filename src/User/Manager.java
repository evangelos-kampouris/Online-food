package User;


import java.io.FileReader;
import java.io.IOException;

import com.google.gson.Gson;
import DTO.*;
import other.ActionType;
import other.Shop;

public class Manager extends User {

    @Override
    public void establishConnection() throws IOException {
        System.out.println("Initializing connection to Master...");

        connectionSocket = new java.net.Socket(MASTER_IP, MASTER_PORT);
        out = new java.io.ObjectOutputStream(connectionSocket.getOutputStream());
        in = new java.io.ObjectInputStream(connectionSocket.getInputStream());

        System.out.println("Connection to Master Achieved.");
    }

    @Override
    protected void showMenu(){
        System.out.println("\n--- Manager Menu ---");
        System.out.println("1. Add Store");
        System.out.println("2. Add/Remove Product");
        System.out.println("3. Change Stock");
        System.out.println("4. View Sales by Store Category");
        System.out.println("5. View Sales by Product Category");
        System.out.println("0. Exit");
        System.out.print("Select an option: ");
    }

    public static void main(String[] args) {
        Manager manager = new Manager();

        try {
            manager.establishConnection();
        } catch (IOException e) {
            throw new RuntimeException("Failed to connect to Master", e);
        }

        boolean running = true;

        while (running) {
            manager.showMenu();
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    System.out.print("Enter path to Store JSON file: ");
                    String jsonPath = scanner.nextLine();

                    try (FileReader reader = new FileReader(jsonPath)) {
                        Gson gson = new Gson();
                        AddStoreRequestDTO addStoreRequestDTO = gson.fromJson(reader, AddStoreRequestDTO.class);

                        manager.sendRequest(addStoreRequestDTO);

                        System.out.println("Store sent successfully.");
                    } catch (IOException e) {
                        System.out.println("Failed to send store: " + e.getMessage());
                    }
                    break;

                case "2":
                    System.out.print("Enter Store Name: ");
                    String storeName2 = scanner.nextLine();

                    System.out.print("Enter Product Name: ");
                    String productName2 = scanner.nextLine();

                    System.out.print("Enter Action (add/remove): ");
                    String actionInput = scanner.nextLine().toLowerCase();

                    ActionType action;

                    if (actionInput.equals("add")) {
                        action = ActionType.ADD;
                    }
                    else if (actionInput.equals("remove")) {
                        action = ActionType.REMOVE;
                    }
                    else {
                        System.out.println("Invalid action. Please enter 'add' or 'remove'.");
                        break;
                    }

                    String productCategory = "";
                    double price = 0.0;

                    if (action == ActionType.ADD) {
                        System.out.print("Enter Product Category: ");
                        productCategory = scanner.nextLine();

                        System.out.print("Enter Product Price: ");
                        price = scanner.nextDouble();
                        scanner.nextLine();
                    }

                    AddRemoveProductDTO addRemoveProductDTO = new AddRemoveProductDTO(storeName2, productName2, action, productCategory, price);

                    try {
                        manager.sendRequest(addRemoveProductDTO);

                        System.out.println("Product " + action + " request sent successfully.");
                    } catch (IOException e) {
                        System.out.println("Failed to send product request: " + e.getMessage());
                    }
                    break;

                case "3":
                    System.out.print("Enter Store Name: ");
                    String storeName3 = scanner.nextLine();

                    System.out.print("Enter Product Name: ");
                    String productName3 = scanner.nextLine();

                    System.out.print("Enter New Stock Quantity: ");
                    int newStock = scanner.nextInt();

                    scanner.nextLine();

                    ChangeStockDTO changeStockDTO = new ChangeStockDTO(storeName3, productName3, newStock);

                    try {
                        manager.sendRequest(changeStockDTO);
                        System.out.println("Stock change request sent successfully.");
                    } catch (IOException e) {
                        System.out.println("Failed to send stock change request: " + e.getMessage());
                    }

                    //Shop updatedShop = (Shop) in.readObject(); //TODO UPDATE TO WAIT AND RECEIVE THE NEW OBJECT

                    break;

                case "4":
                    StatsRequestDTO statsRequestByStore = new StatsRequestDTO("store");

                    try {
                        manager.sendRequest(statsRequestByStore);
                        System.out.println("Request to view sales by store category sent successfully.");
                    } catch (IOException e) {
                        System.out.println("Failed to send stats request: " + e.getMessage());
                    }
                    break;

                case "5":
                    StatsRequestDTO statsRequestByProduct = new StatsRequestDTO("product");

                    try {
                        manager.sendRequest(statsRequestByProduct);
                        System.out.println("Request to view sales by product category sent successfully.");
                    } catch (IOException e) {
                        System.out.println("Failed to send stats request: " + e.getMessage());
                    }
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