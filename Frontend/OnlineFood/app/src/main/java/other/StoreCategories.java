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

    public String getName() {
        return name;
    }
} 