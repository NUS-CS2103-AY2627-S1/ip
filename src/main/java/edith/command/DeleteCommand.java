package edith.command;

import edith.exception.EdithException;
import edith.task.Task;
import edith.task.TaskList;
import edith.ui.Ui;

/** Removes one task from Edith's task list. */
public class DeleteCommand extends Command {
    private final int taskNumber;

    /**
     * Creates a command targeting the supplied one-based task number.
     *
     * @param taskNumber the one-based position of the task to delete
     */
    public DeleteCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /**
     * Removes the selected task, saves the updated list, and displays a confirmation.
     *
     * @param tasks the task list from which the task is removed
     * @param ui the user interface used to display the confirmation
     * @throws EdithException if the list is empty, the task number is invalid, or the list cannot be saved
     */
    @Override
    public void execute(TaskList tasks, Ui ui) throws EdithException {
        if (tasks.isEmpty()) {
            throw new EdithException("There are no tasks to delete. Add a task first.");
        }
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new EdithException("Please provide a task number from 1 to " + tasks.size() + ".");
        }

        int previousSize = tasks.size();
        Task selectedTask = tasks.get(taskNumber - 1);
        Task removedTask = tasks.remove(taskNumber - 1);
        assert tasks.size() == previousSize - 1 && removedTask == selectedTask
                : "Deleting a task must remove exactly the selected task";
        saveTasks(tasks, () -> tasks.add(taskNumber - 1, removedTask));
        ui.showTaskDeleted(removedTask, tasks.size());
    }
}
