package stephen.command;

import stephen.exception.ChatbotException;
import stephen.storage.Storage;
import stephen.task.Task;
import stephen.task.TaskList;
import stephen.ui.Ui;

/** Deletes one task from the task list. */
public class DeleteCommand extends Command {
    private final int taskIndex;

    /**
     * Creates a command that deletes the task at the given zero-based index.
     *
     * @param taskIndex zero-based index of the task to delete
     */
    public DeleteCommand(int taskIndex) {
        assert taskIndex >= 0 : "A delete command must use a non-negative task index";
        this.taskIndex = taskIndex;
    }

    /**
     * Deletes the selected task, saves the updated list, and displays a confirmation.
     *
     * @param tasks task list to update
     * @param ui console UI used to display the confirmation
     * @param storage persistence service used to save the updated list
     * @throws ChatbotException if the updated task list cannot be saved
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws ChatbotException {
        Task deletedTask = tasks.delete(taskIndex);
        saveTasks(tasks, storage);
        ui.showTaskDeleted(deletedTask, tasks.size());
    }
}
