package bobby;

/** Displays all tasks currently stored by Bobby. */
public class ListCommand extends Command {

    /** Creates a command that displays the task list. */
    public ListCommand() {
    }

    /**
     * Shows the current task list.
     *
     * @param tasks current task list to display
     * @param ui interface used to display the tasks
     * @param storage unused because listing does not change persisted data
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showTaskList(tasks);
    }
}
