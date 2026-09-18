package altair;

/**
 * Represents an input error that Altair can explain to the user.
 */
public class AltairException extends Exception {

    /**
     * Creates an exception with a user-facing explanation.
     *
     * @param message the explanation to show to the user.
     */
    public AltairException(String message) {
        super(message);
    }
}
