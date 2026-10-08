package rudra.command;

import java.util.ArrayList;

import rudra.exception.RudraException;
import rudra.storage.Storage;
import rudra.task.Deadline;
import rudra.task.Task;
import rudra.task.TaskDateTime;
import rudra.ui.Ui;

/**
 * Adds a deadline task to the task list.
 */
public class DeadlineCommand extends Command {
    private final String description;
    private final TaskDateTime by;

    /**
     * Creates a command that adds a deadline task.
     *
     * @param description Deadline description.
     * @param by Deadline time.
     */
    public DeadlineCommand(String description, TaskDateTime by) {
        this.description = description;
        this.by = by;
    }

    /**
     * Creates and saves the requested deadline task, then shows the confirmation message.
     *
     * @param tasks Current task list.
     * @param ui UI helper used to show output.
     * @param storage Storage helper used to persist changes.
     * @throws RudraException If the task cannot be saved.
     */
    @Override
    public void execute(ArrayList<Task> tasks, Ui ui, Storage storage) throws RudraException {
        Task deadlineTask = new Deadline(this.description, this.by);
        addTask(tasks, deadlineTask, storage);
        ui.showTaskAdded(deadlineTask, tasks.size());
    }
}
