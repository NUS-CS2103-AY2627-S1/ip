package yuno.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class TodoTest {
    @Test
    void isRelevantFor_anyDate_returnsTrue() {
        Todo todo = new Todo("read book", false);

        assertTrue(todo.isRelevantFor(LocalDate.of(2026, 8, 26)));
    }

    @Test
    void toStorageString_descriptionContainsDelimiter_preservesDescription() {
        Todo todo = new Todo("compare A | B", true);

        assertEquals("T | X | compare A | B", todo.toStorageString());
    }

    @Test
    void toString_incompleteTodo_returnsDisplayFormat() {
        Todo todo = new Todo("read book", false);

        assertEquals("[T][ ] read book", todo.toString());
    }
}
