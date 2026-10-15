package nerrad;

/**
 * Represents an invalid command or task input that Nerrad can explain to the user.
 */
public class NerradException extends Exception {
    /**
     * Creates an exception with a user-facing explanation of the input problem.
     *
     * @param message Explanation that will be shown in the chatbot UI.
     */
    public NerradException(String message) {
        super(message);
    }
}

