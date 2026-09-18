package computah.command;

import java.util.ArrayList;

import computah.exception.ComputahException;
import computah.model.Model;
import computah.storage.Storage;
import computah.task.Task;
import computah.ui.Ui;

/**
 * Marks a task as done.
 */
public class MarkCommand extends Command {
    private final int taskIndex;

    /**
     * Creates a command that marks the task at the given zero-based index as done.
     *
     * @param taskIndex zero-based index of the task to mark.
     */
    public MarkCommand(int taskIndex) {
        this.taskIndex = taskIndex;
    }

    /**
     * Marks the task as done, persists the updated list, and shows the confirmation.
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
        tasks.get(taskIndex).markAsDone();
        storage.saveTasks(tasks);
        ui.showTaskMarked(tasks.get(taskIndex));
    }
}
