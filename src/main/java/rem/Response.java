package rem;

/**
 * Holds the text, exit status, and error status produced by one command.
 *
 * @param text Complete response for a chat bubble.
 * @param isExit Whether the command ended the conversation.
 * @param isError Whether the command failed validation or could not save its changes.
 */
public record Response(String text, boolean isExit, boolean isError) {
}
