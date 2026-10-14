package bott;

/**
 * Represents an error specific to Bott, such as an invalid or incomplete
 * command entered by the user.
 */
public class BottException extends Exception {

    /**
     * Creates a new exception carrying a message describing what went
     * wrong and, where possible, how to fix it.
     *
     * @param message Description of the error.
     */
    public BottException(String message) {
        super(message);
    }
}
