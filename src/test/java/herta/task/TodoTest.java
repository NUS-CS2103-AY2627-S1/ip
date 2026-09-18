package herta.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Tests todo-specific serialization and display formatting.
 */
class TodoTest {

    @Test
    void todoFormatting_incompleteAndComplete_useExpectedRepresentations() {
        Todo todo = new Todo("read book");

        assertEquals("T | 0 | read book", todo.toStorageString());
        assertEquals("[T][ ] read book", todo.toString());

        todo.markAsDone();

        assertEquals("T | 1 | read book", todo.toStorageString());
        assertEquals("[T][X] read book", todo.toString());
    }
}
