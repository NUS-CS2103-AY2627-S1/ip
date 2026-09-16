package walter.place;

/**
 * Represents a named place and its address.
 */
public class Place {
    private final String name;
    private final String address;

    /**
     * Creates a place with a name and address validated by the parser or storage layer.
     *
     * @param name Name used to identify the place.
     * @param address Address associated with the place.
     */
    public Place(String name, String address) {
        this.name = name;
        this.address = address;
    }

    /**
     * Returns the place name.
     *
     * @return Place name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the place address.
     *
     * @return Place address.
     */
    public String getAddress() {
        return address;
    }

    @Override
    public String toString() {
        return name + " — " + address;
    }
}
