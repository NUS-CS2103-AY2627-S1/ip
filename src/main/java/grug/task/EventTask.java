package grug.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

/**
 * Represents a task that starts at a specific date/time and ends at a specific
 * date/time, e.g. _team project meeting 2/10/2019 2-4pm_
 */
public class EventTask extends Task {
    private LocalDateTime startDatetime;
    private LocalDateTime endDatetime;

    /**
     * Creates a new {@link EventTask} that is incomplete.
     *
     * @param details The details of the task.
     * @param start   The start date/time of the task.
     * @param end     The end date/time of the task.
     * @throws DateTimeParseException   If the start or end text cannot be parsed.
     * @throws IllegalArgumentException If start comes *after* end.
     */
    public EventTask(String details, String start, String end) {
        super(details);
        this.startDatetime = LocalDateTime.parse(start, DATE_TIME_INPUT_FORMATTER);
        this.endDatetime = LocalDateTime.parse(end, DATE_TIME_INPUT_FORMATTER);
        if (this.startDatetime.isAfter(endDatetime)) {
            throw new IllegalArgumentException(
                    "start date/time `%s` comes after end date/time `%s`".formatted(start, end));
        }
    }

    public LocalDateTime getStartDatetime() {
        return this.startDatetime;
    }

    public LocalDateTime getEndDatetime() {
        return this.endDatetime;
    }

    @Override
    public boolean doesOccurOn(LocalDate date) {
        assert !this.startDatetime.isAfter(this.endDatetime);
        // event occurs on that datetime iff start <= datetime <= end
        LocalDate startDate = startDatetime.toLocalDate();
        boolean isTargetOnOrAfterStart = startDate.isBefore(date) || startDate.equals(date);

        LocalDate endDate = endDatetime.toLocalDate();
        boolean isTargetOnOrBeforeEnd = endDate.isAfter(date) || endDate.isEqual(date);

        return isTargetOnOrAfterStart && isTargetOnOrBeforeEnd;
    }

    @Override
    public String toString() {
        return "[E]%s (from: %s | to: %s)".formatted(
                super.toString(),
                startDatetime.format(DATE_TIME_DISPLAY_FORMATTER),
                endDatetime.format(DATE_TIME_DISPLAY_FORMATTER));
    }
}
