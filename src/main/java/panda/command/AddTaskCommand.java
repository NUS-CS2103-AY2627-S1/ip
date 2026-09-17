package panda.command;

import panda.exception.ApplicationException;
import panda.storage.Storage;
import panda.task.Task;
import panda.task.TaskList;
import panda.ui.Ui;

/**
 * Represents a base command for adding a task and displaying its shared confirmation.
 */
public abstract class AddTaskCommand implements Command {

    /**
     * Initializes an {@code AddTaskCommand}.
     */
    protected AddTaskCommand() {
    }

    /**
     * Adds this command's specific task type to the supplied task list.
     *
     * @param taskList Active task list.
     * @return The newly added task.
     * @throws ApplicationException If the task cannot be added.
     */
    protected abstract Task addTask(TaskList taskList) throws ApplicationException;

    /**
     * Executes the task addition by invoking {@link #addTask()}, then prints a standardized
     * confirmation message displaying the added task and the updated task list size.
     *
     * @param taskList Active task list.
     * @throws ApplicationException If the task cannot be added.
     */
    @Override
    public final void execute(TaskList taskList) throws ApplicationException {
        int previousTaskCount = taskList.getSize();
        Task task = addTask(taskList);
        assert task != null : "addTask() must return the newly added task, but returned null";
        assert taskList.getSize() == previousTaskCount + 1
                : "addTask() must increase TaskList size by exactly one: expected "
                        + (previousTaskCount + 1) + " but was " + taskList.getSize();
        Storage.saveState(taskList);

        int taskCount = taskList.getSize();
        String taskNoun = taskCount == 1 ? "task" : "tasks";
        Ui.printMessage("A task? Groundbreaking. I've added it to your training plan:"
                + System.lineSeparator()
                + "  " + task + System.lineSeparator()
                + "That's " + taskCount + " " + taskNoun + " standing between you and basic competence.");
    }
}
