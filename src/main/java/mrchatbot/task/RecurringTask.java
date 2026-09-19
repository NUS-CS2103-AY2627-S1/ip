package mrchatbot.task;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.Objects;

/**
 * Represents one reusable task whose completion resets on a fixed calendar schedule.
 */
public class RecurringTask extends Task {
    private final Recurrence recurrence;
    private LocalDate nextResetDate;

    /**
     * Creates or restores a task with its next scheduled reset date.
     * Weekly tasks retain the weekday of this date across all subsequent resets.
     */
    public RecurringTask(String description, Recurrence recurrence, LocalDate nextResetDate) {
        super(description);
        this.recurrence = Objects.requireNonNull(recurrence);
        this.nextResetDate = Objects.requireNonNull(nextResetDate);
    }

    public Recurrence getRecurrence() {
        return recurrence;
    }

    public LocalDate getNextResetDate() {
        return nextResetDate;
    }

    /**
     * Resets an expired occurrence and advances past all missed intervals.
     * Returns whether the task changed, including when it was already incomplete.
     */
    public boolean refresh(LocalDate today) {
        if (today.isBefore(nextResetDate)) {
            return false;
        }
        long elapsedDays = ChronoUnit.DAYS.between(nextResetDate, today);
        long intervals = elapsedDays / recurrence.getDays() + 1;
        nextResetDate = nextResetDate.plusDays(intervals * recurrence.getDays());
        markAsNotDone();
        return true;
    }

    @Override
    public String toString() {
        String schedule = "every day";
        if (recurrence == Recurrence.WEEK) {
            schedule = "every week on " + nextResetDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        }
        return "[R][" + getStatusIcon() + "] " + description
                + " (" + schedule + "; next reset: " + nextResetDate + ")";
    }
}
