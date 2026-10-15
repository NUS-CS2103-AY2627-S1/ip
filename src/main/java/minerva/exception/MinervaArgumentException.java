package minerva.exception;

/**
 * Exception class just to print out custom messages passed in
 * for when arguments violate deadline, event
 */
public class MinervaArgumentException extends RuntimeException {
    public MinervaArgumentException(String message) {
        super(message);
    }
}
