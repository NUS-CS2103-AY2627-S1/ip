package edith.command;

import edith.exception.EdithException;
import edith.task.Task;
import edith.task.TaskList;
import edith.ui.Ui;

/** Marks one task as completed. */
public class MarkCommand extends Command {
    private final int taskNumber;

    /**
     * Creates a command targeting the supplied one-based task number.
     *
     * @param taskNumber the one-based position of the task to mark as completed
     */
    public MarkCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /**
     * Marks the selected task as completed, saves the updated list, and displays a confirmation.
     *
     * @param tasks the task list containing the task to mark
     * @param ui the user interface used to display the confirmation
     * @throws EdithException if the list is empty, the task number is invalid, or the list cannot be saved
     */
    @Override
    public void execute(TaskList tasks, Ui ui) throws EdithException {
        if (tasks.isEmpty()) {
            throw new EdithException("There are no tasks to mark. Add a task first.");
        }
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new EdithException("Please provide a task number from 1 to " + tasks.size() + ".");
        }

        Task task = tasks.get(taskNumber - 1);
        boolean wasDone = "[X]".equals(task.getStatusIcon());
        task.markAsDone();
        assert "[X]".equals(task.getStatusIcon()) : "A marked task must display as completed";
        saveTasks(tasks, () -> {
            if (!wasDone) {
                task.markAsNotDone();
            }
        });
        ui.showTaskMarked(task, true);
    }
}
