package longfrog.command;
import longfrog.task.Task;
import longfrog.task.TaskList;
import longfrog.ui.Ui;

/** Adds one task and reports the added task through the UI. */
public class AddCommand implements Command {
    private final TaskList taskList;
    private final Task task;

    /**
     * Creates a command that adds a task to the supplied list.
     *
     * @param taskList the list that receives the task
     * @param task the task to add
     */
    public AddCommand(TaskList taskList, Task task) {
        this.taskList = taskList;
        this.task = task;
    }

    /** Adds the task and displays a confirmation. */
    @Override
    public boolean execute(Ui ui) {
        int duplicateIndex = taskList.findDuplicateIndex(task);
        if (duplicateIndex >= 0) {
            Task existingTask = taskList.getTask(duplicateIndex);
            assert existingTask != null : "Duplicate index must identify an existing task";
            ui.showMessage("Duplicate detected; task already exists at position "
                    + (duplicateIndex + 1) + ": " + existingTask);
            return false;
        }

        taskList.addTask(task);
        ui.showMessage("Task secured on the lily pad: " + task);
        return false;
    }
}
