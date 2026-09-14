package morgan.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

/**
 * Represents a task that needs to be completed before specified deadline date.
 */
public class Deadline extends Task {
    private static final DateTimeFormatter INPUT_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH);
    private static final DateTimeFormatter OUTPUT_FORMATTER =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);
    private LocalDate date;

    /**
     * Constructs a Deadline task with specified description and date.
     *
     * @param name    The textual description of the task.
     * @param dateStr The completion date of the task.
     * @throws DateTimeParseException If the completion date is not in the right format.
     */
    public Deadline(String name, String dateStr) throws DateTimeParseException {
        super(name);
        this.date = LocalDate.parse(dateStr.trim(), INPUT_FORMATTER);
    }

    /**
     * Gets the completion date of the task.
     *
     * @return The completion date of the task.
     */
    public LocalDate getDate() {
        return this.date;
    }

    /**
     * Gets the completion date of the task from the storage file.
     *
     * @return The completion date of the task.
     */
    public String getDateForStorage() {
        return this.date.format(INPUT_FORMATTER);
    }

    /**
     * Checks if a task happens on the date.
     *
     * @param date A date entered by the user.
     * @return True if the task is happened on that dates, otherwise false.
     */
    @Override
    public boolean isOnDate(LocalDate date) {
        return this.date.equals(date);
    }

    @Override
    public String toString() {
        return String.format("[D]%s (by: %s)", super.toString(), date.format(OUTPUT_FORMATTER));
    }
}
