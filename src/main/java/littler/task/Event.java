package littler.task;

import java.util.Map;
import java.util.Objects;

import littler.datetime.StringDateTimeConverter;
import littler.datetime.StringDateTimeConverter.ParsedDateTime;
import littler.exception.LittleRException;

/**
 * Represents a task that occurs within a specific time frame,
 * defined by a start date/time and an end date/time.
 */
public class Event extends Task implements Schedulable {
    public static final String TYPE_CODE = "E";
    public static final String FROM_DELIMITER = "/from";
    public static final String TO_DELIMITER = "/to";
    private final ParsedDateTime startDateTime;
    private final ParsedDateTime endDateTime;

    /**
     * Constructs a new Event task with the specified description, start date/time, and end date/time.
     *
     * @param name the description of the event
     * @param from the starting date and optional time of the event
     * @param to the ending date and optional time of the event
     * @throws LittleRException if the start or end date/time is invalid
     */
    public Event(String name, ParsedDateTime startDateTime, ParsedDateTime endDateTime) throws LittleRException {
        super(name);
        assert startDateTime != null && endDateTime != null
            : "start/end dates are required and should already be parsed by the time they reach here";
        if (startDateTime.compareTo(endDateTime) >= 0) {
            throw new LittleRException("The start date/time must be before the end date/time.");
        }
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
    }

    /**
     * Checks whether this event occurs on or within the specified target date and time range.
     *
     * @param date the target parsed date/time to check against
     * @return true if the event overlaps with or occurs on the given date/time; false otherwise
     */
    @Override
    public boolean isOccurringOn(ParsedDateTime date) {
        boolean isOnOrAfterStartDate = date.compareDate(startDateTime) >= 0;

        if (!date.hasTime()) {
            boolean isBeforeEndDate = date.compareDate(endDateTime) < 0;
            return isOnOrAfterStartDate && isBeforeEndDate;
        }

        boolean isOnOrBeforeEndDate = date.compareDate(endDateTime) <= 0;
        boolean isOnOrAfterStartTime = date.compareTime(startDateTime) >= 0;
        boolean isOnOrBeforeEndTime = date.compareTime(endDateTime) <= 0;
        return isOnOrAfterStartDate && isOnOrBeforeEndDate && isOnOrAfterStartTime && isOnOrBeforeEndTime;
    }

    @Override
    public Task withUpdates(Map<String, String> updates) throws LittleRException {
        if (updates.containsKey(Deadline.INPUT_DELIMITER)) {
            throw new LittleRException(
                "An event only supports " + NAME_DELIMITER + ", " + FROM_DELIMITER + ", and " + TO_DELIMITER + ".");
        }
        String newName = updates.getOrDefault(NAME_DELIMITER, super.getName());
        ParsedDateTime newStart = updates.containsKey(FROM_DELIMITER)
            ? StringDateTimeConverter.parse(updates.get(FROM_DELIMITER))
            : startDateTime;
        ParsedDateTime newEnd = updates.containsKey(TO_DELIMITER)
            ? StringDateTimeConverter.parse(updates.get(TO_DELIMITER))
            : endDateTime;
        Event updated = new Event(newName, newStart, newEnd);
        copyMarkedStatusTo(updated);
        copyPriorityTo(updated);
        copyTagsTo(updated);
        return updated;
    }

    /**
     * Encodes this event as a single-line string for saving to file storage.
     *
     * @return the formatted data string representing this event
     */
    @Override
    public String toFileString() {
        return TYPE_CODE + " | "
            + (super.isMarked() ? "1" : "0") + " | "
            + super.getName() + " | "
            + StringDateTimeConverter.toStorageString(startDateTime) + " | "
            + StringDateTimeConverter.toStorageString(endDateTime)
            + getPriorityStorageSuffix()
            + getTagsStorageSuffix();
    }

    /**
     * Returns the date/time value used to order this task chronologically relative to other schedulable tasks.
     *
     * @return the parsed date/time representing this task's position in time
     */
    @Override
    public ParsedDateTime getSortDate() {
        return startDateTime;
    }

    /**
     * Checks if this event task is equal to another object.
     * Two events are considered equal if they have the same name, start date/time, and end date/time.
     *
     * @param obj the object to compare with
     * @return true if the other object is an Event with the same name and date/time range, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Event other)) {
            return false;
        }
        return super.getName().equalsIgnoreCase(other.getName())
            && startDateTime.equals(other.startDateTime)
            && endDateTime.equals(other.endDateTime);
    }

    /**
     * Computes the hash code for this event task based on its class, name, and date/time range.
     *
     * @return the hash code of this task
     */
    @Override
    public int hashCode() {
        return Objects.hash(Event.class, super.getName().toLowerCase(), startDateTime, endDateTime);
    }

    /**
     * Returns a user-friendly string representation of this event task, including its type icon and duration.
     *
     * @return the formatted string representation of the event
     */
    @Override
    public String toString() {
        return "[E]" + super.toString()
            + " (" + startDateTime + " to " + endDateTime + ")"
            + getPriorityDisplaySuffix()
            + getTagsDisplaySuffix();
    }

}
