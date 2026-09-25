package friedberg;

/**
 * Result of processing one user input.
 *
 * @param message text that the frontend should show to the user
 * @param shouldExit whether this input asks Friedberg to end the session
 * @param responseType semantic category frontends can use for presentation
 */
public record CommandResult(String message, boolean shouldExit, ResponseType responseType) {
}
