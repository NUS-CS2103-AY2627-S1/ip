package task;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/** Tests todo formatting, status, and lack of date information. */
public class TodoTest {
    /** Verifies that a todo can be constructed with a valid description. */
    @Test
    public void constructor_validString_todoCreated() {
        assertDoesNotThrow(() -> new Todo("wash the dishes"));
    }

    /** Verifies todo display and storage formats as its completion state changes. */
    @Test
    public void markAndUnmark_statusAndFormatsUpdated() {
        Todo todo = new Todo("wash the dishes");

        assertEquals("[T][ ] wash the dishes", todo.toString());
        assertEquals("TODO | 0 | wash the dishes", todo.toFileFormat());
        todo.markAsDone();
        assertEquals("[T][X] wash the dishes", todo.toString());
        assertEquals("TODO | 1 | wash the dishes", todo.toFileFormat());
        todo.markAsNotDone();
        assertFalse(todo.isDone());
    }

    /** Verifies that an undated todo never occurs on a particular date. */
    @Test
    public void occursOn_anyDate_falseReturned() {
        Todo todo = new Todo("wash the dishes");

        assertFalse(todo.occursOn(LocalDate.of(2026, 8, 26)));
    }

    /** Verifies that an undated todo has no chronological sort time. */
    @Test
    public void getChronologicalDateTime_todo_emptyValueReturned() {
        Todo todo = new Todo("wash the dishes");

        assertTrue(todo.getChronologicalDateTime().isEmpty());
    }

    /** Verifies task accessors and direct status restoration. */
    @Test
    public void accessorsAndRestoreCompletionStatus_variedStates_valuesReturned() {
        Todo todo = new Todo("wash the dishes");

        assertEquals("wash the dishes", todo.getDescription());
        assertEquals(" ", todo.getStatusIcon());
        todo.restoreCompletionStatus(true);
        assertTrue(todo.isDone());
        assertEquals("X", todo.getStatusIcon());
        todo.restoreCompletionStatus(false);
        assertFalse(todo.isDone());
    }

    /** Verifies duplicate identity ignores case and status but respects type and null. */
    @Test
    public void hasSameDetails_variedTasks_correctBooleanReturned() {
        Todo todo = new Todo("Read Book");
        Todo completedDuplicate = new Todo("read book");
        completedDuplicate.markAsDone();

        assertTrue(todo.hasSameDetails(completedDuplicate));
        assertFalse(todo.hasSameDetails(new Todo("write report")));
        assertFalse(todo.hasSameDetails(new Deadline("Read Book",
                LocalDateTime.of(2026, 12, 2, 18, 0))));
        assertFalse(todo.hasSameDetails(null));
    }
}
