package shannon.exception;

/**
 * Thrown when a command that takes nothing after it, such as {@code list} or {@code bye}, is
 * followed by more words, e.g. {@code list done}.
 */
public class UnexpectedArgumentException extends ShannonException {

    /**
     * Builds the message naming the extra words, so the user sees they were not acted on.
     *
     * @param command  the command word, which should have been typed on its own.
     * @param argument the words typed after it.
     */
    public UnexpectedArgumentException(String command, String argument) {
        super("\"" + command + "\" doesn't take anything after it, but you typed \"" + argument
                + "\". Just type: " + command);
    }
}
