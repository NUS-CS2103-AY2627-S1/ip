package penny.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit test suite for {@link Task}.
 * Verifies common task operations such as status updates, icon representation, and description retrieval.
 */
public class TaskTest {

    private static class ConcreteTask extends Task {
        ConcreteTask(String description) {
            super(description);
        }

        @Override
        public String toFileFormat() {
            return toFileFormatPrefix("X");
        }
    }

    private Task task;

    @BeforeEach
    public void setUp() {
        task = new ConcreteTask("sample task");
    }

    @Test
    public void markAsDone_undoneTask_setsDoneTrue() {
        assertFalse(task.isDone());
        assertEquals(" ", task.getStatusIcon());

        task.markAsDone();
        assertTrue(task.isDone());
        assertEquals("X", task.getStatusIcon());
    }

    @Test
    public void markAsUndone_doneTask_setsDoneFalse() {
        task.markAsDone();
        assertTrue(task.isDone());

        task.markAsUndone();
        assertFalse(task.isDone());
        assertEquals(" ", task.getStatusIcon());
    }

    @Test
    public void getDescription_validTask_returnsCorrectDescription() {
        assertEquals("sample task", task.getDescription());
    }

    @Test
    public void isOnDate_defaultImplementation_returnsFalse() {
        assertFalse(task.isOnDate(LocalDate.now()));
    }

    @Test
    public void toString_doneAndUndone_formatsCorrectly() {
        assertEquals("[ ] sample task", task.toString());
        task.markAsDone();
        assertEquals("[X] sample task", task.toString());
    }
}
