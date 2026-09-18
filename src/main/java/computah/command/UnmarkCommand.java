package computah.command;

import java.util.ArrayList;

import computah.exception.ComputahException;
import computah.model.Model;
import computah.storage.Storage;
import computah.task.Task;
import computah.ui.Ui;

/**
 * Marks a task as not done.
 */
public class UnmarkCommand extends Command {
    private final int taskIndex;

    /**
     * Creates a command that marks the task at the given zero-based index as not done.
     *
     * @param taskIndex zero-based index of the task to unmark.
     */
    public UnmarkCommand(int taskIndex) {
        this.taskIndex = taskIndex;
    }

    /**
     * Marks the task as not done, persists the updated list, and shows the confirmation.
     *
     * @param model current application data.
     * @param ui user interface used to display the confirmation.
     * @param storage storage used to persist the updated task list.
     * @throws ComputahException if the updated task list cannot be saved.
     */
    @Override
    public void execute(Model model, Ui ui, Storage storage) throws ComputahException {
        ArrayList<Task> tasks = model.getTasks();
        assert taskIndex >= 0 && taskIndex < tasks.size()
                : "Parser should supply an index of an existing task";
        tasks.get(taskIndex).markAsNotDone();
        storage.saveTasks(tasks);
        ui.showTaskUnmarked(tasks.get(taskIndex));
    }
}
