package daddy.command;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import daddy.exception.DaddyException;
import daddy.storage.Storage;
import daddy.task.TaskList;
import daddy.ui.Ui;

/**
 * Finds and displays the earliest working-hours slot of a requested duration.
 */
public class FreeCommand extends Command {
    /** Stores the length of free time requested by the user. */
    private final Duration duration;
    /** Supplies the current local time and allows deterministic command tests. */
    private final Clock clock;

    /**
     * Creates a free-time command that uses the computer's current local time.
     *
     * @param duration the length of free time requested by the user
     */
    public FreeCommand(Duration duration) {
        this(duration, Clock.systemDefaultZone());
    }

    /**
     * Creates a free-time command using a supplied clock.
     *
     * @param duration the length of free time requested by the user
     * @param clock the clock used to establish when the search begins
     */
    FreeCommand(Duration duration, Clock clock) {
        assert duration != null : "free-time duration must not be null";
        assert clock != null : "free-time clock must not be null";
        this.duration = duration;
        this.clock = clock;
    }

    /** Finds and displays the earliest slot without changing or saving the task list. */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws DaddyException {
        LocalDateTime searchStart = roundUpToMinute(LocalDateTime.now(clock));
        LocalDateTime slotStart = tasks.findEarliestFreeTime(duration, searchStart);
        ui.showFreeTime(slotStart, duration);
    }

    /**
     * Rounds a time upward so the displayed minute represents the complete available slot.
     *
     * @param dateTime the time to normalize
     * @return the same time at minute precision, or the next minute when seconds or nanoseconds are present
     */
    private LocalDateTime roundUpToMinute(LocalDateTime dateTime) {
        LocalDateTime truncatedDateTime = dateTime.truncatedTo(ChronoUnit.MINUTES);
        return dateTime.equals(truncatedDateTime) ? dateTime : truncatedDateTime.plusMinutes(1);
    }
}
