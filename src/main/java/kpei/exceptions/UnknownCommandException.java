package kpei.exceptions;

/**
 * Signals that an unrecognized or unsupported Command was entered.
 */
public class UnknownCommandException extends BertException {
    /**
     * Constructs an {@code UnknownCommandException} with the specified detail message.
     *
     * @param invalidCommand The Command called in BERT that is not part of the set of valid commands.
     */
    public UnknownCommandException(String invalidCommand) {
        super("Unknown Command: " + invalidCommand);
    }
}
