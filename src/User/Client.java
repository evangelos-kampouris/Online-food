package User;

import DTO.*;
import Filtering.*;
import Inventory.InventoryCart;
import Responses.ResponseDTO;
import other.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a client user who can search for shops, rate them, and make purchases.
 * Handles user interaction, cart management, and communication with the MasterNode.
 */
public class Client extends User {

    //Attributes
    InventoryCart cart;
    private final Coordinates coordinates;

    /**
     * Initializes a client with specific coordinates.
     *
     * @param coordinates the geographic location of the client
     */
    public Client(Coordinates coordinates) {
        super();
        this.cart = new InventoryCart();
        this.coordinates = coordinates;
    }

    //Networking
    @Override
    public void establishConnection() throws IOException, ClassNotFoundException {
        System.out.println("Connecting to Master Achieved and Receiving necessary data....");

        //PERFORM A SEARCH FOR SHOPS IN 5KM.
        FilterCords km5search = new FilterCords(5.0f, coordinates);
        performSearch(List.of(km5search));
    }

    public void addToCart(Product product, int quantity) {
        cart.addProduct(product.getName(), product, quantity);
    }
    public void addToCart(Product product) {
        addToCart(product, 1);
    }

    /**
     * Sends a purchase request to the server for the selected shop.
     * If the transaction succeeds, the cart is cleared.
     *
     * @param selectedShop the shop from which the user wants to buy
     */
    private void performPurchase(Shop selectedShop){
        BuyRequestDTO buyRequestDTO = new BuyRequestDTO(selectedShop, cart);
        ResponseDTO<Request> response = (ResponseDTO<Request>) sendAndReceiveRequest(buyRequestDTO);
        System.out.println(response.getMessage());
        if(response.isSuccess()) {
            cart.clearInventory();
        }
    }

    /**
     * Performs a shop search using the provided filters and updates the local shop map with results.
     *
     * @param filters the list of filters to apply (e.g., distance, rating, category)
     * @throws IOException if communication with the server fails
     * @throws ClassNotFoundException if the response type is not recognized
     */
    private void performSearch(List<Filtering> filters) throws IOException, ClassNotFoundException {
        SearchRequestDTO searchRequestDTO = new SearchRequestDTO(filters);
        List<Shop> receivedShops  = null; //The response forwards the shops in a list.

        Object receivedObject = sendAndReceiveRequest(searchRequestDTO);

        if (receivedObject instanceof ResponseDTO searchResponse) {
            ReducerResultDTO dto = (ReducerResultDTO) searchResponse.getData();
            receivedShops = dto.getResults();
        }

        if (receivedShops != null) {
            shops.clear();
            for (Shop shop : receivedShops) {
                String shopName = shop.getName();
                System.out.println("Received shop: " + shopName);
                shops.put(shopName, shop);
            }
        }
    }

