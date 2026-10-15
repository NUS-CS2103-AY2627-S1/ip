package lumine.ui;

/**
 * Captures Lumine's messages for display in the JavaFX conversation window.
 */
public class GuiUi extends Ui {
    private final StringBuilder output = new StringBuilder();

    /**
     * Creates a GUI output collector without standard-input support.
     */
    public GuiUi() {
        super(false);
    }

    /**
     * {@inheritDoc}
     * Stores a compact greeting suitable for a chat bubble.
     */
    @Override
    public void showGreetings() {
        showMessage("Hello, I'm Lumine!\nWhat can I do for you today?");
    }

    /**
     * {@inheritDoc}
     * Stores the message instead of writing it to standard output.
     */
    @Override
    public void showMessage(String message) {
        if (!output.isEmpty()) {
            output.append('\n');
        }
        output.append(message);
    }

    /**
     * Returns all messages produced while a command was handled.
     *
     * @return the captured command output.
     */
    public String getOutput() {
        return output.toString();
    }
}
