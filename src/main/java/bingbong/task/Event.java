package bingbong.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Represents a task that happens during a specific start and end date period.
 */
public class Event extends Task {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    protected LocalDate from;
    protected LocalDate to;

    /**
     * Constructs an Event task record parsing two independent range bounds.
     *
     * @param description The summary text details of the item.
     * @param from        The start date parameter details string.
     * @param to          The target termination end parameter details string.
     * @throws DateTimeParseException If either chronological input argument fails conversion fails.
     */
    public Event(String description, String from, String to) throws DateTimeParseException {
        super(description);
        String trimmedFrom = from.trim();
        String trimmedTo = to.trim();

        try {
            this.from = LocalDate.parse(trimmedFrom, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            this.from = LocalDate.parse(trimmedFrom);
        }

        try {
            this.to = LocalDate.parse(trimmedTo, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            this.to = LocalDate.parse(trimmedTo);
        }
    }

    /**
     * Checks if a specific query date falls inside the active duration range of this item instance.
     *
     * @param target The reference query LocalDate parameter to cross-examine.
     * @return True if the target occurs on or inside the event span, false otherwise.
     */
    public boolean isOccuringOn(LocalDate target) {
        return (!target.isBefore(this.from) && !target.isAfter(this.to));
    }

    /**
     * Returns a formatted text summary representation containing event status block tags.
     *
     * @return The formatted terminal view layout string.
     */
    @Override
    public String toString() {
        String formattedFrom = this.from.format(DATE_FORMATTER);
        String formattedTo = this.to.format(DATE_FORMATTER);
        return "[E] [" + getStatusIcon() + "] " + this.description + " (from: " + formattedFrom + " to: " + formattedTo
                + ")";
    }

    /**
     * Translates object parameters into a file save storage record line pattern layout.
     *
     * @return A pipe-delimited database safe data line serialisation row.
     */
    @Override
    public String formatToFile() {
        return "E | " + getStatusBinary() + " | " + this.description + " | " + this.from + " | " + this.to;
    }
}
