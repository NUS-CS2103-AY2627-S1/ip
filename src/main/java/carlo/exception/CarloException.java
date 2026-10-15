package carlo.exception;

/**
 * Represents an error caused by an invalid Carlo command.
 */
public class CarloException extends Exception {
    public CarloException(String message) {
        super(message);
    }
}
