package kojispawn.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Tests behavior shared by tasks and behavior specific to each task type.
 */
public class TaskTest {
    @Test
    public void todo_statusChanges_updatesDisplayAndStoredData() {
        Todo todo = new Todo("read book");

        assertEquals("[T][ ] read book", todo.toString());
        assertEquals("T | 0 | read book", todo.toDataString());

        todo.markAsDone();
        assertEquals("[T][X] read book", todo.toString());
        assertEquals("T | 1 | read book", todo.toDataString());

        todo.markAsNotDone();
        assertEquals("[T][ ] read book", todo.toString());
    }

    @Test
    public void deadline_dateBehavior_formatsStoresAndMatchesDate() {
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 12, 2));

        assertEquals("[D][ ] return book (by: Dec 2 2019)", deadline.toString());
        assertEquals("D | 0 | return book | 2019-12-02", deadline.toDataString());
        assertTrue(deadline.occursOn(LocalDate.of(2019, 12, 2)));
        assertFalse(deadline.occursOn(LocalDate.of(2019, 12, 3)));
    }

    @Test
    public void event_values_formatsAndStoresValues() {
        Event event = new Event("project meeting", "Monday 2pm", "4pm");

        assertEquals("[E][ ] project meeting (from: Monday 2pm to: 4pm)", event.toString());
        assertEquals("E | 0 | project meeting | Monday 2pm | 4pm", event.toDataString());
        assertFalse(event.occursOn(LocalDate.of(2019, 12, 2)));
    }
}
