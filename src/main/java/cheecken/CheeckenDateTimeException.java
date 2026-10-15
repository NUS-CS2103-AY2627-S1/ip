package cheecken;

/**
 * Indicates that a deadline or event date/time is missing or invalid.
 */
public class CheeckenDateTimeException extends RuntimeException {
    /**
     * Creates an exception with guidance for the invalid command.
     * @param command command name used in error guidance
     */
    public CheeckenDateTimeException(String command) {
        super("No time how I set the task...\n" + (command.equals("event")
                ? "Try: event meeting /from today 0900 /to tomorrow 1000."
                : "Try: deadline report /by tomorrow 1800.")
                + "\nDates: d/M/yyyy, today, tomorrow, Mon–Sun (or full names); optional HHmm. Or now.");
    }
}
