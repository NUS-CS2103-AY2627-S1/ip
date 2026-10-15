package ayre.tasks;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * A subset of Task that hold(s) date(s). Abstract to prevent instantiation as it is not complete as is.
 * Defines how the date should be formatted when printed.
 */
public abstract class TimedTask extends Task {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, uuuu");

    /**
     * Initializes the TimedTask by passing the name on.
     *
     * @param name Name of the TimedTask.
     */
    public TimedTask(String name) {
        super(name);
    }

    /**
     * Converts a LocalDate into a more readable format of MMM dd, uuuu.
     *
     * @param date LocalDate.
     * @return String of formatted date.
     */
    public String formatDate(LocalDate date) {
        return date.format(formatter);
    }
}
