package yqr;

/**
 * Contains the text produced by a command and whether it represents an error.
 *
 * @param text text to display to the user.
 * @param isError whether the command failed.
 */
public record CommandResult(String text, boolean isError) {
}
