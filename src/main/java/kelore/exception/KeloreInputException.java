package kelore.exception;

/** Represents an invalid command or command argument entered by a Kelore user. */
public class KeloreInputException extends Exception {
    /**
     * Creates an input exception with a message explaining how to correct the input.
     *
     * @param message Explanation of the invalid input.
     */
    public KeloreInputException(String message) {
        super(message);
    }
}
