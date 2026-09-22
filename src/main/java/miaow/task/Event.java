package miaow.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

/**
 * event
 */
public class Event extends Task {
    private String from;
    private String to;
    private LocalDate fromDate;
    private LocalDate toDate;

    /**
     * event constructor
     * @param name
     */
    public Event(String name) {
        super(name);
        //this.from = "";
        //this.to = "";
    }

    /**
     * checks if range of dates is positive
     */
    private void validateRange() {
        if (fromDate != null && toDate != null
                && !toDate.isAfter(fromDate)) {
            throw new IllegalArgumentException(
                    "Event end must be after event start."
            );
        }
    }

    /**
     * Sets the start date as a date
     * @param date String input for date
     */
    public void from(String date) {
        if (date == null || date.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Event start date cannot be empty."
            );
        }

        String cleanedDate = date.trim();

        try {
            DateTimeFormatter formatter = DateTimeFormatter
                    .ofPattern("uuuu-MM-dd")
                    .withResolverStyle(ResolverStyle.STRICT);

            fromDate = LocalDate.parse(cleanedDate, formatter);
            from = cleanedDate;
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                    "Invalid event start date: " + date
            );
        }

        validateRange();
    }
    /**
     * Sets the due date as a date
     * @param date String input for date
     */
    public void to(String date) {
        if (date == null || date.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Event end date cannot be empty."
            );
        }

        String cleanedDate = date.trim();

        try {
            DateTimeFormatter formatter = DateTimeFormatter
                    .ofPattern("uuuu-MM-dd")
                    .withResolverStyle(ResolverStyle.STRICT);

            toDate = LocalDate.parse(cleanedDate, formatter);
            to = cleanedDate;
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                    "Invalid event end date: " + date
            );
        }

        validateRange();
    }

    /**
     * converts a LocalDate into date
     * @param dateTime LocalDate input for conversion
     */
    public String getFormattedDate(LocalDate dateTime) {
        if (dateTime != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd yyyy");
            return dateTime.format(formatter);
        }
        return "Invalid date";
    }

    /**
     * Returns the start date
     */
    public String getFormattedFrom() {
        return getFormattedDate(fromDate);
    }

    /**
     * Returns the end date
     */
    public String getFormattedTo() {
        return getFormattedDate(toDate);
    }

    public LocalDate getFromDateTime() {
        return this.fromDate;
    }

    /**
     * Returns a string of the task as stored in the file.
     */
    @Override
    public String toFileFormat() {
        String statusIcon = super.isDone() ? "1" : "0";
        return "E | " + statusIcon + " | " + super.getDescription() + " | " + from + " | " + to;
    }

    /**
     * Returns a string of the task as it should be displayed in the list.
     */
    @Override
    public String toString() {
        return "[E]" + "[" + (super.isDone() ? "X" : " ") + "] " + super.getDescription() + " (from: "
                + getFormattedFrom() + " to: " + getFormattedTo() + ")";
    }
}
