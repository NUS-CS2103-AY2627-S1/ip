package exception;

/**
 * For any command related user exceptions
 * FriedbergCommandException
 */
public class FriedbergCommandException extends FriedbergException {
    public FriedbergCommandException(String message, String command) {
        super(String.format("command: %s|%s", command, message));
    }
}
