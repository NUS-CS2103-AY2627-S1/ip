/**
 * Marks one task incomplete again.
 */
public class UnmarkCommand extends Command {
    private final int taskNumber;

    /**
     * Creates a command that will reopen the given task.
     *
     * @param taskNumber the one-based number shown to the user
     */
    public UnmarkCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws BibiException {
        tasks.get(taskNumber).markIncomplete();
        ui.showMessage("Unmarked task " + taskNumber + ", now incomplete.");
        saveTasks(tasks, ui, storage);
    }
}
