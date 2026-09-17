package crystal.exception;

/**
 * Represents an error specific to the Crystal chatbot.
 */
public class CrystalException extends Exception {
    /**
     * Creates a chatbot exception with the message to show the user.
     *
     * @param message explanation of the error.
     */
    public CrystalException(String message) {
        super(message);
    }

    /**
     * Returns this error in Crystal's user-facing message format.
     *
     * @return error message prefixed with Crystal's name and reaction.
     */
    public String getUserMessage() {
        return "Crystal: Oopsies!!! " + getMessage();
    }
}
