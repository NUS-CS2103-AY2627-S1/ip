package duke.place;

import java.util.Objects;

/**
 * Represents a place with a name and optional details.
 */
public class Place {
    private final String name;
    private final String details;

    /**
     * Constructs a Place with a name and no additional details.
     *
     * @param name The name of the place.
     */
    public Place(String name) {
        this(name, "");
    }

    /**
     * Constructs a Place with a name and details.
     *
     * @param name    The name of the place.
     * @param details The descriptive details or address of the place.
     */
    public Place(String name, String details) {
        assert name != null && !name.trim().isEmpty() : "Place name must not be null or empty";
        assert details != null : "Place details must not be null";
        this.name = name.trim();
        this.details = details.trim();
    }

    /**
     * Returns the name of the place.
     *
     * @return The place name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the details of the place.
     *
     * @return The place details.
     */
    public String getDetails() {
        return details;
    }

    /**
     * Formats the place for saving to a file.
     *
     * @return A pipe-separated string representing the place state.
     */
    public String toFileFormat() {
        return name + " | " + details;
    }

    /**
     * Creates and returns a copy of this Place.
     *
     * @return A copy of this Place.
     */
    public Place copy() {
        return new Place(name, details);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Place)) {
            return false;
        }
        Place otherPlace = (Place) other;
        return name.equals(otherPlace.name) && details.equals(otherPlace.details);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, details);
    }

    @Override
    public String toString() {
        if (details.isEmpty()) {
            return name;
        }
        return name + " (details: " + details + ")";
    }
}
