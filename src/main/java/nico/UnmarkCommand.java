package nico;

import java.util.List;

/**
 * Marks one task as incomplete.
 */
public class UnmarkCommand extends TaskNumberCommand {
    /**
     * Creates a command to unmark the specified task number.
     */
    public UnmarkCommand(String taskNumberText) {
        super("unmark", taskNumberText);
    }

    /**
     * Marks the requested task as incomplete and saves the updated task list.
     *
     * @param tasks tasks currently managed by the chatbot.
     * @param ui user interface used to display confirmation.
     * @throws NicoException if the task number is invalid or the updated list cannot be saved
     */
    @Override
    public void execute(List<Task> tasks, Ui ui) throws NicoException {
        Task task = getTask(tasks);
        task.markAsNotDone();
        try {
            TaskStorage.writeAllTasks(Nico.FILE_PATH, tasks);
        } catch (NicoException exception) {
            task.markAsDone();
            throw exception;
        }
        ui.showTaskMarkedNotDone(task);
    }
}
