package aster.exception;

/**
 * An error Aster can explain to the user, such as an incomplete command.
 *
 * <p>It is checked rather than unchecked so the compiler forces every command to
 * be handled at the one place in {@code Aster} that reports errors. Its message is
 * written for the user, so it can be printed exactly as it is.
 */
public class AsterException extends Exception {
    /**
     * Version marker required of a serializable class; {@link Exception} is serializable.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Creates an exception carrying a message that says what went wrong and how to
     * correct it.
     *
     * @param message the user-facing explanation, printed exactly as given.
     */
    public AsterException(String message) {
        super(message);
    }
}
