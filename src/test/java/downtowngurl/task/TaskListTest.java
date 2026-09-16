package downtowngurl.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/**
 * Tests task list operations that contain ordering logic.
 */
public class TaskListTest {
    /**
     * Checks that dated tasks are sorted by date before undated todo tasks.
     */
    @Test
    public void sortByDate_mixedDatedAndUndatedTasks_ordersDatedTasksBeforeTodos() {
        TaskList tasks = new TaskList();
        Todo todo = new Todo("read book");
        Deadline laterDeadline = new Deadline("submit report", LocalDateTime.of(2019, 12, 3, 18, 0));
        laterDeadline.setRecurrenceFrequency(RecurrenceFrequency.WEEKLY);
        Event earlierEvent = new Event("meeting", LocalDateTime.of(2019, 12, 2, 18, 0),
                LocalDateTime.of(2019, 12, 2, 20, 0));
        tasks.add(todo);
        tasks.add(laterDeadline);
        tasks.add(earlierEvent);

        tasks.sortByDate();

        assertEquals(earlierEvent, tasks.get(0));
        assertEquals(laterDeadline, tasks.get(1));
        assertEquals(todo, tasks.get(2));
    }

    @Test
    public void findByKeyword_matchingDescriptions_returnsCaseInsensitiveSubstringMatches() {
        TaskList tasks = new TaskList();
        Todo lowercaseMatch = new Todo("visit the bookstore");
        Todo uppercaseMatch = new Todo("READ BOOK tonight");
        Todo nonMatch = new Todo("write report");
        tasks.add(lowercaseMatch);
        tasks.add(uppercaseMatch);
        tasks.add(nonMatch);

        TaskList matches = tasks.findByKeyword("book");

        assertEquals(2, matches.size());
        assertEquals(lowercaseMatch, matches.get(0));
        assertEquals(uppercaseMatch, matches.get(1));
    }

    /**
     * Checks that clearing recurrence removes the recurring display text.
     */
    @Test
    public void clearRecurrence_recurringDeadline_removesRecurrenceText() {
        Deadline deadline = new Deadline("submit report", LocalDateTime.of(2019, 12, 3, 18, 0));
        deadline.setRecurrenceFrequency(RecurrenceFrequency.WEEKLY);

        deadline.clearRecurrence();

        assertEquals("[D][ ] submit report (by: 03 Dec 2019, Tuesday 18:00)", deadline.toString());
    }
}
