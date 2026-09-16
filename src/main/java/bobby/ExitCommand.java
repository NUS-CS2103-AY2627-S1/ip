package bobby;

/** Ends Bobby's session and shows the farewell message. */
public class ExitCommand extends Command {

    /** Creates a command that ends the session. */
    public ExitCommand() {
    }

    /**
     * Shows Bobby's farewell message.
     *
     * @param tasks current task list, which is not modified
     * @param ui interface used to display the farewell
     * @param storage persistence service, which is not used
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showGoodbye();
    }

    /**
     * Returns that this command ends the command loop.
     *
     * @return always {@code true}
     */
    @Override
    public boolean isExit() {
        return true;
    }
}
