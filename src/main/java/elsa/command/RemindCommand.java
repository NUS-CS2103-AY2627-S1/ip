package elsa.command;

import elsa.Dates;
import elsa.storage.Storage;
import elsa.task.TaskList;
import elsa.ui.Ui;

/**
 * Shows the unfinished deadlines that are overdue or due within the next week.
 *
 * <p>The same reminder is shown when the chatbot starts, which is why the number
 * of days is a public constant rather than a detail of this class: the start of
 * a session and this command must agree about what "soon" means.
 */
public class RemindCommand extends Command {
    /**
     * How many days after today a deadline still counts as due soon. Today and
     * the last day both count, so a deadline on the same weekday next week is
     * included.
     */
    public static final int DAYS_AHEAD = 7;

    /**
     * Creates a command that shows what needs the user's attention.
     * Anything typed after the keyword is not read, in the same way "list"
     * ignores it, so this command is built with no arguments.
     */
    public RemindCommand() {
    }

    @Override
    public String execute(TaskList tasks, Ui ui, Storage storage) {
        // Nothing changes, so the list is not saved.
        return ui.getRemindersMessage(tasks.getReminders(Dates.today(), DAYS_AHEAD), DAYS_AHEAD);
    }
}
