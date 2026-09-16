import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/** Tests task state transitions and formatted representations. */
class TaskTest {
    @Test
    void todoTracksCompletionAndDescription() {
        Todo todo = new Todo("read book");
        assertFalse(todo.isDone());
        assertEquals("read book", todo.getDescription());
        todo.markAsDone();
        assertTrue(todo.isDone());
        assertEquals("[T][X] read book", todo.toString());
        todo.markAsNotDone();
        assertEquals("[T][ ] read book", todo.toString());
    }

    @Test
    void deadlineAndEventExposeTheirDetails() {
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 10, 15));
        Event event = new Event("meeting", "Mon 2pm", "4pm");
        assertEquals(LocalDate.of(2019, 10, 15), deadline.getBy());
        assertEquals("[D][ ] return book (by: Oct 15 2019)", deadline.toString());
        assertEquals("Mon 2pm", event.getFrom());
        assertEquals("4pm", event.getTo());
        assertEquals("[E][ ] meeting (from: Mon 2pm to: 4pm)", event.toString());
    }
}
