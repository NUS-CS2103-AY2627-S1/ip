package trybot.ui;

/**
 * Carries reply text and explicit presentation state from a command to the graphical client.
 *
 * @param text formatted reply.
 * @param hasError whether the command was rejected and can be corrected.
 * @param hasWarning whether a change could not be saved.
 * @param isExit whether the command ended the conversation.
 */
public record ChatReply(String text, boolean hasError, boolean hasWarning, boolean isExit) {
}
