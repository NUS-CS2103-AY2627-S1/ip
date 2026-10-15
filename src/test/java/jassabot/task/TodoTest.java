package jassabot.task;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests Todo state, display, and storage representations.
 */
public class TodoTest {

    @Test
    public void markAsDoneAndUndone_validTransitions_updateCompletionState() {
        Todo todo = new Todo("read book");

        assertFalse(todo.isDone());
        todo.markAsDone();
        assertAll(() ->
                assertTrue(todo.isDone()), () ->
                assertEquals("X", todo.getStatusIcon())
        );
        todo.markAsUndone();
        assertAll(() ->
                assertFalse(todo.isDone()), () ->
                assertEquals(" ", todo.getStatusIcon())
        );
    }

    @Test
    public void toString_completionState_formatsTodoForDisplay() {
        Todo todo = new Todo("read book");

        assertEquals("[T][ ] read book", todo.toString());
        todo.markAsDone();
        assertEquals("[T][X] read book", todo.toString());
    }

    @Test
    public void toDataString_reservedCharacters_escapesDescription() {
        Todo todo = new Todo("compare A | B \\ C");
        todo.markAsDone();

        assertEquals("T | 1 | compare A \\| B \\\\ C", todo.toDataString());
    }
}
