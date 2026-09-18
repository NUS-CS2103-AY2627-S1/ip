package bibi.command;

/**
 * Marks one task complete.
 */
public class MarkCommand extends CompletionCommand {
    /**
     * Creates a command that will complete the given task.
     *
     * @param taskNumber the one-based number shown to the user
     */
    public MarkCommand(int taskNumber) {
        super(taskNumber);
    }

    @Override
    protected boolean isCompleteWanted() {
        return true;
    }

    @Override
    protected String describeChange(int taskNumber) {
        return "Task " + taskNumber + " done. Look at us getting things done.";
    }

    @Override
    protected String describeUnchanged(int taskNumber) {
        return "Task " + taskNumber + " was already done. Doing it twice won't count twice.";
    }
}
