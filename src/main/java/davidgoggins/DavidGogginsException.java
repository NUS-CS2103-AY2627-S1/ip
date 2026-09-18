package davidgoggins;

/**
 * Signals that the chatbot cannot carry out what the user asked for.
 *
 * <p>Each command method throws with a helpful message and the main loop is the single
 * place that decides how it is shown. Extends {@link Exception} rather than
 * {@link RuntimeException}, so the compiler forces every caller to deal with it.
 */
public class DavidGogginsException extends Exception {
    /**
     * Required because {@link Exception} is serializable. Fixing a value keeps the
     * class compatible with itself across builds instead of letting the compiler
     * generate a different one each time.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Creates an exception carrying the explanation to show the user.
     *
     * @param message the explanation shown to the user, ideally saying both what
     *                went wrong and how to type the command correctly
     */
    public DavidGogginsException(String message) {
        super(message);
    }
}
