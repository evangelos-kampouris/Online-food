package other;

public enum Rating {
    ONE_STAR(1.0f),
    ONE_HALF_STAR(1.5f),
    TWO_STARS(2.0f),
    TWO_HALF_STARS(2.5f),
    THREE_STARS(3.0f),
    THREE_HALF_STARS(3.5f),
    FOUR_STARS(4.0f),
    FOUR_HALF_STARS(4.5f),
    FIVE_STARS(5.0f);

    private final float value;

    Rating(float value) {
        this.value = value;
    }

    public float getValue() {
        return value;
    }
} 