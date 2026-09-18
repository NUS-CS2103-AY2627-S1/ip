package herta.command;

import herta.exception.HertaException;
import herta.storage.Storage;
import herta.task.Task;
import herta.task.TaskList;
import herta.ui.UiOutput;

/**
 * Represents the command that removes a task from the task list.
 */
public class DeleteCommand extends TaskIndexCommand {
    private static final String DELETE_CONFIRMATION_MESSAGE = "Gone. I've removed it:";

    /**
     * Creates a command that removes the task at the given index.
     *
     * @param taskIndex the zero-based index of the task to remove.
     */
    public DeleteCommand(int taskIndex) {
        super(taskIndex);
    }

    /**
     * Saves the list without the selected task, then removes it from the live list.
     *
     * @param tasks the task list to update.
     * @param ui the output interface used to display responses.
     * @param storage the storage used to persist the deletion.
     * @throws HertaException if the selected task is invalid or the updated list cannot be saved.
     */
    @Override
    public void execute(TaskList tasks, UiOutput ui, Storage storage) throws HertaException {
        int taskIndex = getTaskIndex(tasks);
        Task task = tasks.get(taskIndex);
        TaskList updatedTasks = new TaskList(tasks.asUnmodifiableList());
        updatedTasks.remove(taskIndex);
        storage.save(updatedTasks);
        tasks.remove(taskIndex);
        ui.showMessage(DELETE_CONFIRMATION_MESSAGE);
        ui.showTask(task);
        ui.showTaskCount(tasks.size());
    }
}
