package cooper;

/**
 * Contains Cooper's response and interface-independent command status.
 *
 * @param message response to display
 * @param shouldExit whether the application should close after displaying it
 * @param isError whether the command failed
 */
public record CommandResult(String message, boolean shouldExit, boolean isError) {
    /** Creates a successful result, preserving the existing two-argument API. */
    public CommandResult(String message, boolean shouldExit) {
        this(message, shouldExit, false);
    }
}
