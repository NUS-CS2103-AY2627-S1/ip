package penny;

/**
 * Result of parsing and executing a single line of user input.
 *
 * @param message text to display to the user.
 * @param shouldExit whether Penny should stop running after this input.
 */
public record ParseResult(String message, boolean shouldExit) {
}
