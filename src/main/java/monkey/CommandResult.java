package monkey;

/** Contains a command's response text and whether the application should exit. */
public record CommandResult(String response, boolean shouldExit) {
}
