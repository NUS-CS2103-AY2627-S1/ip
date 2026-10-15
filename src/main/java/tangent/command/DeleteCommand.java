package tangent.command;

import java.util.List;

import tangent.exception.TangentException;
import tangent.storage.Storage;
import tangent.task.Task;
import tangent.task.TaskList;
import tangent.ui.Ui;

/** Removes one or more tasks from the task list and saves the result. */
public class DeleteCommand extends Command {

    /** 0-based indexes of the tasks to be removed. */
    private final List<Integer> taskIndexes;

    /** Creates a command that deletes tasks at the specified 0-based indexes. */
    public DeleteCommand(List<Integer> taskIndexes) {
        this.taskIndexes = List.copyOf(taskIndexes);
    }

    /**
     * Deletes a task from {@code tasks} at the specified index and persists the removal in {@code storage}.
     *
     * @throws TangentException if the data file cannot be written to, or the task index is invalid.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws TangentException {
        List<Integer> resolvedIndexes = tasks.resolveDisplayedIndexes(taskIndexes);
        List<Task> removedTasks = tasks.removeAtIndexes(resolvedIndexes);
        try {
            storage.save(tasks.toList());
        } catch (TangentException e) {
            tasks.restoreAtIndexes(resolvedIndexes, removedTasks);
            throw e;
        }
        ui.showTasksDeleted(removedTasks, tasks);
    }
}
