package plana.exception;

/**
 * Represents an input error that Plana can explain to the user and recover from.
 */
public class PlanaException extends Exception {

    /**
     * Creates an exception with a user-facing explanation.
     *
     * @param message the explanation to show the user.
     */
    public PlanaException(String message) {
        super(message);
    }
}
