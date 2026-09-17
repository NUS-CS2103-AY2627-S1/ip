package panda.exception.parser;

/**
 * Signals that the user did not enter a command or task description.
 */
public class NoCommandFoundException extends ParseException {

    /**
     * Creates an exception for an unrecognized command.
     *
     * @param input Unrecognized input, or an empty string for blank input.
     */
    public NoCommandFoundException(String input) {
        super(input.isEmpty()
                ? "Amazing. You managed to submit absolutely nothing."
                : "\"" + input + "\" isn't a command. Confidence: 10/10. Accuracy: 0/10.");
    }
}
