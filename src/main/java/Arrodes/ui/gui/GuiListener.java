package arrodes.ui.gui;

/** Receives text submitted through the graphical user interface. */
@FunctionalInterface
public interface GuiListener {
    /**
     * Handles submitted user input.
     *
     * @param input text submitted by the user
     */
    public void execute(String input);
}
