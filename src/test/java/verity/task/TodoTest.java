package verity.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import verity.exception.VerityException;

/**
 * Tests the observable behavior of todo tasks.
 */
class TodoTest {
    private static final LocalDate ANY_DATE = LocalDate.of(2026, 8, 10);

    @Test
    void getStatus_incompleteTodo_includesTodoTypeAndDescription() {
        Todo todo = new Todo("read book");

        assertEquals("[T][ ] read book", todo.getStatus());
    }

    @Test
    void getStatus_completedTodo_showsCompletedStatus() {
        Todo todo = new Todo("read book");
        todo.markAsDone();

        assertEquals("[T][X] read book", todo.getStatus());
    }

    @Test
    void serialize_incompleteTodo_returnsStorageFormat() {
        Todo todo = new Todo("read book");

        assertEquals("T\t0\tread book\t\t", todo.serialize());
    }

    @Test
    void serialize_completedTodo_returnsCompletedStorageFormat() {
        Todo todo = new Todo("read book");
        todo.markAsDone();

        assertEquals("T\t1\tread book\t\t", todo.serialize());
    }

    @Test
    void occursOn_anyDate_returnsFalse() {
        Todo todo = new Todo("read book");

        assertFalse(todo.occursOn(ANY_DATE));
    }

    @Test
    void constructor_nullDescription_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> new Todo(null));
    }

    @Test
    void clientAssociations_duplicateAndMalformedIds_areHandled()
            throws VerityException {
        Todo todo = new Todo("read book");
        todo.addClientId("C002");
        todo.addClientId("c001");

        assertEquals(List.of("C001", "C002"), todo.getClientIds());
        assertThrows(VerityException.class, () -> todo.addClientId("C001"));
        assertFalse(todo.hasClientId("invalid"));
        assertFalse(todo.removeClientId("C003"));
        assertTrue(todo.removeClientId("c001"));
    }

    @Test
    void addFormerClientNote_blankNote_throwsAssertionError() {
        Todo todo = new Todo("read book");

        assertThrows(AssertionError.class, () -> todo.addFormerClientNote(" "));
    }
}
