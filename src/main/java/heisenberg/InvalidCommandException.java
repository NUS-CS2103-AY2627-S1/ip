package heisenberg;

/** Indicates that the user input does not start with a known command. */
public class InvalidCommandException extends RuntimeException {
    public InvalidCommandException(String message) {
        super(message);
    }
}
