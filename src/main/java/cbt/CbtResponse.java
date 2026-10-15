package cbt;

/**
 * Contains the message and display status produced for one GUI command.
 *
 * @param message message to display to the user.
 * @param isError whether the command failed because of invalid user input.
 */
public record CbtResponse(String message, boolean isError) {
}
