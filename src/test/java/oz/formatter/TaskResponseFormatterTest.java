package oz.formatter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import oz.exception.OzException;
import oz.task.RecurringEvent;
import oz.task.TaskDateTime;
import oz.task.ToDo;

/**
 * Tests task response formatting independently of command execution.
 */
public class TaskResponseFormatterTest {
    /** Verifies task lists are numbered without trailing whitespace. */
    @Test
    public void formatTaskList_multipleTasks_returnsNumberedList() {
        String response = TaskResponseFormatter.formatTaskList("Tasks:\n",
                List.of(new ToDo("read book"), new ToDo("write report")));

        assertEquals("Tasks:\n1. [T][ ] read book\n2. [T][ ] write report", response);
    }

    /** Verifies recurring series use their occurrence view in date results. */
    @Test
    public void formatTasksOnDate_recurringTask_returnsOccurrenceView() throws OzException {
        LocalDate occurrenceDate = LocalDate.of(2026, 10, 9);
        RecurringEvent recurringEvent = new RecurringEvent("project meeting",
                TaskDateTime.parse("2026-10-02 1400"),
                TaskDateTime.parse("2026-10-02 1500"), 1, null);
        recurringEvent.markOccurrence(occurrenceDate);

        String response = TaskResponseFormatter.formatTasksOnDate(
                "Tasks on Oct 09 2026:\n", List.of(recurringEvent), occurrenceDate);

        assertEquals("Tasks on Oct 09 2026:\n"
                + "1. [R][X] project meeting "
                + "(from: Oct 09 2026, 2pm to: Oct 09 2026, 3pm)", response);
    }

    /** Verifies mutation confirmations retain their task count and status text. */
    @Test
    public void formatMutationResponses_ordinaryTask_returnsExpectedMessages()
            throws OzException {
        ToDo task = new ToDo("read book");

        assertEquals("*Snort* Added to the list:\n[T][ ] read book\n"
                + "Now you have 1 tasks in the list.",
                TaskResponseFormatter.formatAddedTask(task, 1));
        task.markAsDone();
        assertEquals("*Oink* Marked as done:\n  [T][X] read book",
                TaskResponseFormatter.formatMarkedTask(task, null));
        task.markAsNotDone();
        assertEquals("*Snort* Marked as not done yet:\n  [T][ ] read book",
                TaskResponseFormatter.formatUnmarkedTask(task, null));
        assertEquals("Scrapped! Removed task:\n[T][ ] read book\n"
                + "Now you have 0 tasks in the list.",
                TaskResponseFormatter.formatDeletedTask(task, 0));
    }
}
