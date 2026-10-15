package nori;

/**
 * Carries response text and its outcome so the GUI can highlight errors without parsing messages.
 *
 * @param message text to display to the user.
 * @param isError whether the command could not be completed.
 */
public record Reply(String message, boolean isError) {
}
