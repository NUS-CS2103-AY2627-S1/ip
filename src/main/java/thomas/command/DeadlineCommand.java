package thomas.command;

import thomas.ThomasException;
import thomas.storage.Storage;
import thomas.task.Deadline;
import thomas.task.Task;
import thomas.task.TaskList;
import thomas.ui.Ui;

/**
 * Adds a deadline task to the task list.
 */
public class DeadlineCommand extends Command {
    private final String description;
    private final String by;

    /**
     * Creates a command that adds a deadline task.
     *
     * @param description Description of the deadline.
     * @param by Due date/time of the deadline.
     */
    public DeadlineCommand(String description, String by) {
        this.description = description;
        this.by = by;
    }

    /**
     * Adds the deadline, saves the list, and confirms the addition.
     *
     * @param tasks task list to update
     * @param ui user interface used for output
     * @param storage storage used to persist the change
     * @throws ThomasException if the due date/time cannot be parsed
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws ThomasException {
        Task task = new Deadline(description, by);
        if (tasks.containsEquivalent(task)) {
            throw new ThomasException("This task already exists.");
        }
        tasks.add(task);
        storage.save(tasks);
        ui.showTaskAdded(task, tasks.size());
    }
}
