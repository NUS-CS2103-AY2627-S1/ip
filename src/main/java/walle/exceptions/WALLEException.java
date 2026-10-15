package walle.exceptions;

/**
 * Base class for all of WALLE's own exceptions. Each subclass's message is
 * already formatted for direct display to the user (see {@link walle.ui.Ui#printErrorMsg(WALLEException)}).
 */
// CHECKSTYLE.OFF: AbbreviationAsWordInName - "WALLE" is the chatbot's actual name, not an abbreviation to shorten.
public class WALLEException extends RuntimeException {
    // CHECKSTYLE.ON: AbbreviationAsWordInName

    /**
     * Creates a WALLEException with the given user-facing message.
     *
     * @param message the message to show the user, already formatted for display.
     */
    public WALLEException(String message) {
        super(message);
    }

}
