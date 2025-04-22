package other;


import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;
import com.google.gson.Gson;
import DTO.*;

public class Manager extends User{

    private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 55000;

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

        //ftiaksimo koitakse USER
        try (Socket socket = new Socket(SERVER_IP, SERVER_PORT)) {
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            Scanner scanner = new Scanner(System.in);
        } catch (IOException e) {
            throw new RuntimeException(e);
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

                        out.writeObject(addStoreRequestDTO);
                        out.flush();

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
                    String action = scanner.nextLine().toLowerCase();

                    String productCategory = "";
                    double price = 0.0;

                    if (action.equals("add")) {
                        System.out.print("Enter Product Category: ");
                        productCategory = scanner.nextLine();

                        System.out.print("Enter Product Price: ");
                        price = scanner.nextDouble();
                        scanner.nextLine();
                    }

                    AddRemoveProductDTO addRemoveProductDTO = new AddRemoveProductDTO(storeName2, productName2, action, productCategory, price);

                    try {
                        out.writeObject(addRemoveProductDTO);
                        out.flush();
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
                        out.writeObject(changeStockDTO);
                        out.flush();
                        System.out.println("Stock change request sent successfully.");
                    } catch (IOException e) {
                        System.out.println("Failed to send stock change request: " + e.getMessage());
                    }
                    break;

                case "4":
                    StatsRequestDTO statsRequestByStore = new StatsRequestDTO("store");

                    try {
                        out.writeObject(statsRequestByStore);
                        out.flush();
                        System.out.println("Request to view sales by store category sent successfully.");
                    } catch (IOException e) {
                        System.out.println("Failed to send stats request: " + e.getMessage());
                    }
                    break;

                case "5":
                    StatsRequestDTO statsRequestByProduct = new StatsRequestDTO("product");

                    try {
                        out.writeObject(statsRequestByProduct);
                        out.flush();
                        System.out.println("Request to view sales by product category sent successfully.");
                    } catch (IOException e) {
                        System.out.println("Failed to send stats request: " + e.getMessage());
                    }
                    break;

                case "0":
                    running = false;
                    System.out.println("Exiting Manager...");
                    break;

                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }
}
