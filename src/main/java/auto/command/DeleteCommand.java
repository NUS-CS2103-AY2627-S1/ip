package auto.command;

import auto.exception.AutoException;
import auto.storage.Storage;
import auto.task.Task;
import auto.task.TaskList;

/** Deletes a numbered task and persists the updated list. */
public class DeleteCommand extends Command {
    private final int taskNumber;

    /**
     * Creates a command that deletes the task with the specified one-based number.
     */
    public DeleteCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /** {@inheritDoc} */
    @Override
    public CommandResult execute(TaskList tasks, Storage storage) throws AutoException {
        Task task = tasks.delete(taskNumber);
        if (!saveTasks(tasks, storage)) {
            tasks.restoreDeleted(taskNumber, task);
            return storageFailure();
        }
        return new CommandResult(String.format(
                "Can, removed this task already:%n  %s%nYou now have %d tasks on your list. Slowly clear, can one.",
                task, tasks.size()));
    }
}
