package alfred.command;

import alfred.task.TaskDateTime;
import alfred.task.TaskList;
import alfred.ui.Ui;

/**
 * Lists deadlines and events that fall on a given date.
 */
public class OnCommand extends Command {
    /** Date to match against stored deadlines and events. */
    private final TaskDateTime query;

    /**
     * Creates a command that lists tasks occurring on {@code query}.
     *
     * @param query Parsed date from the {@code on} command.
     */
    public OnCommand(TaskDateTime query) {
        // Parser rejects an unparseable date before constructing this command.
        assert query != null : "OnCommand requires a parsed date";
        this.query = query;
    }

    @Override
    public void execute(TaskList tasks, Ui ui) {
        ui.showTasksOn(tasks.getTasks(), query);
    }
}
