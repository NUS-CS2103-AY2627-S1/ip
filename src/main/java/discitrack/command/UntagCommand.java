package discitrack.command;

/**
 * Removes one tag using the same validation and save transaction as tagging.
 */
public class UntagCommand extends TagCommand {
    /**
     * Creates a command to remove one tag.
     *
     * @param taskNumber the full-list task number.
     * @param tag the tag name to remove.
     */
    public UntagCommand(int taskNumber, String tag) {
        super(taskNumber, tag, true);
    }
}
