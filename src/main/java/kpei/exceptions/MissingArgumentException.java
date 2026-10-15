package kpei.exceptions;

/**
 * Signals that a required field or argument for a Command is missing or empty.
 */
public class MissingArgumentException extends BertException {
    /**
     * Constructs a {@code MissingArgumentException} with the specified detail message.
     *
     * @param argument The name of the argument that is required when creating a item.
     */
    public MissingArgumentException(String argument) {
        super("Argument " + argument + " is required.");
    }
}
