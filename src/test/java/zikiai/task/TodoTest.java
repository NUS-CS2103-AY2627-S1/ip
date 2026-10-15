package zikiai.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Tests the display and storage representations of todo tasks.
 */
class TodoTest {

    @Test
    void constructor_blankDescription_assertionErrorThrown() {
        assertThrows(AssertionError.class, () -> new Todo("   "));
    }

    @Test
    void constructor_newTodo_incompleteRepresentationsReturned() {
        Todo todo = new Todo("read book");

        assertEquals("[T][ ] read book", todo.getDescription());
        assertEquals("[T][ ] | read book", todo.toDataString());
    }

    @Test
    void markAsDone_incompleteTodo_doneRepresentationsReturned() {
        Todo todo = new Todo("read book");

        todo.markAsDone();

        assertEquals("[T][X] read book", todo.getDescription());
        assertEquals("[T][X] | read book", todo.toDataString());
    }

    @Test
    void markAsNotDone_completedTodo_incompleteRepresentationsRestored() {
        Todo todo = new Todo("read book");
        todo.markAsDone();

        todo.markAsNotDone();

        assertEquals("[T][ ] read book", todo.getDescription());
        assertEquals("[T][ ] | read book", todo.toDataString());
    }

    @Test
    void setPriority_highPriority_priorityShownInBothRepresentations() {
        Todo todo = new Todo("read book");

        todo.setPriority(Priority.HIGH);

        assertEquals(Priority.HIGH, todo.getPriority());
        assertEquals("[T][HIGH][ ] read book", todo.getDescription());
        assertEquals("[T][ ][H] | read book", todo.toDataString());
    }

    @Test
    void setPriority_nonePriority_priorityRemovedFromRepresentations() {
        Todo todo = new Todo("read book");
        todo.setPriority(Priority.MEDIUM);

        todo.setPriority(Priority.NONE);

        assertEquals(Priority.NONE, todo.getPriority());
        assertEquals("[T][ ] read book", todo.getDescription());
        assertEquals("[T][ ] | read book", todo.toDataString());
    }

    @Test
    void setPriority_nullPriority_assertionErrorThrown() {
        Todo todo = new Todo("read book");

        assertThrows(AssertionError.class, () -> todo.setPriority(null));
    }
}
