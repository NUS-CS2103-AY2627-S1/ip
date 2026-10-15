package clover.command;

import clover.exception.CloverException;
import clover.storage.Storage;
import clover.task.Task;
import clover.task.TaskList;
import clover.tutoree.TutoreeList;
import clover.ui.Ui;

/**
 * Marks an existing task as incomplete.
 */
public class UnmarkCommand extends Command {
    private final String taskNumber;

    /**
     * Creates a command for the supplied one-based task number.
     */
    public UnmarkCommand(String taskNumber) {
        this.taskNumber = taskNumber;
    }

    /**
     * Unmarks and saves the selected task.
     */
    @Override
    public void execute(TaskList tasks, TutoreeList tutorees, Ui ui, Storage storage) throws CloverException {
        int taskIndex = parseValidTaskIndex(taskNumber, tasks, "Choose a valid quest number to tend again.");
        Task task = tasks.get(taskIndex);
        if (!task.isDone()) {
            throw new CloverException("That study quest is already incomplete.");
        }
        boolean wasDone = task.isDone();
        task.markAsUndone();
        try {
            saveTasks(tasks, storage);
        } catch (CloverException exception) {
            if (wasDone) {
                task.markAsDone();
            }
            throw exception;
        }
        ui.showTaskUnmarked(task);
    }
}
