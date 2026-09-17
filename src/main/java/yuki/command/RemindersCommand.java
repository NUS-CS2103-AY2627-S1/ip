package yuki.command;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

import yuki.storage.Storage;
import yuki.task.TaskList;
import yuki.ui.Ui;

/**
 * Displays incomplete deadlines and events due within the next 24 hours.
 */
public class RemindersCommand extends Command {
    /** Number of hours included in the upcoming-task window. */
    private static final int UPCOMING_WINDOW_HOURS = 24;
    /** Supplies the current local date and time. */
    private final Clock clock;

    /** Creates a reminder query that uses the computer's local clock. */
    public RemindersCommand() {
        this(Clock.systemDefaultZone());
    }

    /**
     * Creates a reminder query that uses the supplied clock.
     *
     * @param clock Clock used to determine the upcoming-task window.
     */
    public RemindersCommand(Clock clock) {
        this.clock = Objects.requireNonNull(clock, "clock must not be null");
    }

    /** Displays upcoming tasks, or reports that there are none. */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        List<Integer> taskNumbers = findUpcomingTaskNumbers(tasks);
        if (taskNumbers.isEmpty()) {
            ui.showNoUpcomingTasks();
            return;
        }
        ui.showUpcomingTasks(tasks.getTasks(), taskNumbers);
    }

    /**
     * Displays upcoming tasks only when at least one exists.
     *
     * @param tasks Tasks managed by Yuki.
     * @param ui User interface used to display the result.
     * @return The value {@code true} if a reminder was displayed.
     */
    public boolean executeIfAny(TaskList tasks, Ui ui) {
        List<Integer> taskNumbers = findUpcomingTaskNumbers(tasks);
        if (taskNumbers.isEmpty()) {
            return false;
        }
        ui.showUpcomingTasks(tasks.getTasks(), taskNumbers);
        return true;
    }

    /** Returns the task numbers inside the 24-hour window starting now. */
    private List<Integer> findUpcomingTaskNumbers(TaskList tasks) {
        LocalDateTime now = LocalDateTime.now(clock).truncatedTo(ChronoUnit.MINUTES);
        return tasks.findUpcomingTaskNumbers(now, now.plusHours(UPCOMING_WINDOW_HOURS));
    }
}
