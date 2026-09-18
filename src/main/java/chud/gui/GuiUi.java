package chud.gui;

import chud.ui.Ui;

/**
 * A {@link Ui} that buffers Chud's replies as plain text instead of printing them to the
 * console, so {@link MainWindow} can show them as a chat bubble. Every {@code Command} calls the
 * same {@code showMessage}/{@code showIndentedMessage}/{@code showError} methods either way --
 * only where the text ends up differs.
 */
public class GuiUi extends Ui {
    private final StringBuilder buffer = new StringBuilder();
    private boolean hasError;

    @Override
    public void showWelcome() {
        showMessage("yo it's Chud 🗿", "what we lockin in today?");
    }

    @Override
    public void showLine() {
        // No-op: the console's horizontal-line separator has no GUI equivalent.
    }

    @Override
    public void showMessage(Object... contents) {
        for (Object content : contents) {
            appendLine(String.valueOf(content));
        }
    }

    @Override
    public void showIndentedMessage(Object content) {
        appendLine(String.valueOf(content));
    }

    @Override
    public void showError(String message) {
        hasError = true;
        // No "OOPS!!!": the error bubble's color/border already signals "this is a problem",
        // so the text itself doesn't need to shout too.
        appendLine("⚠ " + message);
    }

    private void appendLine(String line) {
        if (buffer.length() > 0) {
            buffer.append("\n");
        }
        buffer.append(line);
    }

    /**
     * Returns true if the reply currently buffered (since the last {@link #flush}) was an error,
     * so the caller can style it differently before flushing.
     */
    public boolean hasError() {
        return hasError;
    }

    /** Returns everything buffered since the last {@link #flush}, as one multi-line reply. */
    public String flush() {
        String text = buffer.toString();
        buffer.setLength(0);
        hasError = false;
        return text;
    }
}
