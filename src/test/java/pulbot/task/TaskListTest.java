package pulbot.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.Test;

/** Tests the ordered task collection operations. */
public class TaskListTest {
    @Test
    public void constructorAndAdd_nullInput_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> new TaskList(null));

        TaskList tasks = new TaskList();
        assertThrows(AssertionError.class, () -> tasks.add(null));
    }

    @Test
    public void collectionOperations_addGetIterateAndRemove_inOrder() {
        Task first = new Todo("first");
        Task second = new Todo("second");
        List<Task> source = new ArrayList<>(List.of(first));
        TaskList tasks = new TaskList(source);
        source.clear();

        assertFalse(tasks.isEmpty());
        assertEquals(1, tasks.size());
        assertSame(first, tasks.get(0));

        tasks.add(second);
        Iterator<Task> iterator = tasks.iterator();
        assertSame(first, iterator.next());
        assertSame(second, iterator.next());
        assertFalse(iterator.hasNext());

        assertSame(first, tasks.remove(0));
        assertSame(second, tasks.remove(0));
        assertTrue(tasks.isEmpty());
    }

    @Test
    public void containsDuplicate_matchingDetails_ignoresCaseWhitespaceAndCompletion() {
        Todo existingTask = new Todo("Read book");
        existingTask.markAsDone();
        TaskList tasks = new TaskList(List.of(existingTask));

        assertTrue(tasks.containsDuplicate(new Todo("  READ BOOK  ")));
    }

    @Test
    public void containsDuplicate_differentTypeOrSchedule_returnsFalse() {
        LocalDateTime dueDate = LocalDateTime.of(2019, 12, 2, 18, 0);
        LocalDateTime eventStart = LocalDateTime.of(2019, 12, 3, 14, 0);
        LocalDateTime eventEnd = LocalDateTime.of(2019, 12, 3, 16, 0);
        TaskList tasks = new TaskList(List.of(
                new Todo("read book"),
                new Deadline("submit report", dueDate),
                new Event("lecture", eventStart, eventEnd)));

        assertFalse(tasks.containsDuplicate(new Deadline("read book", dueDate)));
        assertFalse(tasks.containsDuplicate(new Deadline("submit report", dueDate.plusHours(1))));
        assertFalse(tasks.containsDuplicate(new Event("lecture", eventStart.plusHours(1), eventEnd)));
        assertFalse(tasks.containsDuplicate(new Event("lecture", eventStart, eventEnd.plusHours(1))));
    }

    @Test
    public void containsDuplicate_sameDeadlineOrEventSchedule_returnsTrue() {
        LocalDateTime dueDate = LocalDateTime.of(2019, 12, 2, 18, 0);
        LocalDateTime eventStart = LocalDateTime.of(2019, 12, 3, 14, 0);
        LocalDateTime eventEnd = LocalDateTime.of(2019, 12, 3, 16, 0);
        TaskList tasks = new TaskList(List.of(
                new Deadline("submit report", dueDate),
                new Event("lecture", eventStart, eventEnd)));

        assertTrue(tasks.containsDuplicate(new Deadline("SUBMIT REPORT", dueDate)));
        assertTrue(tasks.containsDuplicate(new Event("LECTURE", eventStart, eventEnd)));
    }
}
