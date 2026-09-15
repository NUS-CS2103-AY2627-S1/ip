package sal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Tests tagging behaviour on {@link Task} and how tags appear in display text.
 */
public class TaskTest {
    @Test
    public void addTag_validName_appendsInOrder() throws SalException {
        Todo todo = new Todo("read book");
        todo.addTag("fun");
        todo.addTag("cs2103");

        assertEquals(2, todo.getTags().size());
        assertEquals("fun", todo.getTags().get(0));
        assertEquals("cs2103", todo.getTags().get(1));
        assertEquals("[T][ ] read book #fun #cs2103", todo.toString());
    }

    @Test
    public void addTag_duplicate_exceptionThrown() throws SalException {
        Todo todo = new Todo("read book");
        todo.addTag("fun");
        SalException exception = assertThrows(SalException.class, () -> todo.addTag("fun"));
        assertEquals("This task already has the tag #fun", exception.getMessage());
        assertEquals(1, todo.getTags().size());
    }

    @Test
    public void addTag_invalidName_exceptionThrown() {
        Todo todo = new Todo("read book");
        assertThrows(SalException.class, () -> todo.addTag(""));
        assertThrows(SalException.class, () -> todo.addTag("hello world"));
        assertThrows(SalException.class, () -> todo.addTag("fun,party"));
        assertTrue(todo.getTags().isEmpty());
    }

    @Test
    public void toString_deadlineAndEvent_appendsTagsAtEnd() throws SalException {
        Deadline deadline = new Deadline("return book", new TaskDateTime(LocalDate.of(2019, 10, 15)));
        deadline.addTag("urgent");
        assertTrue(deadline.toString().endsWith("#urgent"));
        assertTrue(deadline.toString().contains("(by:"));

        Event event = new Event("meeting",
                new TaskDateTime(LocalDate.of(2019, 10, 15)),
                new TaskDateTime(LocalDate.of(2019, 10, 16)));
        event.addTag("work");
        assertTrue(event.toString().endsWith("#work"));
        assertTrue(event.toString().contains("(from:"));
    }
}
