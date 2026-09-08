package crack.gui;

import crack.Ui;

/**
 * Collects what Crack says instead of printing it, so the window can put the
 * same wording inside a dialog box. It also remembers whether the reply was a
 * complaint, so the window can paint it differently.
 */
public class GuiUi extends Ui {
    private final StringBuilder reply = new StringBuilder();
    private boolean isErrorReply;

    /**
     * A finished reply, together with whether it reports something going wrong.
     *
     * @param text What Crack said.
     * @param isError Whether any part of it was an error message.
     */
    public record Reply(String text, boolean isError) { }

    /**
     * Skips the divider, since every reply already sits in its own bubble.
     */
    @Override
    public void showLine() {
        // Nothing to draw: the window separates messages by putting them in separate bubbles.
    }

    @Override
    public void showError(String message) {
        isErrorReply = true;
        super.showError(message);
    }

    /**
     * Returns everything said since the last call, then starts collecting afresh.
     *
     * @return The collected reply, with no trailing newline.
     */
    public Reply takeReply() {
        Reply collected = new Reply(reply.toString(), isErrorReply);
        reply.setLength(0);
        isErrorReply = false;
        return collected;
    }

    @Override
    protected void print(String line) {
        if (!reply.isEmpty()) {
            reply.append("\n");
        }
        reply.append(line);
    }
}
