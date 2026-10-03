package ramly.command;

import ramly.exception.TaskValidationException;
import ramly.model.Task;
import ramly.model.TaskList;
import ramly.storage.Storage;
import ramly.ui.Ui;

/** Command that removes a task by its one-based user-facing number. */
public class DeleteCommand extends Command {
    private final int taskIndex;

    /** Creates a delete command for a validated zero-based task index. */
    public DeleteCommand(int taskIndex) {
        assert taskIndex >= 0 : "Task index must not be negative";
        this.taskIndex = taskIndex;
    }

    /** Removes the selected task and persists the updated list. */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        try {
            Task task = tasks.get(taskIndex);
            tasks.remove(taskIndex);
            storage.save(tasks);
            ui.show("Cleared from the trail! I've removed this task:",
                    " " + task,
                    "Now you have " + tasks.size() + " tasks in the list.");
        } catch (IndexOutOfBoundsException e) {
            throw new TaskValidationException(
                    "That trail marker doesn't exist. Use list to check the available numbers.");
        }
    }
}
