package jeremy;

/**
 * Centralises the colours and styles used by Jeremy's retro interface.
 */
public final class RetroTheme {
    public static final String BACKGROUND = "#191817";
    public static final String PAPER = "#e9dfd0";
    public static final String INK = "#252323";
    public static final String OLIVE = "#7a7d52";
    public static final String BURGUNDY = "#4f3439";
    public static final String BURGUNDY_HIGHLIGHT = "#70464d";
    public static final String BORDER = "#817b6d";
    public static final String ERROR = "#8f3030";
    public static final String ERROR_BORDER = "#ffb3a7";
    public static final String FONT = "'Monospaced'";

    private RetroTheme() {
        // Utility class; do not instantiate.
    }

    /** Returns the base style for the application background. */
    public static String backgroundStyle() {
        return "-fx-background-color: " + BACKGROUND + ";";
    }

    /** Returns the style for the command input field. */
    public static String inputStyle() {
        return "-fx-background-color: " + PAPER + ";"
                + " -fx-text-fill: " + INK + "; -fx-font-family: " + FONT + ";"
                + " -fx-font-size: 13px; -fx-prompt-text-fill: #6c655c;"
                + " -fx-border-color: " + BORDER + "; -fx-border-width: 1px;";
    }

    /** Returns the style for the send button. */
    public static String buttonStyle() {
        return "-fx-background-color: " + BURGUNDY + ";"
                + " -fx-text-fill: " + PAPER + "; -fx-font-family: " + FONT + ";"
                + " -fx-font-weight: bold; -fx-font-size: 12px;"
                + " -fx-border-color: " + BORDER + "; -fx-border-width: 1px;";
    }

    /** Returns the highlighted style for the send button. */
    public static String buttonHoverStyle() {
        return buttonStyle() + " -fx-background-color: " + BURGUNDY_HIGHLIGHT + ";"
                + " -fx-cursor: hand;";
    }

    /** Returns the border and background style for the conversation pane. */
    public static String scrollPaneStyle() {
        return "-fx-background: " + BACKGROUND + "; -fx-background-color: " + BACKGROUND + ";"
                + " -fx-border-color: " + BORDER + "; -fx-border-width: 1px;";
    }
}
