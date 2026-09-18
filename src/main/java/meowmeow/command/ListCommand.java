package meowmeow.command;

import meowmeow.storage.Storage;
import meowmeow.task.TaskDateTime;
import meowmeow.task.TaskList;
import meowmeow.ui.Ui;

/**
 * Shows the task list - either the whole list ("list") or just the tasks
 * occurring on one day ("list &lt;date&gt;"). Which one is decided by
 * whether a date was given: a {@code null} {@link #date} means "show
 * everything".
 *
 * <p>One class with a nullable field rather than two classes, because both
 * forms answer the same question ("which tasks should I show?") and only
 * differ by the filter.
 */
public class ListCommand extends Command {
    private final TaskDateTime date;

    /** Constructs the "list" command with no date, which shows every task. */
    public ListCommand() {
        this.date = null;
    }

    /**
     * Constructs the "list &lt;date&gt;" command, which shows only the tasks on
     * {@code date}. A time, if the user gave one, is ignored since the filter
     * matches by day.
     */
    public ListCommand(TaskDateTime date) {
        this.date = date;
    }

    @Override
    public String execute(TaskList tasks, Ui ui, Storage storage) {
        if (date == null) {
            return ui.showTasks(tasks.asList());
        }
        return ui.showTasksOn(date.toDateString(), tasks.findOn(date.getDate()));
    }
}
