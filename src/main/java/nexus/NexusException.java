package nexus;

/**
 * Represents invalid input given to Nexus.
 */
public class NexusException extends Exception {
    private static final long serialVersionUID = 1L;

    /**
     * Creates an exception with a user-facing explanation.
     *
     * @param message explanation of the invalid input.
     */
    public NexusException(String message) {
        super(message);
    }
}
