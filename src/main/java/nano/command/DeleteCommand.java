package nano.command;

import nano.NanoException;
import nano.Storage;
import nano.task.Task;
import nano.task.TaskList;

/**
 * Represents the delete command.
 */
public class DeleteCommand implements Command {
    private final int taskNumber;

    /**
     * Creates a delete command for the given task number.
     *
     * @param taskNumber number of the task to delete
     */
    public DeleteCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    @Override
    public String execute(TaskList tasks, Storage storage) throws NanoException {
        Task deletedTask = tasks.deleteTask(taskNumber);
        storage.save(tasks);

        return "Noted. I've removed this task:\n"
                + "  " + deletedTask
                + "\nNow you have " + tasks.size() + " tasks in the list.";
    }
}
