package nabmak.place;

/**
 * Represents a place saved by the user.
 */
public class Place {
    private final String name;
    private final String details;

    /**
     * Creates a place with the specified name and details.
     *
     * @param name name of the place
     * @param details details recorded about the place
     */
    public Place(String name, String details) {
        this.name = name;
        this.details = details;
    }

    public String getName() {
        return name;
    }

    public String getDetails() {
        return details;
    }

    @Override
    public String toString() {
        return name + " - " + details;
    }
}
