package hermes.command;

import hermes.HermesException;
import hermes.task.LogBook;
import hermes.task.Task;

/**
 * Adds a task to the task list stored in {@link LogBook}.
 *
 * <p>One class serves todo, deadline and event: by the time a Command is built
 * the Parser has already produced the right kind of {@link Task}, so
 * adding it is the same work in all three cases.
 */
public class AddCommand extends Command {

    private final Task task;

    /**
     * @param task the task the user described.
     */
    public AddCommand(Task task) {
        this.task = task;
    }

    @Override
    public String execute(LogBook logBook) throws HermesException {
        return logBook.log(this.task);
    }
}
