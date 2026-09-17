package nubish.utils;

/**
 * Represents an application-specific exception raised while handling Nubish commands.
 */
public class NubishException extends RuntimeException {
    /**
     * Creates an exception without a detail message.
     */
    public NubishException() {
        super();
    }

    /**
     * Creates a Nubish exception with the given detail message.
     *
     * @param message explanation of the error
     */
    public NubishException(String message) {
        super(message);
    }
}
