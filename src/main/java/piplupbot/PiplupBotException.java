package piplupbot;

// ACKNOWLEDGEMENTS: This Java file was written with the help of Claude.

/**
 * Represents an error that PiplupBot itself recognizes and can explain to the
 * user, such as a command with its details missing or a task number that
 * names no task.
 *
 * <p>It extends {@code Exception}, which makes it a <em>checked</em> exception.</p>
 *
 * <p>The message is carried as separate lines because the bot replies in
 * lines, so the text can be handed straight to the method that prints a
 * reply without being taken apart again.</p>
 */
public class PiplupBotException extends Exception {
    /** The lines of the explanation shown to the user. */
    private final String[] messageLines;

    /**
     * Creates an error carrying the explanation to show the user.
     *
     * @param messageLines One or more lines of explanation, printed in order.
     */
    public PiplupBotException(String... messageLines) {
        // Join the lines for getMessage(), which is what a stack trace or a
        // debugger shows; the lines themselves are kept for the reply.
        super(String.join(" ", messageLines));
        this.messageLines = messageLines;
    }

    /**
     * Returns the explanation as the separate lines it should be printed on.
     *
     * @return The lines of the message.
     */
    public String[] getMessageLines() {
        return messageLines;
    }
}
