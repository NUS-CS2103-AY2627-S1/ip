package computa;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

/** A task rule that creates one ordinary task every week on a chosen day. */
public class RecurringTask extends Todo {
    private final DayOfWeek day;
    /** Null for the permanent rule; set for a generated weekly instance. */
    private final LocalDate occurrenceDate;
    private LocalDate lastGeneratedDate;

    public RecurringTask(String description, DayOfWeek day) {
        this(description, day, null, null, false);
    }

    public RecurringTask(String description, DayOfWeek day, LocalDate lastGeneratedDate) {
        this(description, day, null, lastGeneratedDate, false);
    }

    /** Creates a generated instance belonging to the weekly recurring rule. */
    public RecurringTask(String description, DayOfWeek day, LocalDate occurrenceDate,
                         boolean isDone) {
        this(description, day, occurrenceDate, null, isDone);
    }

    private RecurringTask(String description, DayOfWeek day, LocalDate occurrenceDate,
                          LocalDate lastGeneratedDate, boolean isDone) {
        super(description, isDone);
        if (day == null) {
            throw new IllegalArgumentException("A recurring task needs a weekday");
        }
        this.day = day;
        this.occurrenceDate = occurrenceDate;
        this.lastGeneratedDate = lastGeneratedDate;
    }

    public DayOfWeek getDay() {
        return day;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getOccurrenceDate() {
        return occurrenceDate;
    }

    public boolean isInstance() {
        return occurrenceDate != null;
    }

    /** Returns true when this rule should create today's task instance. */
    public boolean isDueToday(LocalDate date) {
        LocalDate occurrence = date.with(TemporalAdjusters.previousOrSame(day));
        return lastGeneratedDate == null || occurrence.isAfter(lastGeneratedDate);
    }

    public void markGenerated(LocalDate date) {
        lastGeneratedDate = date;
    }

    @Override
    public String getTaskType() {
        return "[R]";
    }

    @Override
    public String getTaskDescription() {
        if (isInstance()) {
            return getTaskType() + getStatusIcon() + " " + description
                    + " (week of " + occurrenceDate + ")\n";
        }
        return getTaskType() + " " + description + " (every " + day + ")\n";
    }

    @Override
    public String toFileFormat() {
        return "R | " + (isDone ? 1 : 0) + " | " + description + " | " + day + " | "
                + (isInstance() ? occurrenceDate
                : "NONE:" + (lastGeneratedDate == null ? "NONE" : lastGeneratedDate));
    }
}
