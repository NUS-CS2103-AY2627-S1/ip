package meowmeow.ui;

/**
 * How a message built by {@link Ui} should be rendered for its audience.
 *
 * <p>{@code PLAIN} is the console form: every line keeps its existing
 * leading space (a formatting convention from the original console output)
 * and no decoration is added. {@code RICH} is the JavaFX GUI form: the
 * leading space is stripped from every line (an indent that only made sense
 * inside the console's divider box) and the first line gets a small
 * decoration, since a GUI speech bubble can carry more visual flourish than
 * a terminal can.
 */
public enum MessageStyle {
    /** The console form: leading spaces kept, no decoration. */
    PLAIN,
    /** The GUI form: leading spaces stripped, first line decorated. */
    RICH
}
