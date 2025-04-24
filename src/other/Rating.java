package other;

public enum Rating {
    ONE_STAR(1.0),
    ONE_HALF_STAR(1.5),
    TWO_STARS(2.0),
    TWO_HALF_STARS(2.5),
    THREE_STARS(3.0),
    THREE_HALF_STARS(3.5),
    FOUR_STARS(4.0),
    FOUR_HALF_STARS(4.5),
    FIVE_STARS(5.0);

    private final double value;

    public static Rating fromValue(double value) {
        for (Rating rating : Rating.values()) {
            if (rating.getValue() == value) {
                return rating;
            }
        }
        throw new IllegalArgumentException("Invalid rating value: " + value);
    }

    Rating(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }
}