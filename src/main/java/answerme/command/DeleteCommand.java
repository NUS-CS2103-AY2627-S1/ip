package answerme.command;

import answerme.exception.AnswerMeException;
import answerme.storage.Storage;
import answerme.task.Task;
import answerme.task.TaskList;
import answerme.ui.Ui;

/**
 * Represents a command that deletes a task from the task list.
 */
public class DeleteCommand extends Command {
    private final int taskIndex;

    /**
     * Creates a command that deletes the task at the specified index.
     *
     * @param taskIndex The index of the task to delete.
     */
    public DeleteCommand(int taskIndex) {
        this.taskIndex = taskIndex;
    }

    /**
     * {@inheritDoc}
     *
     * Deletes the task at the specified index, saves the updated task list,
     * and displays a confirmation.
     *
     * @throws AnswerMeException If no task exists at the specified index or
     *                           the updated task list cannot be saved.
     */
    @Override
    public void execute(TaskList taskList, Ui ui, Storage storage)
            throws AnswerMeException {
        Task deletedTask = taskList.getTask(taskIndex);
        int originalListSize = taskList.size();
        taskList.remove(taskIndex);
        assert taskList.size() == originalListSize - 1;

        try {
            storage.saveTasks(taskList);
        } catch (AnswerMeException exception) {
            taskList.add(taskIndex, deletedTask);
            throw exception;
        }

        ui.printDeleteItem(deletedTask, taskList.size());
    }
}
