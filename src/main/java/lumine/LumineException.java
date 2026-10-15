package lumine;
/**
 * Represents an input error that can be explained to the user.
 */
public class LumineException extends RuntimeException {

    /**
     * Constructs a new exception with the given human-readable explanation.
     *
     * @param message the explanation shown to the user.
     */
    public LumineException(String message) {
        super(message);
    }

}
