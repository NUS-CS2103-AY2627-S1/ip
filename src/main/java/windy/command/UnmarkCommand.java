package windy.command;

/**
 * Marks a task as not completed.
 */
public class UnmarkCommand extends TaskCompletionCommand {

    /**
     * Creates a command that unmarks the task at the specified index.
     *
     * @param taskIndex the zero-based index of the task to unmark.
     */
    public UnmarkCommand(int taskIndex) {
        super(taskIndex, false);
    }
}
