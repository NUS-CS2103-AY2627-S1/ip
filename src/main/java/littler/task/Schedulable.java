package littler.task;

import littler.datetime.StringDateTimeConverter.ParsedDateTime;

/**
 * Defines an interface for tasks that can be scheduled or associated with a specific date and time.
 */
public interface Schedulable {
    /**
     * Checks whether this task falls on, is due on, or occurs during the specified date.
     *
     * @param date the target parsed date and optional time to check against
     * @return true if the task occurs on or matches the given date; false otherwise
     */
    boolean isOccurringOn(ParsedDateTime date);

    /**
     * Returns the date/time value used to order this task chronologically relative to other schedulable tasks.
     *
     * @return the parsed date/time representing this task's position in time
     */
    ParsedDateTime getSortDate();
}
