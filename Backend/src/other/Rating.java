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

    Rating(float value) {
        this.value = value;
    }

    public float getValue() {
        return value;
    }
}