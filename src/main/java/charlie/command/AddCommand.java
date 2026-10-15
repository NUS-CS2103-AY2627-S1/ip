package charlie.command;

import charlie.storage.Storage;
import charlie.task.Task;
import charlie.task.TaskList;
import charlie.ui.Ui;

/**
 * Adds one task to the task list.
 */
public class AddCommand extends Command {
    /** Task to add when this command executes. */
    private final Task task;

    /**
     * Creates a command for the task to add.
     *
     * @param task Task to add.
     */
    public AddCommand(Task task) {
        this.task = task;
    }

    /**
     * Adds the task, saves the task list, and displays the result.
     *
     * @param tasks Task collection to update.
     * @param ui User interface used to display the result.
     * @param storage Storage used to persist the updated task list.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        storage.save(tasks.getTasksAfterAdding(task));
        tasks.add(task);
        ui.showMessages(
                "A happy little roar! I've tucked this task safely into the nest:",
                "  " + task);
        ui.showTaskCount(tasks.getSize());
    }
}
