package panda.exception.parser;

/**
 * Signals that a date or date-time has an invalid format or value.
 */
public class IllegalDateTimeException extends InvalidArgumentException {

    /**
     * Creates an exception that identifies the illegal date or date-time.
     *
     * @param illegalDateTime Illegal date or date-time supplied for parsing.
     */
    public IllegalDateTimeException(String illegalDateTime) {
        super(createMessage(illegalDateTime));
    }

    /**
     * Creates an exception that identifies the illegal date or date-time and its cause.
     *
     * @param illegalDateTime Illegal date or date-time supplied for parsing.
     * @param cause Parsing error that caused this exception.
     */
    public IllegalDateTimeException(String illegalDateTime, Throwable cause) {
        super(createMessage(illegalDateTime), cause);
    }

    private static String createMessage(String illegalDateTime) {
        return String.format(
                "Time itself rejected \"%s\". Use \"d/M/yyyy\" "
                        + "(e.g. 2/12/2019) or \"d/M/yyyy HH:mm\" (e.g. 2/12/2019 18:00).",
                illegalDateTime);
    }
}
