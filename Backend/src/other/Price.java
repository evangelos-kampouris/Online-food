package other;

public enum Price{
    LOW("Cheap"),
    MEDIUM("Medium"),
    HIGH("Expensive");

    private final String description;

    Price(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
