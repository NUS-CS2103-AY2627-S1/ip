package chausistant.command;

/** Command that marks one task as not completed. */
public class UnmarkCommand extends StatusCommand {

    /** Creates a command for the task number supplied after {@code unmark}. */
    public UnmarkCommand(String taskNumberText) {
        super("unmark", taskNumberText, false);
    }
}
