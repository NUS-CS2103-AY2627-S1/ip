package axiom;

/**
 * Represents an error specific to the AXIOM chatbot.
 */
public class AxiomException extends Exception {

    /**
     * Creates an exception with the given message.
     *
     * @param message Description of the error.
     */
    public AxiomException(String message) {
        super(message);
    }
}
