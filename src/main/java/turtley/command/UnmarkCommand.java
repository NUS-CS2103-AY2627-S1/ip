package turtley.command;

import turtley.model.Task;
import turtley.model.TaskList;
import turtley.storage.Storage;
import turtley.ui.Ui;

/**
 * Marks one task as not done.
 */
public class UnmarkCommand extends IndexedCommand {

    private final String input;

    /**
     * Creates an unmark command.
     *
     * @param input the user-supplied task number.
     */
    public UnmarkCommand(String input) {
        this.input = input;
    }

    /**
     * Marks the selected task as not done and persists the updated task list.
     *
     * @param tasks the application's task list.
     * @param ui the application's user interface.
     * @param storage the application's persistence service.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        int taskIndex = requireTaskIndex(input, tasks);
        Task task = tasks.get(taskIndex);
        boolean wasDone = task.isDone();
        task.markAsNotDone();
        assert !task.isDone() : "markAsNotDone() must clear the task's completion state.";
        saveOrRollback(tasks, storage, () -> {
            if (wasDone) {
                task.markAsDone();
            }
        });
        ui.showTaskUnmarked(task);
    }
}
