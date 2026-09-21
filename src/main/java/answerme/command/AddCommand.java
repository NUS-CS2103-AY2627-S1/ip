package answerme.command;

import answerme.exception.AnswerMeException;
import answerme.storage.Storage;
import answerme.task.Task;
import answerme.task.TaskList;
import answerme.ui.Ui;

/**
 * Represents a command that adds a task to the task list.
 */
public abstract class AddCommand extends Command {
    private final Task task;

    /**
     * Creates a command that adds the specified task.
     *
     * @param task The task to add.
     */
    protected AddCommand(Task task) {
        this.task = task;
    }

    /**
     * {@inheritDoc}
     *
     * Adds this command's task to the task list, saves the task list
     * and displays a confirmation.
     */
    @Override
    public void execute(TaskList taskList, Ui ui, Storage storage)
            throws AnswerMeException {
        if (taskList.containsDuplicateTask(task)) {
            throw new AnswerMeException("A duplicate task cannot be added. "
                    + "Please name it something else.");
        }

        int originalListSize = taskList.size();
        taskList.add(task);
        assert taskList.size() == originalListSize + 1;

        try {
            storage.saveTasks(taskList);
        } catch (AnswerMeException exception) {
            taskList.remove(task);
            throw exception;
        }

        ui.printAddNewItem(task, taskList.size());
    }

    /**
     * Checks equality of {@code AddCommand} objects.
     *
     * @param obj   the reference object with which to compare.
     * @return {@code true} if objects are equal, otherwise {@code false}.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof AddCommand other) {
            return task.equals(other.task);
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return task.hashCode();
    }
}
