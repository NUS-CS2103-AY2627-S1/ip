package kong.command;

import java.time.LocalDate;

import kong.exception.KongException;
import kong.storage.Storage;
import kong.task.Deadline;
import kong.task.Task;
import kong.task.TaskList;
import kong.ui.Ui;

/**
 * Adds a deadline task to the task list.
 */
public class DeadlineCommand extends Command {
    private final String description;
    private final LocalDate by;

    /**
     * Creates a command that adds a deadline.
     *
     * @param description task description
     * @param by deadline date
     */
    public DeadlineCommand(String description, LocalDate by) {
        this.description = description;
        this.by = by;
    }

    /** {@inheritDoc} */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws KongException {
        Task task = new Deadline(description, by);
        if (tasks.hasDuplicate(task)) {
            throw new KongException("This task already exists in your list.");
        }
        int previousTaskCount = tasks.size();
        tasks.add(task);
        assert tasks.size() == previousTaskCount + 1 : "Adding a task must increase the task count by one";
        ui.showTaskAdded(task);
        saveTasks(tasks, storage);
    }
}
