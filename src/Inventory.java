import java.util.HashMap;
import java.util.Map;

public abstract class Inventory {

    Map<String, Product> inventory = new HashMap<String, Product>(); // Selected DataStructure <name, product>

    public abstract void addProduct(String productName, Product product);

    public abstract void removeProduct(String productName);

}
