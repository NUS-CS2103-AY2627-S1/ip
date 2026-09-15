package luigibot.command;

import luigibot.storage.Storage;
import luigibot.task.Task;
import luigibot.task.TaskList;
import luigibot.ui.Ui;

/**
 * Deletes a task from LuigiBot's task list.
 */
public class DeleteCommand extends Command {
    private static final String TASK_NOT_FOUND_ERROR =
            "Oh no! Luigi can't-a find that task number.";

    private final int taskNumber;

    /**
     * Creates a command that deletes the task with the given displayed number.
     *
     * @param taskNumber displayed task number, starting from 1.
     */
    public DeleteCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /**
     * Deletes the task, saves the updated list, and displays a confirmation.
     *
     * @param tasks stored tasks.
     * @param ui user interface used to display the confirmation.
     * @param storage storage used to persist the updated task list.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        if (!tasks.isValidTaskNumber(this.taskNumber)) {
            throw new IllegalArgumentException(TASK_NOT_FOUND_ERROR);
        }

        Task removedTask = tasks.delete(this.taskNumber);
        storage.save(tasks, ui);
        ui.showTaskDeleted(removedTask, tasks.size());
    }
}
