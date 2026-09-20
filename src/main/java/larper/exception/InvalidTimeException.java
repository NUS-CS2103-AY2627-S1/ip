package larper.exception;

import larper.common.CommandHelp;

/**
 * Signals that a deadline or event command has a date but still needs a time confirmation.
 */
public class InvalidTimeException extends LarperException {
    /**
     * Creates an exception that identifies the date field waiting for an optional time answer.
     *
     * @param label Name of the date field that needs a time confirmation.
     */
    public InvalidTimeException(String label) {
        super(" Larper found the " + label + " date, but the time is still allegedly missing.\n"
                + CommandHelp.TIME_EXAMPLES);
    }
}
