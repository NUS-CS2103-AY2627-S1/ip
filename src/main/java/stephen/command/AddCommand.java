package stephen.command;

import stephen.exception.ChatbotException;
import stephen.storage.Storage;
import stephen.task.Task;
import stephen.task.TaskList;
import stephen.ui.Ui;

/** Adds one previously parsed task to the task list. */
public class AddCommand extends Command {
    private final Task task;

    /**
     * Creates a command that adds the given task.
     *
     * @param task task to add when the command executes
     */
    public AddCommand(Task task) {
        assert task != null : "An add command must contain a parsed task";
        this.task = task;
    }

    /**
     * Adds the task, saves the updated list, and displays a confirmation.
     *
     * @param tasks task list to update
     * @param ui console UI used to display the confirmation
     * @param storage persistence service used to save the updated list
     * @throws ChatbotException if the updated task list cannot be saved
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws ChatbotException {
        tasks.add(task);
        saveTasks(tasks, storage);
        ui.showTaskAdded(task, tasks.size());
    }
}
