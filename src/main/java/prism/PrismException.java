package prism;

/** Represents an expected error while processing a Prism command or task. */
public class PrismException extends Exception {
    /** Creates an exception with a user-facing error message. */
    public PrismException(String message) {
        super(message);
    }
}
