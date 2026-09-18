package meowmeow;

/**
 * Signals an error specific to Meowmeow (e.g. an unrecognised command, or a
 * malformed argument) that should be reported to the user as a friendly
 * explanation rather than crashing the program. A multi-line explanation is
 * carried as a single message with lines joined by "\n"; the catch site
 * splits on "\n" when boxing the message for display.
 */
public class MeowmeowException extends Exception {

    /**
     * Constructs an exception carrying the message to show the user. The
     * parameter is a varargs so a multi-line explanation can be given as one
     * line per argument instead of the caller hand-writing the {@code "\n"}
     * separators; the lines are joined here with {@code "\n"}. Passing a
     * single string still works and is used unchanged.
     *
     * @param lines the friendly explanation, one display line per argument.
     */
    public MeowmeowException(String... lines) {
        super(String.join("\n", lines));
    }
}
