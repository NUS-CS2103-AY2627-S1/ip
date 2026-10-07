package mary.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Verifies todo display and persisted completion markers.
 */
class TodoTest {
    /**
     * Tests to string: and storage record; completion changes preserve description.
     */
    @Test
    void toString_andStorageRecord_completionChangesPreserveDescription() {
        Todo todo = new Todo("read book");
        assertEquals("[T][ ] read book", todo.toString());
        assertEquals("T | 0 | read book", todo.toStorageRecord());
        todo.markAsDone();
        assertEquals("[T][X] read book", todo.toString());
        assertEquals("T | 1 | read book", todo.toStorageRecord());
    }
}
