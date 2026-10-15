package heisenberg;

/** Indicates that the arguments of a command do not follow the expected format. */
public class InvalidFormatException extends RuntimeException {
    public InvalidFormatException(String message) {
        super(message);
    }
}
