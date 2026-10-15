package yapbot.ui;

/**
 * A {@link Ui} that captures its output instead of printing to standard
 * output, so a GUI front end can display each command's response text
 * without duplicating any of {@link Ui}'s message wording.
 */
public class GuiUi extends Ui {

    private final StringBuilder buffer = new StringBuilder();
    private boolean lastResponseWasError = false;

    @Override
    protected void output(String line) {
        buffer.append(line).append(System.lineSeparator());
    }

    /**
     * {@inheritDoc} Every rejection/error message in YapBot is shown through
     * this one method (unlike its various success confirmations, which each
     * have their own dedicated {@code show...} method), so overriding just
     * this method is enough to reliably flag "the response about to be
     * consumed is a rejection" for {@link #wasLastResponseAnError()}.
     */
    @Override
    public void showMessage(String message) {
        lastResponseWasError = true;
        super.showMessage(message);
    }

    /**
     * Clears the error flag tracked by {@link #showMessage}. Must be called
     * before executing each new command, so {@link #wasLastResponseAnError()}
     * reflects only that command's outcome rather than a stale one.
     */
    public void startNewResponse() {
        lastResponseWasError = false;
    }

    /**
     * Returns whether the response since the last {@link #startNewResponse()}
     * was an error/rejection message rather than a success confirmation.
     *
     * @return {@code true} if {@link #showMessage} was called.
     */
    public boolean wasLastResponseAnError() {
        return lastResponseWasError;
    }

    /**
     * Returns everything captured since the last call to this method, then
     * clears the buffer so the next command starts with a clean slate.
     *
     * @return the captured output, with the trailing line separator removed,
     *         or an empty string if nothing was captured.
     */
    public String consumeResponse() {
        String response = buffer.toString().stripTrailing();
        buffer.setLength(0);
        return response;
    }
}
