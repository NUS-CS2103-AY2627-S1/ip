package thomas;

/**
 * Signals that the chatbot cannot carry out a command the user typed.
 * Covers only mistakes the user can fix by typing something different, such as
 * an empty description or a task number that does not exist, and carries a
 * message written for the user to read.
 * Checked rather than unchecked, so that the compiler refuses to build code
 * that throws one without handling it.
 */
public class ThomasException extends Exception {
    /**
     * Creates an exception carrying a message meant for the user.
     *
     * @param message What went wrong, phrased for the person typing.
     */
    public ThomasException(String message) {
        super(message);
    }
}
