package ayre.tasks;

import java.time.LocalDate;

/**
 * A TimedTask that holds two dates.
 */
public class Event extends TimedTask {
    private final LocalDate startDate;
    private final LocalDate endDate;

    /**
     * Initializes Event by parsing the user inputs as LocalDates.
     *
     * @param name Name of the Event.
     * @param startDate ISO date starting from.
     * @param endDate ISO date ending at.
     */
    public Event(String name, String startDate, String endDate) {
        super(name);
        this.startDate = LocalDate.parse(startDate);
        this.endDate = LocalDate.parse(endDate);
    }

    /**
     * Converts the Task into a String that is easily parsable to store into the save file.
     *
     * @return String containing all necessary information about the Task to reconstruct it later.
     */
    @Override
    public String toLogString() {
        return "E " + super.toLogString() + " " + startDate + " " + endDate;
    }

    @Override
    public String toString() {
        return "[E]" + super.toString() + " <from: "
                + this.formatDate(startDate) + " to: "
                + this.formatDate(endDate) + ">";
    }
}
