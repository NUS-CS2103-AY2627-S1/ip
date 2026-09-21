package auto.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/** Tests the common state, rendering, and persistence behavior of {@link Task}. */
class TaskTest {
    @Test
    void markAndUnmark_repeatedCalls_setExpectedCompletionState() {
        Task task = new Task("read book");

        task.mark();
        task.mark();
        assertTrue(task.isCompleted());

        task.unmark();
        task.unmark();
        assertFalse(task.isCompleted());
    }

    @Test
    void occursOn_unscheduledTask_returnsFalseForAnyDate() {
        Task task = new Task("read book");

        assertFalse(task.occursOn(LocalDate.of(2026, 8, 22)));
    }

    @Test
    void matches_substringWithDifferentCase_returnsTrue() {
        Task task = new Task("Read Book");

        assertTrue(task.matches("book"));
        assertTrue(task.matches("READ"));
        assertFalse(task.matches("write"));
    }

    @Test
    void toString_incompleteAndCompletedTask_displaysExpectedStatus() {
        Task task = new Task("read book");
        assertEquals("[ ] read book", task.toString());

        task.mark();
        assertEquals("[X] read book", task.toString());
    }

    @Test
    void toDataString_nameContainsStorageSeparator_encodesNameUnambiguously() {
        Task task = new Task("read | book");

        assertEquals("T | 0 | cmVhZCB8IGJvb2s=", task.toDataString());

        task.mark();
        assertEquals("T | 1 | cmVhZCB8IGJvb2s=", task.toDataString());
    }
}
