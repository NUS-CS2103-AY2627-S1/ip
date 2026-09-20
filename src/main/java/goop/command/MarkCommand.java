package goop.command;

import java.io.IOException;

import goop.exception.GoopException;
import goop.storage.Storage;
import goop.task.TaskList;
import goop.ui.Ui;

/**
 * Represents a request to mark one task as complete.
 */
public class MarkCommand extends TaskCommand {
    /**
     * Creates a mark command for a one-based task number.
     *
     * @param taskNumber Task to mark.
     */
    public MarkCommand(int taskNumber) {
        super(taskNumber);
    }

    /**
     * Marks the task, persists the list, and rolls back if saving fails.
     *
     * @param tasks Task list containing the task to mark.
     * @param ui User interface used to display the marked task.
     * @param storage Storage used to persist the updated task list.
     * @throws GoopException If the requested task number is outside the list.
     * @throws IOException If the updated task list cannot be saved.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage)
            throws GoopException, IOException {
        int taskIndex = getTaskIndex(tasks, "mark");
        setDoneAndSave(tasks, taskIndex, true, storage);
        ui.showMarkedTask(tasks.get(taskIndex));
    }
}
