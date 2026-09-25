package shan.task;

import java.time.LocalDate;
import java.time.LocalDateTime;

import shan.datetime.DateTimeParser;

/**
 * Represents a task that must be completed by a deadline.
 */
public class Deadline extends Task {
    private final LocalDateTime endDate;

    /**
     * Constructs a deadline task.
     *
     * @param taskName Name of the task.
     * @param endDate  Date and time by which the task is due.
     */
    public Deadline(String taskName, LocalDateTime endDate) {
        super(taskName);
        assert endDate != null : "Deadline date must not be null";
        this.endDate = endDate;
    }

    /**
     * Returns this Deadline in the save-file format.
     *
     * @return Serialized deadline.
     */
    @Override
    public String toFileString() {
        return String.format("D | %s | %s", getFileFields(),
                DateTimeParser.formatForStorage(this.endDate));
    }

    /**
     * Returns whether this deadline is due within an inclusive date range.
     *
     * @param startDate First date in the range.
     * @param endDate   Last date in the range.
     * @return {@code true} when the deadline is due within the range.
     */
    @Override
    public boolean occursBetween(LocalDate startDate, LocalDate endDate) {
        LocalDate deadlineDate = this.endDate.toLocalDate();
        return !deadlineDate.isBefore(startDate) && !deadlineDate.isAfter(endDate);
    }

    /**
     * Returns the display representation of this deadline.
     *
     * @return Display representation of this deadline.
     */
    @Override
    public String toString() {
        return String.format("[D]%s (by: %s)", super.toString(),
                DateTimeParser.formatForDisplay(this.endDate));
    }
}
