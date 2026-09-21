package aster.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Tests tagging behaviour shared by all task types.
 */
class TaskTest {
    @Test
    void addTag_validAndDuplicateTag_tagShownOnceInDisplayAndStorage() {
        Task task = new Todo("read book");

        task.addTag("#fun");
        task.addTag("#fun");

        assertEquals("[T][ ] read book #fun", task.toString());
        assertEquals("T | 0 | read book | #fun", task.toStorageString());
    }

    @Test
    void addTag_whitespaceAroundTag_trimmedTagAdded() {
        Task task = new Todo("read book");

        task.addTag("  #school  ");

        assertEquals("[T][ ] read book #school", task.toString());
    }

    @Test
    void addTag_invalidTag_exceptionThrown() {
        Task task = new Todo("read book");

        assertThrows(IllegalArgumentException.class, () -> task.addTag("fun"));
        assertThrows(IllegalArgumentException.class, () -> task.addTag("#"));
        assertThrows(IllegalArgumentException.class, () -> task.addTag("#bad!"));
    }
}
