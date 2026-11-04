package soar.command;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import soar.storage.Storage;
import soar.task.TaskList;
import soar.ui.Ui;

/**
 * Shows deadlines and dated events that occur on a requested calendar date.
 */
public class DateCommand extends Command {
    /** Format used when naming the requested date in query results. */
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd uuuu", Locale.ENGLISH);

    /** Validated date whose matching tasks should be shown. */
    private final LocalDate date;

    /**
     * Creates a date query for a validated calendar date.
     *
     * @param date Date whose deadlines and events should be shown.
     */
    public DateCommand(LocalDate date) {
        this.date = Objects.requireNonNull(date, "Query date must not be null");
    }

    /**
     * Finds and displays deadlines and events occurring on the requested date.
     *
     * @param tasks task list to search
     * @param ui interface used to display matching tasks
     * @param storage unused storage dependency supplied by the command interface
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        String displayedDate = date.format(DISPLAY_DATE_FORMAT);
        List<String> matches = tasks.findIndicesOn(date).stream()
                .map(index -> (index + 1) + "." + tasks.get(index))
                .toList();
        ui.showTasksOnDate(displayedDate, matches);
    }
}
