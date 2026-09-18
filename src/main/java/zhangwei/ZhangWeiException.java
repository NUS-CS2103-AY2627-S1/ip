package zhangwei;

/**
 * An error the chatbot itself detects and can explain to the user,
 * such as a command it does not recognise or a missing argument.
 *
 * <p>Checked (it extends Exception rather than RuntimeException) so that
 * every method able to raise one has to say so, and the command loop has to
 * handle it.
 */
public class ZhangWeiException extends Exception {

    /**
     * Creates an exception carrying a message written for the user to read.
     *
     * @param message the explanation shown to the user, phrased so that it
     *     says what went wrong and what would have worked instead.
     */
    public ZhangWeiException(String message) {
        super(message);
    }
}
