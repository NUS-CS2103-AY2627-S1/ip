package arrodes.command;
import arrodes.storage.Storage;
import arrodes.task.Task;
import arrodes.task.TaskList;
import arrodes.ui.Ui;

/** Command that marks a numbered task as completed. */
public class MarkCommand extends Command {
    /** One-based number of the task to complete. */
    private final int taskNumber;

    /**
     * Creates a command for completing a task.
     * @param taskNumber one-based task number
     */
    public MarkCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /**
     * Marks the task, persists the list, and reports the result.
     * @param ui interface used for output
     * @param taskList list to update
     * @param storage persistence layer
     */
    @Override
    public void execute(Ui ui, TaskList taskList, Storage storage) {
        Task task = taskList.getTaskByNumber(taskNumber);
        task.markAsDone();
        storage.save(taskList);
        ui.showMessage(
                "A worthy task! Arrodes has marked it as done:"
                + "\n"
                + "  "
                + task
        );
    }
}
