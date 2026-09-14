package arthur.exceptions;

/**
 * Indicates that a date or time argument could not be parsed.
 */
public class InvalidDateTimeException extends ArthurRuntimeException {
    public InvalidDateTimeException(String argument) {
        super("Unable to parse datetime: " + argument);
    }
}
