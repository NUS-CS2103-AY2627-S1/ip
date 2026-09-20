package larper.exception;

import larper.common.CommandHelp;

/**
 * Signals that a deadline or event command is missing a required date.
 */
public class InvalidDateException extends LarperException {
    /**
     * Creates an exception that identifies the missing or invalid date field.
     *
     * @param label Name of the date field that could not be parsed.
     */
    public InvalidDateException(String label) {
        super(" Missing lore. Larper needs the " + label + " date.\n" + CommandHelp.DATE_EXAMPLES);
    }
}
