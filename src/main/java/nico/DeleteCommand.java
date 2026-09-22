package nico;

import java.util.List;

/**
 * Removes one task from the task list.
 */
public class DeleteCommand extends TaskNumberCommand {
    /**
     * Creates a command to delete the specified task number.
     */
    public DeleteCommand(String taskNumberText) {
        super("delete", taskNumberText);
    }

    /**
     * Deletes the requested task and saves the remaining task list.
     *
     * @param tasks tasks currently managed by the chatbot.
     * @param ui user interface used to display confirmation.
     * @throws NicoException if the task number is invalid or the updated list cannot be saved
     */
    @Override
    public void execute(List<Task> tasks, Ui ui) throws NicoException {
        Task task = getTask(tasks);
        int taskIndex = tasks.indexOf(task);
        tasks.remove(task);
        try {
            TaskStorage.writeAllTasks(Nico.FILE_PATH, tasks);
        } catch (NicoException exception) {
            tasks.add(taskIndex, task);
            throw exception;
        }
        ui.showTaskRemoved(task, tasks.size());
    }
}
