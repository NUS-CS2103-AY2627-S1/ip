package shan;

/**
 * Contains the text produced by a command and whether it ends the application.
 *
 * @param message    Text to display to the user.
 * @param shouldExit Whether the application should close after showing the message.
 */
public record CommandResult(String message, boolean shouldExit) {
}
