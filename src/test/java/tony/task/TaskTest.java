package tony.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Tests assumptions shared by every task type.
 */
public class TaskTest {
    /** Verifies that a null description is rejected even when assertions are disabled. */
    @Test
    public void constructor_nullDescription_throwsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new Todo(null));

        assertEquals("A task description cannot be null", exception.getMessage());
    }

    /** Verifies that a blank description is rejected even when assertions are disabled. */
    @Test
    public void constructor_blankDescription_throwsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new Todo("  "));

        assertEquals("A task description cannot be blank", exception.getMessage());
    }

    /** Verifies that control characters cannot corrupt the line-based data format. */
    @Test
    public void constructor_descriptionWithControlCharacter_throwsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, () -> new Todo("read\tbook"));

        assertEquals("A task description cannot contain control characters", exception.getMessage());
    }

    /** Verifies that descriptions longer than the storage limit are rejected. */
    @Test
    public void constructor_overlongDescription_throwsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, () -> new Todo("a".repeat(Task.MAX_DESCRIPTION_LENGTH + 1)));

        assertEquals("A task description is too long", exception.getMessage());
    }

    /** Verifies that to-dos compare type and description but ignore case and completion. */
    @Test
    public void hasSameDetails_variedTasks_comparesUserEnteredIdentity() {
        Todo task = new Todo("Read Book");
        Todo sameTask = new Todo("read book");
        sameTask.markAsDone();

        assertTrue(task.hasSameDetails(sameTask));
        assertFalse(task.hasSameDetails(null));
        assertFalse(task.hasSameDetails(new Todo("read another book")));
        assertFalse(task.hasSameDetails(new Deadline("Read Book", LocalDate.of(2026, 9, 20))));
    }

    /** Verifies description searches are case-insensitive. */
    @Test
    public void descriptionContains_variedKeywords_returnsWhetherDescriptionMatches() {
        Todo task = new Todo("Prepare Invoice");

        assertTrue(task.descriptionContains("prepare"));
        assertTrue(task.descriptionContains("INVOICE"));
        assertFalse(task.descriptionContains("report"));
    }

    /** Verifies that a to-do has no associated calendar date. */
    @Test
    public void isOnDate_todoAndAnyDate_returnsFalse() {
        Todo task = new Todo("read book");

        assertFalse(task.isOnDate(LocalDate.of(2026, 9, 20)));
        assertFalse(task.isOnDate(null));
    }
}
