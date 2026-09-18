package adrian;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests task description matching.
 */
public class TaskTest {

    /**
     * Verifies that matching ignores differences in letter case.
     */
    @Test
    public void matchesDescription_keywordWithDifferentCase_returnsTrue() {
        Task task = new Task("Read Book", TaskType.TODO);

        assertTrue(task.matchesDescription("book"));
    }

    /**
     * Verifies that an absent keyword does not match a task description.
     */
    @Test
    public void matchesDescription_absentKeyword_returnsFalse() {
        Task task = new Task("Read Book", TaskType.TODO);

        assertFalse(task.matchesDescription("notes"));
    }
}
