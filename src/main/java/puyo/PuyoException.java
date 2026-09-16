package puyo;

/**
 * Represents an exception specific to the Puyo application.
 */
public class PuyoException extends Exception {

    /**
     * Constructs a {@code PuyoException} with the specified detail message.
     *
     * @param message The detail message describing the cause of the exception.
     */
    public PuyoException(String message) {
        super(message);
    }
}
