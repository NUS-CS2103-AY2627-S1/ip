package arrodes.command;
import arrodes.storage.Storage;
import arrodes.task.Task;
import arrodes.task.TaskList;
import arrodes.ui.Ui;

/** Command that marks a numbered task as incomplete. */
public class UnmarkCommand extends Command {
    /** One-based number of the task to reopen. */
    private final int taskNumber;

    /**
     * Creates a command for reopening a task.
     * @param taskNumber one-based task number
     */
    public UnmarkCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /**
     * Marks the task incomplete, persists the list, and reports the result.
     * @param ui interface used for output
     * @param taskList list to update
     * @param storage persistence layer
     */
    @Override
    public void execute(Ui ui, TaskList taskList, Storage storage) {
        Task task = taskList.getTaskByNumber(taskNumber);
        task.markAsNotDone();
        storage.save(taskList);
        ui.showMessage("As you decree, Arrodes has marked this task as not done yet:"
                + "\n'"
                + "  "
                + task
        );
    }
}
