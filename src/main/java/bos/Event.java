package bos;

/**
 * Represents a task that takes place between two times.
 */
public class Event extends Task {
    static final String STORAGE_TYPE = "E";

    private final String startTime;
    private final String endTime;

    /**
     * Creates an event with the given description and time range.
     *
     * @param description event description.
     * @param startTime event start date-time text.
     * @param endTime event end date-time text.
     */
    public Event(String description, String startTime, String endTime) {
        super(description);
        this.startTime = startTime;
        this.endTime = endTime;
    }

    /**
     * Returns a readable representation of this event and its time range.
     *
     * @return formatted event for display.
     */
    @Override
    public String toString() {
        return "[E]" + super.toString()
                + " (from: " + Parser.formatDateTimeForDisplay(this.startTime)
                + " to: " + Parser.formatDateTimeForDisplay(this.endTime) + ")";
    }

    /**
     * Converts this event into the text-file storage format.
     *
     * @return storage representation of this event.
     */
    @Override
    public String formatForStorage() {
        return formatStorageFields(
                STORAGE_TYPE,
                startTime,
                endTime);
    }
}
