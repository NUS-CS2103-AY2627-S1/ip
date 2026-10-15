package nano.command;

import nano.NanoException;
import nano.Storage;
import nano.task.Task;
import nano.task.TaskList;

/**
 * Represents the mark command.
 */
public class MarkCommand implements Command {
    private final int taskNumber;

    public MarkCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    @Override
    public String execute(TaskList tasks, Storage storage) throws NanoException {
        assert tasks != null : "Task list should not be null";

        Task task = tasks.getTask(taskNumber);
        task.markDone();
        storage.save(tasks);

        return "Nice! I've marked this task as done:\n"
                + "  " + task;
    }
}
