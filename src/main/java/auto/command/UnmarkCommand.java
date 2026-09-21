package auto.command;

import auto.exception.AutoException;
import auto.storage.Storage;
import auto.task.Task;
import auto.task.TaskList;

/** Marks a numbered task as incomplete and persists the change. */
public class UnmarkCommand extends Command {
    private final int taskNumber;

    /**
     * Creates a command that unmarks the task with the specified one-based number.
     */
    public UnmarkCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /** {@inheritDoc} */
    @Override
    public CommandResult execute(TaskList tasks, Storage storage) throws AutoException {
        Task task = tasks.get(taskNumber);
        boolean wasCompleted = task.isCompleted();
        tasks.unmark(taskNumber);
        if (!saveTasks(tasks, storage)) {
            if (wasCompleted) {
                task.mark();
            }
            return storageFailure();
        }
        return new CommandResult("No worries, marked this as not done yet. Take your time lah:"
                + System.lineSeparator() + "  " + task);
    }
}
