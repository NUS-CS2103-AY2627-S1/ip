package suki;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests the text each kind of task shows the user, since that formatting is
 * what the list command depends on.
 */
public class TaskTest {

    @Test
    public void todo_startsNotDone() {
        Todo todo = new Todo("read book");
        assertFalse(todo.isDone());
        assertEquals("[T][ ] read book", todo.toString());
    }

    @Test
    public void markAsDone_thenNotDone_togglesStatusIcon() {
        Todo todo = new Todo("read book");

        todo.markAsDone();
        assertTrue(todo.isDone());
        assertEquals("[T][X] read book", todo.toString());

        todo.markAsNotDone();
        assertFalse(todo.isDone());
        assertEquals("[T][ ] read book", todo.toString());
    }

    @Test
    public void deadline_showsByDate() throws SukiException {
        Deadline deadline = new Deadline("return book", DateTime.parse("2019-10-15"));
        assertEquals("[D][ ] return book (by: Oct 15 2019)", deadline.toString());
    }

    @Test
    public void event_showsFromAndToDates() throws SukiException {
        Event event = new Event("meeting",
                DateTime.parse("2019-10-15 1400"), DateTime.parse("2019-10-15 1600"));
        assertEquals("[E][ ] meeting (from: Oct 15 2019, 2:00PM to: Oct 15 2019, 4:00PM)",
                event.toString());
    }

    @Test
    public void getDescription_returnsDescriptionWithoutDecoration() {
        assertEquals("read book", new Todo("read book").getDescription());
    }
}
