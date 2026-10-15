package avon.gui;

import java.util.stream.Collectors;

/**
 * Adapts console-oriented Avon responses for display in the graphical interface.
 */
public final class GuiResponseFormatter {
    private static final String AVON_PREFIX = "Avon:\t";
    private static final String CONSOLE_INDENT = "        ";
    private static final String ERROR_PREFIX = AVON_PREFIX + "Pardon, I beseech thee!";

    private GuiResponseFormatter() {
    }

    /**
     * Removes console-only speaker prefixes and indentation from an Avon response.
     *
     * @param response the response generated through the console UI.
     * @return the same content without console presentation markers.
     */
    public static String formatAvonResponse(String response) {
        return response.lines()
                .map(GuiResponseFormatter::removeConsoleDecoration)
                .collect(Collectors.joining("\n"));
    }

    /**
     * Returns whether a console response reports an error to the user.
     *
     * @param response the response generated through the console UI.
     * @return {@code true} when the response begins with Avon's standard error phrase.
     */
    public static boolean isErrorResponse(String response) {
        return response.startsWith(ERROR_PREFIX);
    }

    /**
     * Removes at most one known console decoration from the start of a response line.
     *
     * @param line one line of console-oriented output.
     * @return the line suitable for display in a dialog box.
     */
    private static String removeConsoleDecoration(String line) {
        if (line.startsWith(AVON_PREFIX)) {
            return line.substring(AVON_PREFIX.length());
        }
        if (line.startsWith(CONSOLE_INDENT)) {
            return line.substring(CONSOLE_INDENT.length());
        }
        return line;
    }
}
