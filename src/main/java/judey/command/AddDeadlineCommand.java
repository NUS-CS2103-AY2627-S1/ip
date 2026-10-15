package judey.command;

import judey.exception.JudeyException;
import judey.storage.Storage;
import judey.task.Deadline;
import judey.task.Task;
import judey.tasklist.TaskList;
import judey.ui.Ui;

/**
 * Represents the action of adding a Deadline task
 */
public class AddDeadlineCommand extends Command {
    private final String description;
    private final String by;

    /**
     * Creates a Deadline task
     * @param description represents the task description
     * @param by represents the deadline the task is due by
     */
    public AddDeadlineCommand(String description, String by) {
        this.description = description;
        this.by = by;
    }

    /**
     * {@inheritDoc}
     *
     * @throws JudeyException If the deadline format is invalid or saving fails.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws JudeyException {
        Task task = new Deadline(description, by);
        tasks.add(task);
        storage.save(tasks.getTasks());
        ui.showTaskAdded(task, tasks.getSize());
    }
}