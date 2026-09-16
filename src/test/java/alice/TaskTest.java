package alice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class TaskTest {

    @Test
    void toDos_constructorAndToString_works() {
        ToDos todo = new ToDos("read book");
        assertEquals("read book", todo.getDescription());
        assertFalse(todo.isDone());
        assertEquals("[T][ ] read book", todo.toString());
    }

    @Test
    void toDos_markAndUnmark_works() {
        ToDos todo = new ToDos("read book");
        todo.toggleStatus();
        assertTrue(todo.isDone());
        assertEquals("[T][X] read book", todo.toString());
        todo.toggleStatus();
        assertFalse(todo.isDone());
        assertEquals("[T][ ] read book", todo.toString());
    }

    @Test
    void deadlines_constructorAndToString_works() {
        LocalDate date = LocalDate.parse("2024-12-25");
        Deadlines deadline = new Deadlines("return book", date);
        assertEquals("return book", deadline.getDescription());
        assertEquals(date, deadline.getBy());
        assertFalse(deadline.isDone());
        assertEquals("[D][ ] return book (by: Dec 25 2024)", deadline.toString());
    }

    @Test
    void deadlines_markAndUnmark_works() {
        Deadlines deadline = new Deadlines("return book", LocalDate.parse("2024-12-25"));
        deadline.toggleStatus();
        assertTrue(deadline.isDone());
        assertEquals("[D][X] return book (by: Dec 25 2024)", deadline.toString());
        deadline.toggleStatus();
        assertFalse(deadline.isDone());
        assertEquals("[D][ ] return book (by: Dec 25 2024)", deadline.toString());
    }

    @Test
    void events_constructorAndToString_works() {
        LocalDate from = LocalDate.parse("2024-12-20");
        LocalDate to = LocalDate.parse("2024-12-22");
        Events event = new Events("conference", from, to);
        assertEquals("conference", event.getDescription());
        assertEquals(from, event.getFrom());
        assertEquals(to, event.getTo());
        assertFalse(event.isDone());
        assertEquals("[E][ ] conference (from: Dec 20 2024 to: Dec 22 2024)", event.toString());
    }

    @Test
    void events_markAndUnmark_works() {
        LocalDate from = LocalDate.parse("2024-12-20");
        LocalDate to = LocalDate.parse("2024-12-22");
        Events event = new Events("conference", from, to);
        event.toggleStatus();
        assertTrue(event.isDone());
        assertEquals("[E][X] conference (from: Dec 20 2024 to: Dec 22 2024)", event.toString());
        event.toggleStatus();
        assertFalse(event.isDone());
        assertEquals("[E][ ] conference (from: Dec 20 2024 to: Dec 22 2024)", event.toString());
    }

    @Test
    void task_getStatusIcon_works() {
        ToDos todo = new ToDos("test");
        assertEquals(" ", todo.getStatusIcon());
        todo.toggleStatus();
        assertEquals("X", todo.getStatusIcon());
    }
}
