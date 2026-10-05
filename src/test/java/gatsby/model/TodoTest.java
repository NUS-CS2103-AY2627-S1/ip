package gatsby.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** Tests the Todo-specific display and save-file representations. */
class TodoTest {

    /** Verifies the representations of an unfinished todo. */
    @Test
    void unfinishedTodo_formatsWithTodoPrefixAndPendingStatus() {
        Todo todo = new Todo("buy milk");

        assertEquals("[T][ ] buy milk", todo.toString());
        assertEquals("T | 0 | buy milk", todo.toFileFormat());
    }

    /** Verifies that a completed todo keeps its type and records its done status. */
    @Test
    void finishedTodo_formatsWithTodoPrefixAndDoneStatus() {
        Todo todo = new Todo("buy milk");
        todo.markDone();

        assertEquals("[T][X] buy milk", todo.toString());
        assertEquals("T | 1 | buy milk", todo.toFileFormat());
    }
}
