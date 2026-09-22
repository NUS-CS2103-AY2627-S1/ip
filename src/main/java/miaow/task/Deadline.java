package miaow.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

/**
 * deadline
 */
public class Deadline extends Task {
    private LocalDate byDate;
    private String by;

    /**
     * deadline constructor
     * @param name
     */
    public Deadline(String name) {
        super(name);
        //this.by = "";
    }


    /**
     * Sets the deadline as a date
     * @param date String input for date
     */
    public void by(String date) {
        if (date == null || date.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Deadline date cannot be empty."
            );
        }

        String cleanedDate = date.trim();

        DateTimeFormatter isoFormatter = DateTimeFormatter
                .ofPattern("uuuu-MM-dd")
                .withResolverStyle(ResolverStyle.STRICT);

        DateTimeFormatter slashFormatter = DateTimeFormatter
                .ofPattern("d/M/uuuu")
                .withResolverStyle(ResolverStyle.STRICT);

        try {
            byDate = LocalDate.parse(cleanedDate, isoFormatter);
        } catch (DateTimeParseException firstError) {
            try {
                byDate = LocalDate.parse(cleanedDate, slashFormatter);
            } catch (DateTimeParseException secondError) {
                throw new IllegalArgumentException(
                        "Invalid deadline date: " + date
                );
            }
        }

        by = cleanedDate;
    }

    public LocalDate getByDate() {
        return byDate;
    }

    /**
     * Returns a string of date formatted as required.
     */
    public String getFormattedDate() {
        if (byDate != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd yyyy");
            return byDate.format(formatter);
        }
        return by;
    }

    /**
     * Returns a string of the task as stored in the file.
     */
    @Override
    public String toFileFormat() {
        String statusIcon = super.isDone() ? "1" : "0";
        return "D | " + statusIcon + " | " + super.getDescription() + " | " + by;
    }

    /**
     * Returns a string of the task as it should be displayed in the list.
     */
    @Override
    public String toString() {
        return "[D]" + "[" + (super.isDone() ? "X" : " ") + "] " + super.getDescription() + " (by: "
                + getFormattedDate() + ")";
    }
}
