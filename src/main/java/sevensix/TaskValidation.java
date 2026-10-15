package sevensix;

import java.time.LocalTime;
import java.util.Objects;

/**
 * Checks task data shared by command input and persisted records.
 */
public final class TaskValidation {
    /** Prevents construction of this utility class. */
    private TaskValidation() {
    }

    /** Rejects task details that cannot be represented faithfully or chronologically.
     *
     * @param task the task being added or loaded.
     * @throws SevenSixException if the description or event range is invalid.
     */
    public static void validate(Task task) throws SevenSixException {
        validateDescription(task.getDescription());
        if (task instanceof Event event) {
            validateEvent(event);
        }
    }

    /** Rejects blank descriptions and characters that break line-based storage.
     *
     * @param description the task description.
     * @throws SevenSixException if the description is invalid.
     */
    private static void validateDescription(String description) throws SevenSixException {
        if (description.isBlank() || description.codePoints().anyMatch(Character::isISOControl)) {
            throw new SevenSixException(Ui.INVALID_CHARACTERS);
        }
    }

    /** Compares event endpoints, interpreting a date without a time as midnight.
     *
     * @param event the event whose range is checked.
     * @throws SevenSixException if its end is not strictly later than its start.
     */
    private static void validateEvent(Event event) throws SevenSixException {
        LocalTime startTime = event.getFromTime() == null ? LocalTime.MIDNIGHT : event.getFromTime();
        LocalTime endTime = event.getToTime() == null ? LocalTime.MIDNIGHT : event.getToTime();
        if (!event.getTo().atTime(endTime).isAfter(event.getFrom().atTime(startTime))) {
            throw new SevenSixException(Ui.INVALID_EVENT_RANGE);
        }
    }

    /** Compares task identity without considering completion status.
     *
     * @param first the existing task.
     * @param second the task being checked.
     * @return true for matching types, case-sensitive descriptions, and dates and times.
     */
    public static boolean hasSameDetails(Task first, Task second) {
        if (!first.getTaskTypeIcon().equals(second.getTaskTypeIcon())
                || !first.getDescription().equals(second.getDescription())) {
            return false;
        }
        if (first instanceof Deadline firstDeadline && second instanceof Deadline secondDeadline) {
            return firstDeadline.getBy().equals(secondDeadline.getBy())
                    && Objects.equals(firstDeadline.getByTime(), secondDeadline.getByTime());
        }
        if (first instanceof Event firstEvent && second instanceof Event secondEvent) {
            return hasSameSchedule(firstEvent, secondEvent);
        }
        return true;
    }

    /** Compares all four event date and time fields.
     *
     * @param first the first event.
     * @param second the second event.
     * @return true when both events have identical schedules.
     */
    private static boolean hasSameSchedule(Event first, Event second) {
        return first.getFrom().equals(second.getFrom()) && first.getTo().equals(second.getTo())
                && Objects.equals(first.getFromTime(), second.getFromTime())
                && Objects.equals(first.getToTime(), second.getToTime());
    }
}
