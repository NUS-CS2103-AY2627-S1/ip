package ben;

/**
 * Exception type for anything Ben-specific that goes wrong while handling
 * a command (bad input, missing arguments, an unreadable save file, and
 * so on).
 * <p>
 * The message is prefixed with "OOPS!!!" so it is ready to print as-is,
 * matching the course spec's sample error messages.
 */
class BenException extends Exception {
    BenException(String message) {
        super("OOPS!!! " + message);
    }
}
