package potato.exception;

/**
 * Represents domain-specific exceptions thrown during Potato application execution.
 */
public class PotatoException extends Exception {

    /**
     * Constructs a {@code PotatoException} with the specified detail message.
     *
     * @param message Explanatory message for the exception.
     */
    public PotatoException(String message) {
        super(message);
    }
}