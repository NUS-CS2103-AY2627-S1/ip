package xiaozhi.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests {@link Todo}, and through it the shared behavior every {@link Task}
 * subtype inherits (completion status, status icon, base save format),
 * since {@link Task} itself has no public constructor to test directly.
 */
public class TodoTest {

    @Test
    public void constructor_newTodo_isNotDoneAndKeepsDescription() {
        Todo todo = new Todo("read book");

        assertEquals("read book", todo.getDescription());
        assertFalse(todo.isDone());
    }

    @Test
    public void getStatusIcon_notDone_returnsSpace() {
        Todo todo = new Todo("read book");

        assertEquals(" ", todo.getStatusIcon());
    }

    @Test
    public void markAsDone_thenGetStatusIcon_returnsX() {
        Todo todo = new Todo("read book");

        todo.markAsDone();

        assertTrue(todo.isDone());
        assertEquals("X", todo.getStatusIcon());
    }

    @Test
    public void markAsDone_thenMarkAsNotDone_returnsToNotDone() {
        Todo todo = new Todo("read book");
        todo.markAsDone();

        todo.markAsNotDone();

        assertFalse(todo.isDone());
        assertEquals(" ", todo.getStatusIcon());
    }

    @Test
    public void markAsDone_calledTwice_staysDone() {
        Todo todo = new Todo("read book");

        todo.markAsDone();
        todo.markAsDone();

        assertTrue(todo.isDone());
    }

    @Test
    public void getTypeIcon_returnsT() {
        Todo todo = new Todo("read book");

        assertEquals("T", todo.getTypeIcon());
    }

    @Test
    public void toString_notDone_hasTypeAndStatusIconAndDescription() {
        Todo todo = new Todo("read book");

        assertEquals("[T][ ] read book", todo.toString());
    }

    @Test
    public void toString_done_showsXStatusIcon() {
        Todo todo = new Todo("read book");
        todo.markAsDone();

        assertEquals("[T][X] read book", todo.toString());
    }

    @Test
    public void toSaveFormat_notDone_usesZeroForDoneFlag() {
        Todo todo = new Todo("read book");

        assertEquals("T | 0 | read book", todo.toSaveFormat());
    }

    @Test
    public void toSaveFormat_done_usesOneForDoneFlag() {
        Todo todo = new Todo("read book");
        todo.markAsDone();

        assertEquals("T | 1 | read book", todo.toSaveFormat());
    }

    @Test
    public void equals_sameDescription_isEqualRegardlessOfDoneStatus() {
        Todo first = new Todo("read book");
        Todo second = new Todo("read book");
        second.markAsDone();

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    public void equals_differentDescription_isNotEqual() {
        assertFalse(new Todo("read book").equals(new Todo("write essay")));
    }

    @Test
    public void equals_notATodo_isNotEqual() {
        assertFalse(new Todo("read book").equals("read book"));
    }
}
