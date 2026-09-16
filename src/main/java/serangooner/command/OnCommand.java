package serangooner.command;

import serangooner.storage.Storage;
import serangooner.task.DateRange;
import serangooner.task.TaskList;
import serangooner.ui.Ui;

/**
 * Shows the tasks falling within a span of dates.
 */
public class OnCommand extends Command {
    private final DateRange range;

    /**
     * Constructs a command that reports on the given span of dates.
     *
     * @param range Span of dates to report on.
     */
    public OnCommand(DateRange range) {
        this.range = range;
    }

    @Override
    public String execute(TaskList tasks, Ui ui, Storage storage) {
        return ui.formatTasksInRange(tasks.getEntriesWithin(range.start(), range.end()),
                range.start(), range.end());
    }
}
