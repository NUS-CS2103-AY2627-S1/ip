package meowmeow;

/**
 * One reply Meowmeow shows in the GUI, paired with what kind of reply it is
 * so the window can style it differently - a plain confirmation, an error, a
 * background warning, or the final reply before the window closes.
 *
 * @param text the message text to show.
 * @param kind what kind of reply this is.
 */
public record Response(String text, ResponseKind kind) {
}
