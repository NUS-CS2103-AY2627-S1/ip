package clover.tutoree;

/**
 * Represents a private-tuition student and the details needed to tutor them.
 */
public class Tutoree {
    private final String name;
    private final String address;
    private final String fee;

    /**
     * Creates a tutoree with their name, tutoring address, and fee arrangement.
     */
    public Tutoree(String name, String address, String fee) {
        assert name != null : "Tutoree names must not be null.";
        assert address != null : "Tutoree addresses must not be null.";
        assert fee != null : "Tutoree fees must not be null.";
        this.name = name;
        this.address = address;
        this.fee = fee;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getFee() {
        return fee;
    }
}
