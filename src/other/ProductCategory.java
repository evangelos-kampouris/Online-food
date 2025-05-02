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
    PIE("Pie"),
    SALAD("Salad"),
    SIDE("Side"),
    DRINK("Drink");

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
            if (category.getName().equalsIgnoreCase(name)) {
                return category;
            }
        }
        throw new IllegalArgumentException("Invalid category: " + name);
    }

    public String getName() {
        return name;
    }

    public static void listCategories(boolean tabbed) {
        for (ProductCategory category : ProductCategory.values()) {
            if (tabbed)
                System.out.println("\t " + category.getName());
            else
                System.out.println(category.getName());
        }
    }
}
