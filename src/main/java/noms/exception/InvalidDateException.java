package noms.exception;

/**
 * Indicates that a date field in a user command could not be parsed as
 * a valid ISO-format date.
 */
public class InvalidDateException extends NomsException {
    /**
     * Creates the exception for a date that could not be parsed.
     *
     * @param raw the unparseable date text typed by the user
     */
    public InvalidDateException(String raw) {
        super("Noms couldn't read the date \"" + raw + "\".\n"
                + "Try the format yyyy-mm-dd (e.g. 2019-10-15).");
    }
}
