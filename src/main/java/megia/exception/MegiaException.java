package megia.exception;

import java.util.Objects;

/**
 * Base class for recoverable errors that can be shown to the user.
 * The exception retains structured error information so localization remains
 * the responsibility of the console interface.
 */
public abstract class MegiaException extends Exception {
    /** Identifies the localized error message. */
    private final ErrorCode errorCode;
    /** Values to interpolate into the localized error message. */
    private final Object[] messageArguments;

    /**
     * Creates an exception with a localized-message key and optional formatting values.
     * An explicitly null argument array is treated as empty.
     *
     * @param errorCode Identifies the message to display.
     * @param messageArguments Values interpolated into the message.
     */
    protected MegiaException(ErrorCode errorCode, Object... messageArguments) {
        super(Objects.requireNonNull(errorCode).name());
        this.errorCode = errorCode;
        this.messageArguments = messageArguments == null ? new Object[0] : messageArguments.clone();
    }

    /**
     * Returns the structured error identifier.
     *
     * @return Error identifier.
     */
    public ErrorCode getErrorCode() {
        return errorCode;
    }

    /**
     * Returns a defensive copy of the values used to format the message.
     *
     * @return Message formatting values.
     */
    public Object[] getMessageArguments() {
        return messageArguments.clone();
    }
}
