package oz.formatter;

import java.time.LocalDate;
import java.util.List;

import oz.exception.OzException;
import oz.task.RecurringEvent;
import oz.task.Task;

/**
 * Formats task-related response text for the Oz user interfaces.
 */
public final class TaskResponseFormatter {
    /** Prevents instantiation of this utility class. */
    private TaskResponseFormatter() {
    }

    /**
     * Formats tasks in their existing order with consecutive display numbers.
     *
     * @param header Heading to place before the numbered tasks.
     * @param tasks Tasks to include in the response.
     * @return Heading and numbered task descriptions without trailing whitespace.
     */
    public static String formatTaskList(String header, List<Task> tasks) {
        StringBuilder response = new StringBuilder(header);
        for (int taskIndex = 0; taskIndex < tasks.size(); taskIndex++) {
            response.append(taskIndex + 1)
                    .append(". ")
                    .append(tasks.get(taskIndex))
                    .append("\n");
        }
        return response.toString().stripTrailing();
    }

    /**
     * Formats tasks for a date query, expanding recurring series to an occurrence.
     *
     * @param header Heading to place before the numbered tasks.
     * @param tasks Tasks occurring on the target date.
     * @param targetDate Date whose occurrences should be displayed.
     * @return Heading and numbered task descriptions without trailing whitespace.
     * @throws OzException If a recurring task cannot produce the expected occurrence.
     */
    public static String formatTasksOnDate(String header, List<Task> tasks,
            LocalDate targetDate) throws OzException {
        StringBuilder response = new StringBuilder(header);
        for (int taskIndex = 0; taskIndex < tasks.size(); taskIndex++) {
            Task task = tasks.get(taskIndex);
            String taskDescription = task instanceof RecurringEvent recurringEvent
                    ? recurringEvent.toOccurrenceString(targetDate)
                    : task.toString();
            response.append(taskIndex + 1)
                    .append(". ")
                    .append(taskDescription)
                    .append("\n");
        }
        return response.toString().stripTrailing();
    }

    /**
     * Formats confirmation that a task was added.
     *
     * @param task Added task.
     * @param taskCount Task count after the addition.
     * @return Add confirmation without trailing whitespace.
     */
    public static String formatAddedTask(Task task, int taskCount) {
        return String.format(
                """
                        *Snort* Added to the list:
                        %s
                        Now you have %d tasks in the list.
                        """,
                task, taskCount).stripTrailing();
    }

    /**
     * Formats confirmation that a task was deleted.
     *
     * @param task Deleted task.
     * @param taskCount Task count after the deletion.
     * @return Delete confirmation without trailing whitespace.
     */
    public static String formatDeletedTask(Task task, int taskCount) {
        return String.format(
                """
                        Scrapped! Removed task:
                        %s
                        Now you have %d tasks in the list.
                        """,
                task, taskCount).stripTrailing();
    }

    /**
     * Formats confirmation that a task or recurring occurrence was completed.
     *
     * @param task Updated task.
     * @param occurrenceDate Updated occurrence date, or null for an ordinary task.
     * @return Mark confirmation.
     * @throws OzException If the recurring occurrence cannot be displayed.
     */
    public static String formatMarkedTask(Task task, LocalDate occurrenceDate)
            throws OzException {
        if (occurrenceDate == null) {
            return "*Oink* Marked as done:\n  " + task;
        }
        return "*Oink* Marked occurrence as done:\n  "
                + formatOccurrence(task, occurrenceDate);
    }

    /**
     * Formats confirmation that a task or recurring occurrence was made incomplete.
     *
     * @param task Updated task.
     * @param occurrenceDate Updated occurrence date, or null for an ordinary task.
     * @return Unmark confirmation.
     * @throws OzException If the recurring occurrence cannot be displayed.
     */
    public static String formatUnmarkedTask(Task task, LocalDate occurrenceDate)
            throws OzException {
        if (occurrenceDate == null) {
            return "*Snort* Marked as not done yet:\n  " + task;
        }
        return "*Snort* Marked occurrence as not done yet:\n  "
                + formatOccurrence(task, occurrenceDate);
    }

    /**
     * Formats one occurrence after verifying the task type expected by the service.
     *
     * @param task Recurring task to format.
     * @param occurrenceDate Occurrence date to display.
     * @return Recurring occurrence description.
     * @throws OzException If the task has no occurrence on the supplied date.
     */
    private static String formatOccurrence(Task task, LocalDate occurrenceDate)
            throws OzException {
        assert task instanceof RecurringEvent
                : "A task updated for an occurrence must be recurring";
        RecurringEvent recurringEvent = (RecurringEvent) task;
        return recurringEvent.toOccurrenceString(occurrenceDate);
    }
}
