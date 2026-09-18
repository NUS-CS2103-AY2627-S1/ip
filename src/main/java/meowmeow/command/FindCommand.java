package meowmeow.command;

import meowmeow.storage.Storage;
import meowmeow.task.TaskList;
import meowmeow.ui.Ui;

/**
 * Shows the tasks whose description contains a keyword ("find &lt;keyword&gt;").
 *
 * <p>This mirrors {@link ListCommand}'s "list &lt;date&gt;" filter: it asks
 * {@link TaskList} for the matching tasks and hands them to {@link Ui} to
 * print. The match is a case-insensitive substring test, so "book" also
 * finds "Booking" and "e-BOOK".
 */
public class FindCommand extends Command {
    private final String keyword;

    /**
     * Constructs a find command searching for {@code keyword}.
     *
     * @param keyword the text to look for inside each task's description.
     */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public String execute(TaskList tasks, Ui ui, Storage storage) {
        return ui.showMatchingTasks(tasks.findByKeyword(keyword));
    }
}
