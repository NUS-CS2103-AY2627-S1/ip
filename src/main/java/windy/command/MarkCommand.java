package windy.command;

/**
 * Marks a task as completed.
 */
public class MarkCommand extends TaskCompletionCommand {

    /**
     * Creates a command that marks the task at the specified index.
     *
     * @param taskIndex the zero-based index of the task to mark.
     */
    public MarkCommand(int taskIndex) {
        super(taskIndex, true);
    }
}
