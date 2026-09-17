package yachiyo.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Tests behavior shared by all {@link Task} types.
 */
public class TaskTest {
    private static final String DESCRIPTION = "Read book";

    private final Task task = new TestTask(DESCRIPTION);

    @Test
    public void constructor_descriptionProvided_initialStateSet() {
        assertEquals(DESCRIPTION, task.getDescription());
        assertFalse(task.isCompleted());
        assertEquals(" ", task.getStatusIcon());
    }

    @Test
    public void constructor_nullDescription_assertionErrorThrown() {
        assertThrows(AssertionError.class, () -> new TestTask(null));
    }

    @Test
    public void constructor_blankDescription_assertionErrorThrown() {
        assertThrows(AssertionError.class, () -> new TestTask("   "));
    }

    @Test
    public void constructor_descriptionContainsPipe_assertionErrorThrown() {
        assertThrows(AssertionError.class, () -> new TestTask("Compare option A | option B"));
    }

    @Test
    public void markAsDone_incompleteTask_taskCompleted() {
        task.markAsDone();

        assertTrue(task.isCompleted());
        assertEquals("X", task.getStatusIcon());
    }

    @Test
    public void markAsNotDone_completedTask_taskMarkedIncomplete() {
        task.markAsDone();

        task.markAsNotDone();

        assertFalse(task.isCompleted());
        assertEquals(" ", task.getStatusIcon());
    }

    @Test
    public void toFileFormat_incompleteTask_correctFormatReturned() {
        assertEquals("0 | Read book", task.toFileFormat());
    }

    @Test
    public void toFileFormat_completedTask_correctFormatReturned() {
        task.markAsDone();

        assertEquals("1 | Read book", task.toFileFormat());
    }

    @Test
    public void occursOn_anyDate_falseReturned() {
        assertFalse(task.occursOn(LocalDate.of(2026, 8, 27)));
    }

    @Test
    public void toString_incompleteTask_correctDisplayReturned() {
        assertEquals("[ ] Read book", task.toString());
    }

    @Test
    public void toString_completedTask_correctDisplayReturned() {
        task.markAsDone();

        assertEquals("[X] Read book", task.toString());
    }

    /**
     * Concrete task used to exercise the base {@link Task} implementation.
     */
    private static final class TestTask extends Task {
        private TestTask(String description) {
            super(description);
        }

        @Override
        public Task withDescription(String description) {
            return copyCompletionStatusTo(new TestTask(description));
        }
    }
}
