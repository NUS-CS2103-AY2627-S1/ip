package pixel.command;

import pixel.storage.Storage;
import pixel.task.Task;
import pixel.task.TaskList;
import pixel.ui.Ui;

/**
 * Adds a parsed task to the task list.
 */
public class AddCommand extends Command {
    private final Task task;

    /**
     * Creates a command that adds the specified task.
     *
     * @param task Task to add.
     */
    public AddCommand(Task task) {
        this.task = task;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        if (tasks.containsSameDetails(task)) {
            throw new IllegalArgumentException("This task is already in your list.");
        }
        tasks.add(task);
        saveTasksSafely(tasks, ui, storage);
        ui.showTaskAdded(task, tasks.size());
    }
}
