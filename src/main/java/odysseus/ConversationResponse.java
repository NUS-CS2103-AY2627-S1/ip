package odysseus;

/**
 * Describes an Odysseus reply together with its presentation intent.
 *
 * @param message text that Odysseus returns to the traveler.
 * @param isCommandError whether the reply corrects an invalid command.
 */
public record ConversationResponse(String message, boolean isCommandError) {
}
