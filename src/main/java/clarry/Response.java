package clarry;

/**
 * Carries reply text and its error status so the GUI can style errors without inspecting the wording.
 *
 * @param text reply shown to the user
 * @param isError whether the command failed validation
 */
public record Response(String text, boolean isError) {
}
