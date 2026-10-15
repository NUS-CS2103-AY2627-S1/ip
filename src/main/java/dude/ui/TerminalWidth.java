package dude.ui;

/**
 * Detects a usable console width for the text renderer.
 */
final class TerminalWidth {
    private static final int DEFAULT_WIDTH = 60;

    private TerminalWidth() {
    }

    /**
     * Returns the attached console width or the standard fallback.
     *
     * @return Console width in characters.
     */
    static int detect() {
        try {
            var console = System.console();
            if (console == null) {
                return DEFAULT_WIDTH;
            }
            var method = console.getClass().getMethod("getWidth");
            return (int) method.invoke(console);
        } catch (ReflectiveOperationException | ClassCastException exception) {
            return DEFAULT_WIDTH;
        }
    }
}
