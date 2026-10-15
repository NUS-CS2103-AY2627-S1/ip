package pulbot.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/** Tests task state changes and user-facing task representations. */
public class TaskTest {
    @Test
    public void constructor_storageDelimiters_throwsException() {
        for (String description : new String[] {"read\tbook", "read\nbook", "read\rbook"}) {
            assertThrows(IllegalArgumentException.class, () -> new Todo(description));
            assertThrows(IllegalArgumentException.class, () ->
                    new Deadline(description, "18/9/2026 1200"));
            assertThrows(IllegalArgumentException.class, () ->
                    new Event(description, "18/9/2026 1200", "18/9/2026 1300"));
        }
    }

    @Test
    public void constructors_missingRequiredDetails_throwAssertionError() {
        assertThrows(AssertionError.class, () -> new Todo(" "));
        assertThrows(AssertionError.class, () ->
                new Deadline("submit report", (LocalDateTime) null));
        assertThrows(AssertionError.class, () ->
                new Event("lecture", null, LocalDateTime.of(2019, 12, 3, 16, 0)));
        assertThrows(AssertionError.class, () ->
                new Event("lecture", LocalDateTime.of(2019, 12, 3, 14, 0), null));
    }

    @Test
    public void markAndUnmark_updatesCompletionStateAndDisplay() {
        Task task = new Task("read book");

        assertFalse(task.isDone());
        assertEquals(" ", task.getStatusIcon());
        assertEquals("[T][ ] read book", task.toString());

        task.markAsDone();
        assertTrue(task.isDone());
        assertEquals("✓", task.getStatusIcon());
        assertEquals("[T][✓] read book", task.toString());

        task.markAsNotDone();
        assertFalse(task.isDone());
        assertEquals(" ", task.getStatusIcon());
    }

    @Test
    public void todo_properties_returnsTodoValues() {
        Todo todo = new Todo("read book");

        assertEquals("read book", todo.getDescription());
        assertEquals(TaskType.TODO, todo.getType());
        assertEquals("[T][ ] read book", todo.toString());
    }

    @Test
    public void deadline_propertiesAndToString_includeDeadline() {
        Deadline deadline = new Deadline("return book", "2/12/2019 1800");

        assertEquals(TaskType.DEADLINE, deadline.getType());
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), deadline.getBy());
        assertEquals("[D][ ] return book (by: Dec 02 2019 6:00 PM)", deadline.toString());
    }

    @Test
    public void event_propertiesAndToString_includeDateRange() {
        Event event = new Event("lecture", "2/12/2019 1400", "2/12/2019 1600");

        assertEquals(TaskType.EVENT, event.getType());
        assertEquals(LocalDateTime.of(2019, 12, 2, 14, 0), event.getFrom());
        assertEquals(LocalDateTime.of(2019, 12, 2, 16, 0), event.getTo());
        assertEquals("[E][ ] lecture (from: Dec 02 2019 2:00 PM to: Dec 02 2019 4:00 PM)",
                event.toString());
    }

    @Test
    public void event_invalidDateRange_throwsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                new Event("lecture", "2/12/2019 1600", "2/12/2019 1400"));

        assertEquals("The event end time must be after its start time.", exception.getMessage());
    }

    @Test
    public void taskType_getSymbol_returnsMatchingSymbols() {
        assertEquals("T", TaskType.TODO.getSymbol());
        assertEquals("D", TaskType.DEADLINE.getSymbol());
        assertEquals("E", TaskType.EVENT.getSymbol());
    }
}
