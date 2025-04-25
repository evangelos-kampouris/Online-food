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

    /**
     *
     * @param name
     * @return reting enum value
     *
     */
    public static ProductCategory fromValue(String name) throws IllegalArgumentException {
        for (ProductCategory category : ProductCategory.values()) {
            if (category.getName().equals(name)) {
                return category;
            }
        }
        throw new IllegalArgumentException("Invalid category: " + name);
    }

    public String getName() {
        return name;
    }
}
