package herta.exception;

/**
 * Represents an input error that Herta can explain to the user.
 */
public class HertaException extends Exception {

    /**
     * Creates an exception with a user-facing explanation.
     *
     * @param message the explanation shown to the user.
     */
    public HertaException(String message) {
        super(message);
    }
}
