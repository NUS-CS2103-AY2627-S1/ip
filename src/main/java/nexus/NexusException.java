package nexus;

/** Represents an input error that Nexus can explain to the user. */
public class NexusException extends Exception {
    /** Creates an exception with a user-facing explanation of the input error. */
    public NexusException(String message) {
        super(message);
    }
}
