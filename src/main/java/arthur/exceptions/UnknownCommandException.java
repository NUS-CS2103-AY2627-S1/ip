package arthur.exceptions;

/**
 * Indicates that the user entered an unregistered command.
 */
public class UnknownCommandException extends ArthurRuntimeException {
    public UnknownCommandException(String name) {
        super("The called command does not exist: " + name);
    }
}
