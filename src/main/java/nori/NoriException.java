package nori;

/**
 * Represents an error caused by an invalid Nori command or task operation.
 */
public class NoriException extends Exception {

    /**
     * Creates a Nori-specific exception with a user-facing explanation.
     *
     * @param message the explanation and correction to show the user.
     */
    public NoriException(String message) {
        super(message);
    }
}
