package auto.command;

import auto.exception.AutoException;
import auto.storage.Storage;
import auto.task.Task;
import auto.task.TaskList;

/** Marks a numbered task as complete and persists the change. */
public class MarkCommand extends Command {
    private final int taskNumber;

    /**
     * Creates a command that marks the task with the specified one-based number.
     */
    public MarkCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /** {@inheritDoc} */
    @Override
    public CommandResult execute(TaskList tasks, Storage storage) throws AutoException {
        Task task = tasks.get(taskNumber);
        boolean wasCompleted = task.isCompleted();
        tasks.mark(taskNumber);
        if (!saveTasks(tasks, storage)) {
            if (!wasCompleted) {
                task.unmark();
            }
            return storageFailure();
        }
        return new CommandResult("Steady lah! Marked this task as done:" + System.lineSeparator()
                + "  " + task);
    }
}
