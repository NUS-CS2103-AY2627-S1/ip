package quackie.command;

import quackie.storage.Storage;
import quackie.task.Task;
import quackie.task.TaskList;
import quackie.ui.Ui;

/**
 * Adds a parsed task to the task list.
 */
public class AddCommand extends Command {
    private final Task task;

    /**
     * Creates a command that adds the supplied task.
     *
     * @param task the task to add
     */
    public AddCommand(Task task) {
        this.task = task;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        if (tasks.contains(task)) {
            ui.showError("That task is already in your list.");
            return;
        }
        if (!tasks.add(task)) {
            ui.showError("Your task list is full. Delete a task before adding another.");
            return;
        }
        saveTasks(tasks, ui, storage);
        ui.showTaskAdded(task, tasks.size());
    }
}
