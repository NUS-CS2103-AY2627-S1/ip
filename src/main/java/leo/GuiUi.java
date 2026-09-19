package leo;

/**
 * A {@link Ui} that captures Leo's replies into a string instead of
 * printing them to the console. Used by {@link Leo#getResponse(String)}
 * so that the JavaFX GUI (see {@link MainWindow}) can show Leo's replies
 * in a chat bubble, while reusing exactly the same command-handling code
 * in {@link Leo} that the console version ({@link Leo#run()}) uses.
 *
 * <p>Each new GuiUi is meant to be used for a single command: create
 * one, pass it into the command-handling code, then read back everything
 * it collected with {@link #getCapturedText()}.
 */
public class GuiUi extends Ui {
    private final StringBuilder capturedText = new StringBuilder();
    private boolean isError = false;

    @Override
    protected void print(String line) {
        if (capturedText.length() > 0) {
            capturedText.append("\n");
        }
        capturedText.append(line);
    }

    @Override
    public void showError(String message) {
        isError = true;
        super.showError(message);
    }

    /**
     * Returns everything printed to this Ui so far, with each call to
     * {@code print} on its own line (matching how it would have looked
     * printed to the console).
     *
     * @return the captured text
     */
    public String getCapturedText() {
        return capturedText.toString();
    }

    /**
     * Returns whether {@link #showError} was ever called on this Ui -
     * i.e. whether the command this GuiUi was created for ended up
     * showing an error message rather than a normal reply.
     *
     * @return true if showError was called, false otherwise
     */
    public boolean isError() {
        return isError;
    }
}
