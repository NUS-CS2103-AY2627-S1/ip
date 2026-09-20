package fifi.command;

import java.time.DateTimeException;

import fifi.TaskList;
import fifi.exception.InvalidDescriptionException;
import fifi.task.Task;

/**
 * Marks a task as done.
 */
public class MarkCommand extends TaskChangingCommand {
    private final int taskIndex;

    /**
     * Creates a command that marks the task at the given zero-based index.
     *
     * @param taskIndex the zero-based task index
     */
    public MarkCommand(int taskIndex) {
        this.taskIndex = taskIndex;
    }

    @Override
    protected String[] applyChange(TaskList tasks) throws InvalidDescriptionException {
        Task currentTask = tasks.get(taskIndex);
        try {
            currentTask.mark();
        } catch (DateTimeException exception) {
            throw new InvalidDescriptionException(exception.getMessage());
        }
        // Every task implementation must honor mark() before its state is saved or reported.
        assert currentTask.isMarked() : "A task must be marked after mark()";
        return new String[]{"Nice! I've marked this task as done:", currentTask.toString()};
    }
}
