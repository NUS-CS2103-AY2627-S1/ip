package arthur.messages;

import arthur.exceptions.ArthurRuntimeException;

/**
 * Displayed when a command throws an error.
 */
public class ErrorMessage extends Message {
    // AI-assisted: Named the error-response format and its CSS class.
    private static final String ERROR_RESPONSE_FORMAT = "%s\n%s";
    private static final String ERROR_MESSAGE_CSS_CLASS = "error-message";

    private final ArthurRuntimeException exception;

    public ErrorMessage(ArthurRuntimeException exception) {
        this.exception = exception;
    }

    @Override
    public String toString() {
        return String.format(ERROR_RESPONSE_FORMAT, exception.getMessage(), getRandomQuote());
    }

    @Override
    public boolean isFinal() {
        return false;
    }

    @Override
    public String getClassName() {
        return ERROR_MESSAGE_CSS_CLASS;
    }
}
