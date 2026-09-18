package milo.exception;

/**
 * Represents an error that Milo can explain to the user, such as a command
 * that is missing information or one that Milo does not recognise.
 * <p>
 * The message carried by a MiloException is written to be read by the user,
 * so whoever catches it can print the message directly.
 * <p>
 * This extends {@link Exception} rather than {@code RuntimeException}, which
 * makes it a <em>checked</em> exception: the compiler will not let a caller
 * ignore it, so every command that can fail must say how it is handled.
 */
public class MiloException extends Exception {
    /**
     * Creates an exception carrying an explanation meant for the user.
     *
     * @param message what went wrong, and where possible how to fix it
     */
    public MiloException(String message) {
        super(message);
    }
}
