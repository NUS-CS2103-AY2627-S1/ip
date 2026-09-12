package minweeder.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

public class TodoTest {

    @Test
    public void toFileString_incompleteTask_returnsCorrectFormat() {
        Todo todo = new Todo("read book");

        assertEquals("T | 0 | read book", todo.toFileString());
    }

    @Test
    public void toFileString_completedTask_returnsCorrectFormat() {
        Todo todo = new Todo("read book");
        todo.mark();

        assertEquals("T | 1 | read book", todo.toFileString());
    }

    @Test
    public void toString_incompleteTask_returnsDisplayFormat() {
        Todo todo = new Todo("read book");

        assertEquals("[T][ ] read book", todo.toString());
    }

    @Test
    public void equals_sameDescription_returnsTrue() {
        assertTrue(new Todo("read book").equals(new Todo("read book")));
    }

    @Test
    public void equals_differentDescription_returnsFalse() {
        assertFalse(new Todo("read book").equals(new Todo("return book")));
    }

    @Test
    public void equals_differentTaskType_returnsFalse() {
        assertNotEquals(new Todo("read book"), new Deadline("read book", LocalDateTime.of(2024, 1, 1, 0, 0)));
    }

    @Test
    public void hashCode_sameDescription_matches() {
        assertEquals(new Todo("read book").hashCode(), new Todo("read book").hashCode());
    }
}
