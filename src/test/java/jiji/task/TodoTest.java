package jiji.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class TodoTest {

    @Test
    public void constructor_nullOrBlankDescription_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> new Todo(null));
        assertThrows(AssertionError.class, () -> new Todo("   "));
    }

    @Test
    public void toString_unmarkedTask_formattedCorrectly() {
        Todo todo = new Todo("read book");
        assertEquals("[T][ ] read book", todo.toString());
    }

    @Test
    public void markAsDone_markedTask_formattedCorrectly() {
        Todo todo = new Todo("read book");
        todo.markAsDone();
        assertEquals("[T][X] read book", todo.toString());
    }

    @Test
    public void markAsNotDone_unmarkedTask_formattedCorrectly() {
        Todo todo = new Todo("read book");
        todo.markAsDone();
        todo.markAsNotDone();
        assertEquals("[T][ ] read book", todo.toString());
    }

    @Test
    public void toFileFormat_unmarkedAndMarked_correctStorageString() {
        Todo todo = new Todo("read book");
        assertEquals("T | 0 | read book", todo.toFileFormat());
        todo.markAsDone();
        assertEquals("T | 1 | read book", todo.toFileFormat());
    }
}
