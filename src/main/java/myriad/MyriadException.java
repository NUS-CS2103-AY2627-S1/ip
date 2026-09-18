package myriad;

/**
 * Signals that a line of text couldn't be turned into a valid command or
 * task — either typed by the user (e.g. a missing task number, or a
 * missing task description/date), or read back from the saved data file
 * (e.g. a corrupted or unrecognized line). The message is the specific
 * reason only: {@code Ui.showError} adds Myriad's error exclamation when
 * the message is shown to the user, so the same message also reads
 * correctly anywhere else it is used, such as a test assertion.
 */
public class MyriadException extends Exception {
    /**
     * Version of this class's serialized form. Every {@link Throwable} is
     * {@link java.io.Serializable}, and declaring this keeps the form fixed
     * instead of letting it be recomputed, differently, whenever the class
     * changes.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Creates an exception carrying the specific reason something failed.
     *
     * @param message the reason alone, without any error prefix.
     */
    public MyriadException(String message) {
        super(message);
    }
}
