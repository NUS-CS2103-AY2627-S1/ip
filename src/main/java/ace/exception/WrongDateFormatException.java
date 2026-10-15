package ace.exception;

/**
 * Thrown when a date/time given to a "deadline" command (or read back from the
 * save file) doesn't match any of the formats Ace knows how to parse.
 */
public class WrongDateFormatException extends AceException {

    /**
     * Creates a new exception for text that couldn't be parsed as a date/time.
     *
     * @param input the text that failed to parse as a date/time.
     */
    public WrongDateFormatException(String input) {
        super(String.format(
                "Oh no! I could not read %s as it is not in the expected format! "
                        + "Try: d/M/yyyy HHmm (e.g. 2/12/2019 1800), or just d/M/yyyy for no specific time.",
                input));
    }
}
