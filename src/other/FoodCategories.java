package other;

public enum FoodCategories {
    SOUVLAKI("Souvlaki"),
    BURGERS("Burgers"),
    PIZZA("Pizza"),
    CREPES("Crepes"),
    SANDWICHES("Sandwiches"),
    PASTA("Pasta"),
    COFFEE("Coffee"),
    DESSERTS("Desserts"),
    SUSHI("Sushi"),
    GRILL("Grill"),
    SEAFOOD("Seafood");

    private final String name;

    FoodCategories(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