    /**
     * Sends a rating for a specific store to the server.
     *
     * @param storeName the name of the store to rate
     * @param rating the rating to assign to the store
     */
    private void performRating(String storeName, Rating rating) {
        RateStoreRequestDTO rateRequest = new RateStoreRequestDTO(storeName, rating);

        ResponseDTO<Request> response = (ResponseDTO<Request>) sendAndReceiveRequest(rateRequest);
        if(response.isSuccess()) {
            System.out.println("Successfully sent rating for store.");
        }
        else{
            System.out.println(response.getMessage());
        }
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
     * Handles the user flow for purchasing products.
     * Guides the user through selecting a shop, choosing products and quantities, and completing the transaction.
     *
     * @throws IOException if the purchase request fails
     */
    public void buyMenuOption() throws IOException {
        boolean finished = false;

        System.out.println("Select a Store: ");
        String storeName = scanner.nextLine();
        Shop shop = shops.get(storeName);

        if (shop == null) {
            System.out.println("Store not found.");
            return;
        }

        do{
            System.out.println("Select a product to buy: ");
            String productName = scanner.nextLine();
            Product product = shops.get(storeName).getCatalog().getProduct(productName);

            if (product == null) {
                System.out.println("Product not found.");
                continue;  // skip rest of loop, ask again
            }

            System.out.println("Select a quantity to buy: ");
            int quantity = scanner.nextInt();
            scanner.nextLine();

            if (quantity <= 0) {
                System.out.println("Quantity must be positive.");
                continue;
            }

            addToCart(product, quantity);

            System.out.println("Want to buy anything else? [Y/N]");
            String answer = scanner.nextLine();
            if (answer.equalsIgnoreCase("N")) finished = true;
        } while(!finished);

        System.out.println("Total Cost: " + cart.getCost());

        performPurchase(shop);

        System.out.println("Purchase Completed.");
    }

    /**
     * Prompts the user to apply filters and performs a filtered shop search.
     *
     * @throws IOException if communication with the server fails
     * @throws ClassNotFoundException if the server response is invalid
     */
    public void searchMenuOption() throws IOException, ClassNotFoundException {
        boolean finished = false;
        List<Filtering> selectedFilters = new ArrayList<>();

        do{
            System.out.println("Select a Filter(Price/Rating/Food Category): ");
            String filter = scanner.nextLine();
            filter = filter.toLowerCase();

            switch (filter) {
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
                    if (!selectedFilters.contains(filterPrice))
                        selectedFilters.add(filterPrice);
                    else
                        System.out.println("Filter already selected.");
                    break;

                case "rating":
                    System.out.println("Select a rating: (Float 1.0-5.0");
                    String rating_str = scanner.nextLine();
                    float rating_float = Float.parseFloat(rating_str);

                    //Check input
                    if (rating_float < 1.0f || rating_float > 5.0f) {
                        System.out.println("Rating must be between 1.0 and 5.0.");
                        return;
                    }

                    Rating selectedRating;

                    switch (rating_str) {
                        case "1.0":
                            selectedRating = Rating.ONE_STAR;
                            break;
                        case "1.5":
                            selectedRating = Rating.ONE_HALF_STAR;
                            break;
                        case "2.0":
                            selectedRating = Rating.TWO_STARS;
                            break;
                        case "2.5":
                            selectedRating = Rating.TWO_HALF_STARS;
                            break;
                        case "3.0":
                            selectedRating = Rating.THREE_STARS;
                            break;
                        case "3.5":
                            selectedRating = Rating.THREE_HALF_STARS;
                            break;
                        case "4.0":
                            selectedRating = Rating.FOUR_STARS;
                            break;
                        case "4.5":
                            selectedRating = Rating.FOUR_HALF_STARS;
                            break;
                        case "5.0":
                            selectedRating = Rating.FIVE_STARS;
                            break;
                        default:
                            System.out.println("Invalid rating. Please enter one of: 1, 1.5, ..., 5");
                            return;
                    }

                    FilterRating rating = new FilterRating(selectedRating);

                    //Checking if the same filter has been applied.
                    if (!selectedFilters.contains(rating))
                        selectedFilters.add(rating);
                    else
                        System.out.println("Rating filter already selected.");
                    break;

                case "food category":
                    ProductCategory selectedFoodCategory = null;

                    while (selectedFoodCategory == null) {
                        System.out.println("Select a food Category: ");
                        String foodCategory = scanner.nextLine().toLowerCase();

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
                            case "salad":
                                selectedFoodCategory = ProductCategory.SALAD;
                                break;
                            case "side":
                                selectedFoodCategory = ProductCategory.SIDE;
                                break;
                            default:
                                System.out.println("Invalid category. Please choose a valid food category Among: \n");
                                ProductCategory.listCategories(true);
                                break;
                        }
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
            System.out.println("Select another filter? [Y/N]: ");
            String answer = scanner.nextLine();
            if (answer.equalsIgnoreCase("N")) finished = true;
        } while(!finished);

        performSearch(selectedFilters);

    }

    /**
     * Handles the user input for rating a specific store.
     * Validates the rating and sends it to the server.
     */
    public void rateMenuOption() {
        System.out.print("Enter the name of the store to rate: ");
        String storeName = scanner.nextLine();

        Rating rating = null;
        while (true) {
            System.out.print("Enter the rate of the store (1 to 5): ");
            double stars = scanner.nextDouble();
            scanner.nextLine(); // consume newline

            try {
                rating = Rating.fromValue(stars); // assuming this throws IllegalArgumentException if invalid
                break; // input is valid, exit loop
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid rating. Please enter one of: 1.0, 1.5, ..., 5.0");
            }
        }

        performRating(storeName, rating); // safe to use rating here since it's guaranteed to be valid
    }

    /**
     * Entry point for launching the client user interface.
     * Initializes the client, connects to the server, and presents a menu-driven interface.
     *
     * @param args command-line arguments (unused)
     */
    public static void main(String[] args) {
        //Let some time pass to initialize the Server Entities
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        //Create a client object
        Coordinates coordinates = new Coordinates(37.9755, 23.7348);//Somewhere in Athens
        Client client = new Client(coordinates);
        try {
            client.establishConnection();
        } catch (IOException | ClassNotFoundException e) {
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
                    } catch (IOException | ClassNotFoundException e) {
                        System.out.println("Something went wrong: " + e.getMessage()+  "\n Please try again.");
                    }catch (NumberFormatException e) {
                        System.out.println("Wrong input. Please try again.");
                    }
                    break;
                case "2":
                    try {
                        client.buyMenuOption();
                    } catch (IOException e) {
                        System.out.println("Something went wrong: " + e.getMessage()+  "\n Please try again.");
                    }
                    break;
                case "3":
                    client.rateMenuOption();
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
