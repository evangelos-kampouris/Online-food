package other;

import DTO.BuyRequestDTO;
import DTO.SearchRequestDTO;
import Filtering.*;
import Inventory.InventoryCart;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.*;


public class Client extends User{

    //Attributes
    InventoryCart cart;
    //Shop Name, Shop's catalog -- Received upon  initialization - Updated on search.
    Map<String, Shop> shops;



    public Client() {
        super();
        this.cart = new InventoryCart();
    }


    //Networking
    @Override
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
    private void performPurchase(Shop selectedShop) throws IOException {
        BuyRequestDTO buyRequestDTO = new BuyRequestDTO(selectedShop, cart);
        out.writeObject(buyRequestDTO);
        out.flush();
        closeConnection(); //close the connection
    }

    private void performSearch(List<Filtering> filters) throws IOException{
        SearchRequestDTO searchRequestDTO = new SearchRequestDTO(filters);
        out.writeObject(searchRequestDTO);
        out.flush();
        closeConnection(); //close the connection
    }


    //Console Menu
   @Override
    protected void showMenu() {
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

    private void searchMenuOption() throws IOException{
        boolean finished = false;
        List<Filtering> selectedFilters = new ArrayList<>();

        do{
            System.out.println("Select a Filter(Price/Rating/Food Category): ");
            String filter = scanner.nextLine();
            filter = filter.toLowerCase();

            switch (filter){
                case "price":
                    System.out.println("Select a price: [$, $$, $$$]");
                    String price_str = scanner.nextLine();
                    Price selectedPrice;

                    switch (price_str) {
                        case "$":
                            selectedPrice = Price.LOW;
                            break;
                        case "$$":
                            selectedPrice = Price.MEDIUM;
                            break;
                        case "$$$":
                            selectedPrice = Price.HIGH;
                            break;
                        default:
                            System.out.println("Invalid price selection.");
                            return;
                    }

                    FilterPrice filterPrice = new FilterPrice(selectedPrice);

                    //Checking if the same filter has been applied.
                    if(!selectedFilters.contains(filterPrice))
                        selectedFilters.add(filterPrice);
                    else
                        System.out.println("Rating filter already selected.");
                    break;

                case "rating":
                    System.out.println("Select a rating: (Float 1.0-5.0");
                    String rating_str = scanner.nextLine();
                    float rating_float = Float.parseFloat(rating_str);

                    //Check input
                    if (rating_float < 1.0 || rating_float > 5.0) {
                        System.out.println("Rating must be between 1.0 and 5.0.");
                        return;
                    }

                    Rating selectedRating;

                    switch (rating_str) {
                        case "1":
                            selectedRating = Rating.ONE_STAR;
                            break;
                        case "1.5":
                            selectedRating = Rating.ONE_HALF_STAR;
                            break;
                        case "2":
                            selectedRating = Rating.TWO_STARS;
                            break;
                        case "2.5":
                            selectedRating = Rating.TWO_HALF_STARS;
                            break;
                        case "3":
                            selectedRating = Rating.THREE_STARS;
                            break;
                        case "3.5":
                            selectedRating = Rating.THREE_HALF_STARS;
                            break;
                        case "4":
                            selectedRating = Rating.FOUR_STARS;
                            break;
                        case "4.5":
                            selectedRating = Rating.FOUR_HALF_STARS;
                            break;
                        case "5":
                            selectedRating = Rating.FIVE_STARS;
                            break;
                        default:
                            System.out.println("Invalid rating. Please enter one of: 1, 1.5, ..., 5");
                            return;
                    }

                    FilterRating rating = new FilterRating(selectedRating);

                    //Checking if the same filter has been applied.
                    if(!selectedFilters.contains(rating))
                        selectedFilters.add(rating);
                    else
                        System.out.println("Rating filter already selected.");
                    break;


                case "food category":
                    System.out.println("Select a food Category: ");
                    String foodCategory = scanner.nextLine();

                    ProductCategory selectedFoodCategory;

                    switch (foodCategory) {
                        case "souvlaki":
                            selectedFoodCategory = ProductCategory.SOUVLAKI;
                            break;
                        case "burger":
                            selectedFoodCategory = ProductCategory.BURGER;
                            break;
                        case "pizza":
                            selectedFoodCategory = ProductCategory.PIZZA;
                            break;
                        case "crepe":
                            selectedFoodCategory = ProductCategory.CREPE;
                            break;
                        case "coffee":
                            selectedFoodCategory = ProductCategory.COFFEE;
                            break;
                        case "sushi":
                            selectedFoodCategory = ProductCategory.SUSHI;
                            break;
                        case "sandwich":
                            selectedFoodCategory = ProductCategory.SANDWICH;
                            break;
                        case "pasta":
                            selectedFoodCategory = ProductCategory.PASTA;
                            break;
                        case "brunch":
                            selectedFoodCategory = ProductCategory.BRUNCH;
                            break;
                        case "steak":
                            selectedFoodCategory = ProductCategory.STEAK;
                            break;
                        case "soup":
                            selectedFoodCategory = ProductCategory.SOUP;
                            break;
                        case "tea":
                            selectedFoodCategory = ProductCategory.TEA;
                            break;
                        case "waffle":
                            selectedFoodCategory = ProductCategory.WAFFLE;
                            break;
                        case "ice cream":
                            selectedFoodCategory = ProductCategory.ICE_CREAM;
                            break;
                        case "tacos":
                            selectedFoodCategory = ProductCategory.TACOS;
                            break;
                        case "beverage":
                            selectedFoodCategory = ProductCategory.BEVERAGE;
                            break;
                        case "pie":
                            selectedFoodCategory = ProductCategory.PIE;
                            break;
                        default:
                            System.out.println("Invalid category. Please choose a valid food category.");
                            return;
                    }

                    FilterFoodCategory filterCategory = new FilterFoodCategory(selectedFoodCategory);

                    //Checking if the same filter has been applied.
                    if(!selectedFilters.contains(filterCategory))
                        selectedFilters.add(filterCategory);
                    else
                        System.out.println("Rating filter already selected.");
                    break;

                default:
                    System.out.println("Invalid Filter. Select either price, rating, or food Category.");
                    break;
            }
            System.out.println("Select another filter?: ");
            String answer = scanner.nextLine();
            if (answer.equalsIgnoreCase("N")) finished = true;
        } while(!finished);

        performSearch(selectedFilters);

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
            client.showMenu();
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    try {
                        client.searchMenuOption();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
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
                    try {
                        client.closeConnection();
                    } catch (IOException e) {
                        System.out.println("An error occurred while closing connection.");
                    }
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }


}
