package topaz.command;

import topaz.TopazException;
import topaz.storage.Storage;
import topaz.task.Task;
import topaz.task.TaskList;
import topaz.ui.Ui;

/**
 * Marks one task as done and saves the updated task list.
 */
public class MarkCommand extends Command {
    private final int taskIndex;

    /**
     * Creates a command for the given zero-based task index.
     *
     * @param taskIndex the zero-based index of the task to mark
     */
    public MarkCommand(int taskIndex) {
        this.taskIndex = taskIndex;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws TopazException {
        // Parser validates taskIndex against the current task list before creating this command.
        assert taskIndex >= 0 && taskIndex < tasks.size()
                : "A mark command must refer to an existing task.";
        Task task = tasks.get(taskIndex);
        boolean wasDone = task.isDone();
        tasks.markAsDone(taskIndex);
        assert task.isDone() : "Marking a task must set its completion status.";
        try {
            storage.save(tasks.asList());
        } catch (TopazException exception) {
            if (wasDone) {
                tasks.markAsDone(taskIndex);
            } else {
                tasks.markAsNotDone(taskIndex);
            }
            assert task.isDone() == wasDone
                    : "A failed save must restore the task's original completion status.";
            throw exception;
        }
        ui.showMarkedTask(task);
    }
}
