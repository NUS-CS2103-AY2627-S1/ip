package bibi.command;

/**
 * Marks one task incomplete again.
 */
public class UnmarkCommand extends CompletionCommand {
    /**
     * Creates a command that will reopen the given task.
     *
     * @param taskNumber the one-based number shown to the user
     */
    public UnmarkCommand(int taskNumber) {
        super(taskNumber);
    }

    @Override
    protected boolean isCompleteWanted() {
        return false;
    }

    @Override
    protected String describeChange(int taskNumber) {
        return "We're not making progress, huh? Task " + taskNumber + " is open again.";
    }

    @Override
    protected String describeUnchanged(int taskNumber) {
        return "Task " + taskNumber + " was never done, so it stays open.";
    }
}
