package other;

import DTO.BuyRequestDTO;
import Inventory.InventoryCart;
import Inventory.ShopInventory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.*;

public class Client extends User{

    //Attributes
    InventoryCart cart;
    //Shop Name, Shop's catalog -- Received upon  initialization - Updated on search.
    Map<String, Shop> shops = new HashMap<>();

    public Client() {
        super();
        this.cart = new InventoryCart();
    }


    //Networking
    public void establishConnection() throws IOException {
        System.out.println("Initializing connection to Master...");

        connectionSocket = new Socket(MASTER_IP, MASTER_PORT);

        out = new ObjectOutputStream(connectionSocket.getOutputStream());
        in = new ObjectInputStream(connectionSocket.getInputStream());

        System.out.println("Connection to Master Achieved.");

        //TODO PERFORM A SEARCH FOR SHOPS IN 5KM.
    }

    private static void searchStores() {

    }

    private static void rateStore(/*String storeName*/) {
    }

    private static void buyProducts() {

    }

    public void addToCart(Product product, int quantity) {
        cart.addProduct(product.getName(), product, quantity);
    }
    public void addToCart(Product product) {
        addToCart(product, 1);
    }

    //TODO NEEDS FIXING
//    /**
//     * @param product
//     *
//     * Removes the product from the cart COMPLETELY.
//     */
    public void removeFromCart(Product product) {
        cart.removeProduct(product.getName(), null);
    }

//    /**
//     * @param product
//     *
//     * Removes the product from the cart COMPLETELY.
//     */
//    public void removeFromCart(Product product, int quantity) {
//        cart.removeProduct(product.getName());
//    }

    /**
     * @throws IOException
     *
     * Send the buy request.
     */
    public void performPurchase(Shop selectedShop) throws IOException {
        BuyRequestDTO buyRequestDTO = new BuyRequestDTO(selectedShop, cart);
        out.writeObject(buyRequestDTO);
        out.flush();

        //close the connection
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (connectionSocket != null && !connectionSocket.isClosed()) connectionSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //Console Menu
    private static void showMenu() {
        System.out.println("\n--- Menu ---");
        System.out.println("1. Search for Stores");
        System.out.println("2. Buy Products");
        System.out.println("3. Rate a Store");
        System.out.println("0. Exit");
        System.out.print("Select an option: ");
    }

    /**
     * @throws IOException
     *
     * Holds the logic of the Buy Option of the Menu.
     */
    public void buyMenuOption() throws IOException {
        boolean finished = false;

        System.out.println("Select a Store: ");
        String storeName = scanner.nextLine();
        Shop shop = shops.get(storeName);

        do{
            System.out.println("Select a product to buy: ");
            String productName = scanner.nextLine();
            Product product = shops.get(storeName).getCatalog().getProduct(productName);

            System.out.println("Select a quantity to buy: ");
            int quantity = scanner.nextInt();
            scanner.nextLine();

            addToCart(product, quantity);

            System.out.println("Want to buy anything else? [Y/N]");
            String answer = scanner.nextLine();
            if (answer.equalsIgnoreCase("N")) finished = true;
        } while(!finished);

        System.out.println("Total Cost: " + cart.getCost());

        performPurchase(shop);

        System.out.println("Purchase Completed.");
    }


    public static void main(String[] args) {
        //Create a client object
        Client client = new Client();
        try {
            client.establishConnection();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        boolean running = true;

        System.out.println("=== Welcome to the Food Delivery Platform (Client Mode) ===");

        while (running) {
            showMenu();
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    searchStores(); //TODO
                    break;
                case "2": //Implemented
                    try {
                        client.buyMenuOption();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                case "3": //TODO
                    rateStore();
                    break;
                case "0":
                    running = false;
                    System.out.println("Exiting Client Console. Goodbye!");
                    //TODO CLOSE CONNECITON
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }
}
