package zucc.command;

import zucc.ZuccException;
import zucc.task.Deadline;
import zucc.task.Priority;
import zucc.task.Task;

/**
 * Adds a deadline task from a raw description and {@code /by} value.
 */
public final class DeadlineCommand extends AddTaskCommand {
    /** Creates a deadline command awaiting values from Parser. */
    DeadlineCommand() {
        super("deadline", "/by", "/priority");
    }

    /**
     * Validates required input and constructs the deadline task.
     *
     * @return constructed deadline task.
     * @throws ZuccException if the description or deadline is missing or malformed.
     */
    @Override
    protected Task createTask() throws ZuccException {
        return new Deadline(
                requireArgument("a description"),
                require("/by"),
                Priority.fromUserInput(getOption("/priority")));
    }
}
