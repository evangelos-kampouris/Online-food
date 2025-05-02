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

    /**
     * Round the given float to the nearest enum constant.
     */
    public static Rating fromValue(float v) {
        // round to nearest 0.5
        float halfSteps = Math.round(v * 2.0f);
        float rounded = halfSteps / 2.0f;
        for (Rating r : values()) {
            if (Float.compare((float)r.value, rounded) == 0) {
                return r;
            }
        }
        // fallback (should never happen if v is within 1.0–5.0)
        throw new IllegalArgumentException("No Rating for value " + v);
    }

    Rating(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }
}