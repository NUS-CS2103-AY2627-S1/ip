package nico;

import java.util.List;

/**
 * Marks one task as complete.
 */
public class MarkCommand extends TaskNumberCommand {
    /**
     * Creates a command to mark the specified task number.
     */
    public MarkCommand(String taskNumberText) {
        super("mark", taskNumberText);
    }

    /**
     * Marks the requested task as complete and saves the updated task list.
     *
     * @param tasks tasks currently managed by the chatbot.
     * @param ui user interface used to display confirmation.
     * @throws NicoException if the task number is invalid or the updated list cannot be saved
     */
    @Override
    public void execute(List<Task> tasks, Ui ui) throws NicoException {
        Task task = getTask(tasks);
        task.markAsDone();
        try {
            TaskStorage.writeAllTasks(Nico.FILE_PATH, tasks);
        } catch (NicoException exception) {
            task.markAsNotDone();
            throw exception;
        }
        ui.showTaskMarkedDone(task);
    }
}
