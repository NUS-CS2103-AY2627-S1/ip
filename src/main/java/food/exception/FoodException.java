package food.exception;

/**
 * Base class for every error the chatbot raises on purpose.
 *
 * <p>It is a <em>checked</em> exception (it extends Exception, not RuntimeException) because
 * callers are expected to react to it: the compiler forces them to either handle it or declare
 * it. Code should generally throw one of the two subclasses rather than this class directly,
 * since the subclass is what tells the caller how to react.
 */
public class FoodException extends Exception {
    /**
     * Creates an exception carrying a message meant to be shown to the user.
     *
     * @param msg the explanation to show.
     */
    public FoodException(String msg) {
        super(msg);
    }

    /**
     * Creates an exception that remembers the lower-level exception that triggered it.
     *
     * <p>Always prefer this constructor when converting one exception into another. Without the
     * cause, the original stack trace is lost and "Error saving to storage" is all you ever get
     * to debug with.
     *
     * @param msg   message shown to the user.
     * @param cause the original exception being translated.
     */
    public FoodException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
