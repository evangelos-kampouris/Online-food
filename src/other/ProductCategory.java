package other;

public enum ProductCategory {
    SOUVLAKI("Souvlaki"),
    BURGER("Burger"),
    PIZZA("Pizza"),
    CREPE("Crepe"),
    COFFEE("Coffee"),
    SUSHI("Sushi"),
    SANDWICH("Sandwich"),
    PASTA("Pasta"),
    BRUNCH("Brunch"),
    STEAK("Steak"),
    SOUP("Soup"),
    TEA("Tea"),
    WAFFLE("Waffle"),
    ICE_CREAM("Ice Cream"),
    TACOS("Tacos"),
    BEVERAGE("Beverage"),
    PIE("Pie");

    private final String name;

    ProductCategory(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
