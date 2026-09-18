package herta.command;

import java.time.DateTimeException;
import java.time.LocalDateTime;

import herta.exception.HertaException;
import herta.exception.UsageGuidanceException;
import herta.storage.Storage;
import herta.task.TaskList;
import herta.ui.UiOutput;

/**
 * Represents the command that displays incomplete tasks in a future time window.
 */
public class UpcomingCommand extends QueryCommand {
    /** Error message used when the requested upcoming range cannot be represented. */
    public static final String INVALID_UPCOMING_DAYS_ERROR = "That day count is not useful. "
            + "Use: upcoming <days>, with a positive number in range.";
    private static final String UPCOMING_HEADING = "The next %d days, arranged for you:";
    private static final String UPCOMING_EMPTY_MESSAGE =
            "Nothing upcoming. Enjoy the silence while it lasts.";
    private final int days;

    /**
     * Creates a command that searches the specified number of future days.
     *
     * @param days the positive number of days in the search window.
     */
    public UpcomingCommand(int days) {
        if (days <= 0) {
            throw new IllegalArgumentException(
                    "An upcoming command must search a positive number of days.");
        }
        this.days = days;
    }

    /**
     * Displays incomplete deadlines and events beginning in the future window.
     *
     * @param tasks the task list to search.
     * @param ui the output interface used to display responses.
     * @param storage unused because querying does not change stored data.
     * @throws HertaException if the requested time range is too large.
     */
    @Override
    public void execute(TaskList tasks, UiOutput ui, Storage storage) throws HertaException {
        LocalDateTime now = LocalDateTime.now();
        final LocalDateTime until;
        try {
            until = now.plusDays(days);
        } catch (DateTimeException e) {
            throw new UsageGuidanceException(INVALID_UPCOMING_DAYS_ERROR);
        }

        showMatchingTasks(tasks,
                task -> !task.isCompleted() && task.isUpcoming(now, until),
                UPCOMING_HEADING.formatted(days),
                UPCOMING_EMPTY_MESSAGE,
                ui);
    }
}
