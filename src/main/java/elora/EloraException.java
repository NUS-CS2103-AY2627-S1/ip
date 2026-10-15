package elora;

/**
 * Represents an exception specific to Elora, thrown when user input
 * can't be processed - for example a missing description, an invalid
 * task number, or an unrecognized command.
 */
public class EloraException extends Exception {

    /**
     * Creates an EloraException with a message describing the problem.
     *
     * @param message Explanation of what went wrong, shown to the user.
     */
    public EloraException(String message) {
        super(message);
    }
}
