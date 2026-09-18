package chausistant.command;

/** Command that marks one task as completed. */
public class MarkCommand extends StatusCommand {

    /** Creates a command for the task number supplied after {@code mark}. */
    public MarkCommand(String taskNumberText) {
        super("mark", taskNumberText, true);
    }
}
