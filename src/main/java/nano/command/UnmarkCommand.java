package nano.command;

import nano.NanoException;
import nano.Storage;
import nano.task.Task;
import nano.task.TaskList;

/**
 * Represents the unmark command.
 */
public class UnmarkCommand implements Command {
    private final int taskNumber;

    /**
     * Creates an unmark command for the given task number.
     *
     * @param taskNumber number of the task to unmark
     */
    public UnmarkCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    @Override
    public String execute(TaskList tasks, Storage storage) throws NanoException {
        Task task = tasks.getTask(taskNumber);
        task.markUndone();
        storage.save(tasks);

        return "Okay, I've marked this task as not done:\n"
                + "  " + task;
    }
}
