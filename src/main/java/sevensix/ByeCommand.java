package sevensix;

/** Produces the farewell response without changing session state. */
public final class ByeCommand implements Command {
    /** Returns the shared interface wording for this command. */
    @Override
    public String execute(TaskList tasks, UndoHistory history) {
        return Ui.getFarewellMessage();
    }
}
