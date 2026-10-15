package stephen.command;

import stephen.exception.ChatbotException;
import stephen.storage.Storage;
import stephen.task.Task;
import stephen.task.TaskList;
import stephen.ui.Ui;

/** Marks one task as done. */
public class MarkCommand extends Command {
    private final int taskIndex;

    /**
     * Creates a command that marks the task at the given zero-based index.
     *
     * @param taskIndex zero-based index of the task to mark
     */
    public MarkCommand(int taskIndex) {
        assert taskIndex >= 0 : "A mark command must use a non-negative task index";
        this.taskIndex = taskIndex;
    }

    /**
     * Marks the selected task as done, saves the list, and displays a confirmation.
     *
     * @param tasks task list to update
     * @param ui console UI used to display the confirmation
     * @param storage persistence service used to save the updated list
     * @throws ChatbotException if the updated task list cannot be saved
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws ChatbotException {
        Task markedTask = tasks.mark(taskIndex);
        saveTasks(tasks, storage);
        ui.showTaskMarked(markedTask, true);
    }
}
