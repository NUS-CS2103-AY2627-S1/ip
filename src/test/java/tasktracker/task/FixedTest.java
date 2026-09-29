package tasktracker.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class FixedTest {

    private Fixed task;

    @BeforeEach
    public void setUp() {
        task = new Fixed("gym workout", "1 hour");
    }

    @Test
    public void constructor_initializesCorrectly() {
        assertEquals("gym workout", task.getDescription());
        assertEquals("1 hour", task.getDuration());
        assertEquals("[F][ ] gym workout (needs: 1 hour)", task.toString());
    }

    @Test
    public void markAsDone_updatesStatusAndIcon() {
        task.markAsDone();
        assertTrue(task.toString().contains("[X]"));
        assertEquals("[F][X] gym workout (needs: 1 hour)", task.toString());
        assertEquals("F | 1 | gym workout | 1 hour", task.toFileFormat());
    }

    @Test
    public void markAsUndone_revertsStatusAndIcon() {
        task.markAsDone();
        task.markAsUndone();
        assertTrue(task.toString().contains("[ ]"));
        assertEquals("[F][ ] gym workout (needs: 1 hour)", task.toString());
        assertEquals("F | 0 | gym workout | 1 hour", task.toFileFormat());
    }

    @Test
    public void toString_unmarkedTask_matchesExpectedFormat() {
        assertEquals("[F][ ] gym workout (needs: 1 hour)", task.toString());
    }

    @Test
    public void toFileFormat_unmarkedTask_matchesExpectedFormat() {
        assertEquals("F | 0 | gym workout | 1 hour", task.toFileFormat());
    }
}
