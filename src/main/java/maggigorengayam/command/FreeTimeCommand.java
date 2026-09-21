package maggigorengayam.command;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import maggigorengayam.storage.Storage;
import maggigorengayam.tasklist.TaskList;
import maggigorengayam.ui.Ui;
import maggigorengayam.util.FreeTimeFinder;
import maggigorengayam.util.FreeTimeFinder.FreeSlot;

/** The {@code freetime <hours> <windowStartHHmm> <windowEndHHmm>} command: finds the nearest free slot. */
public class FreeTimeCommand extends Command {
    private static final int MAX_DAYS_AHEAD = 30;
    private static final int MINUTES_PER_DAY = 24 * 60;

    private final int durationMinutes;
    private final LocalTime windowStart;
    private final LocalTime windowEnd;

    /** Searches for a free slot of the given length within the given window once {@link #execute} runs. */
    public FreeTimeCommand(int durationMinutes, LocalTime windowStart, LocalTime windowEnd) {
        this.durationMinutes = durationMinutes;
        this.windowStart = windowStart;
        this.windowEnd = windowEnd;
    }

    /**
     * Finds the nearest free slot starting from right now and returns a
     * message reporting it, or reports that none was found within
     * {@value #MAX_DAYS_AHEAD} days.
     */
    @Override
    public String execute(TaskList tasks, Ui ui, Storage storage) {
        LocalDate today = LocalDate.now();
        LocalTime now = roundUpToMinute(LocalTime.now());
        LocalTime searchStartTimeToday = now.isAfter(windowStart) ? now : windowStart;

        Optional<FreeSlot> slot = FreeTimeFinder.findNearestFreeSlot(
                tasks.getAll(), durationMinutes, windowStart, windowEnd,
                today, searchStartTimeToday, MAX_DAYS_AHEAD);

        return slot.isPresent() ? ui.showFreeSlot(slot.get()) : ui.showNoFreeSlot(MAX_DAYS_AHEAD);
    }

    /**
     * Rounds up to the next whole minute (capped at 23:59, never wrapping
     * past midnight into "tomorrow"), so a slot suggested "now" is never
     * already slightly in the past.
     */
    private static LocalTime roundUpToMinute(LocalTime time) {
        if (time.getSecond() == 0 && time.getNano() == 0) {
            return time;
        }
        int minutes = time.getHour() * 60 + time.getMinute() + 1;
        if (minutes >= MINUTES_PER_DAY) {
            return LocalTime.of(23, 59);
        }
        return LocalTime.of(minutes / 60, minutes % 60);
    }
}
