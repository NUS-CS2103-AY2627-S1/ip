package avon.command;

import avon.exception.AvonException;
import avon.exception.DuplicateTaskException;
import avon.exception.StorageException;
import avon.storage.Storage;
import avon.task.Task;
import avon.task.TaskList;
import avon.ui.Ui;

/**
 * Adds a parsed task to Avon's task list.
 */
public class AddCommand extends Command {
    private final Task task;

    /**
     * Creates a command for the task to add.
     *
     * @param task the parsed task.
     */
    public AddCommand(Task task) {
        this.task = task;
    }

    /** {@inheritDoc} */
    @Override
    public void execute(TaskList taskList, Ui ui, Storage storage) throws AvonException {
        if (taskList.containsDuplicateOf(task)) {
            throw new DuplicateTaskException();
        }

        int originalTaskCount = taskList.size();
        taskList.add(task);
        assert taskList.size() == originalTaskCount + 1
                : "Adding a task must increase the task count by one";
        try {
            storage.save(taskList);
        } catch (StorageException exception) {
            taskList.removeTask(taskList.size() - 1);
            assert taskList.size() == originalTaskCount
                    : "A failed save must restore the original task count";
            throw exception;
        }
        ui.showAddedTask(task, taskList.size());
    }
}
