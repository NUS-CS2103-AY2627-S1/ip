package bkxss;

/**
 * A chatbot reply with an explicit status for accessible GUI error feedback.
 *
 * @param message response shown to the user
 * @param isError whether command validation failed
 */
public record CommandResult(String message, boolean isError) {
}
