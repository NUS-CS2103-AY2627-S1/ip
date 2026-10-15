package sevensix;

/** Displays the command reference without changing session state. */
public final class HelpCommand implements Command {
    /** Returns the shared interface wording for this command. */
    @Override
    public String execute(TaskList tasks, UndoHistory history) {
        return Ui.getHelpMessage();
    }
}
