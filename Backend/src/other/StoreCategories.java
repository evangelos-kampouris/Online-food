package other;

public enum StoreCategories {
    SOUVLATZIDIKO("Souvlakitzidiko"),
    BURGER_STORE("Burger Joint"),
    PIZZERIA("Pizzeria"),
    CREPERIE("Creperie"),
    COFFEE_SHOP("Coffee Shop"),
    SUSHI_BAR("Sushi Bar"),
    SANDWICH_STORE("Sandwich Deli"),
    ITALIAN("Italian Restaurant"),
    BRUNCH_STORE("Brunch Café"),
    STEAKHOUSE("Steakhouse"),
    SOUP_DELI("Soup Deli"),
    TEA_HOUSE("Tea House"),
    ICE_CREAM_STORE("Ice Cream Parlor"),
    TAQUERIA("Taqueria"),
    BAKERY("Bakery");

    private final String name;

    StoreCategories(String name) {
        this.name = name;
    }

    public static StoreCategories fromValue(String name) throws IllegalArgumentException {
        for (StoreCategories category : StoreCategories.values()) {
            if (category.getName().equalsIgnoreCase(name)) {
                return category;
            }
        }
        throw new IllegalArgumentException("Invalid category: " + name);
    }

    public String getName() {
        return name;
    }
}
