package elsa.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Tests {@link Todo}, the kind of task with no date attached.
 *
 * <p>A todo adds only its type marker to what {@link Task} already does, so the
 * tests here are about that marker being in the right place and about the todo
 * not having quietly gained a date.
 */
public class TodoTest {

    @Test
    public void toString_notDone_showsTypeMarkerThenStatus() {
        assertEquals("[T][ ] borrow book", new Todo("borrow book").toString());
    }

    @Test
    public void toString_done_showsCrossInStatusBrackets() {
        Todo todo = new Todo("borrow book");
        todo.markAsDone();
        assertEquals("[T][X] borrow book", todo.toString());
    }

    @Test
    public void toSaveFormat_notDone_writesTypeLetterThenFields() {
        assertEquals("T | 0 | borrow book", new Todo("borrow book").toSaveFormat());
    }

    @Test
    public void toSaveFormat_done_writesTheDoneMarker() {
        Todo todo = new Todo("borrow book");
        todo.markAsDone();
        assertEquals("T | 1 | borrow book", todo.toSaveFormat());
    }

    /** A todo has no date, so it never answers yes to the "on" command. */
    @Test
    public void occursOn_anyDate_returnsFalse() {
        assertFalse(new Todo("borrow book").occursOn(LocalDate.of(2019, 10, 15)));
    }

    /**
     * A saved todo has to be readable again, or a task would be written to the
     * file and then refused the next time the chatbot starts.
     */
    @Test
    public void toSaveFormatThenDecode_done_givesAnEqualTodo() throws Exception {
        Todo todo = new Todo("borrow book");
        todo.markAsDone();
        Task reloaded = TaskFormat.decode(todo.toSaveFormat());
        assertEquals(todo.toString(), reloaded.toString());
    }
}
