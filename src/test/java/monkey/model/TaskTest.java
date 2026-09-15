package monkey.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TaskTest {
    @Test
    void constructor_nullBlankOrDelimitedDescription_rejectsInput() {
        assertThrows(IllegalArgumentException.class, () -> new Task(null));
        assertThrows(IllegalArgumentException.class, () -> new Task("  "));
        assertThrows(IllegalArgumentException.class, () -> new Task("buy | milk"));
    }

    @Test
    void constructor_repeatedWhitespace_normalizesDescription() {
        assertEquals("buy milk", new Task("  buy   milk  ").getDescription());
    }

    @Test
    void markAndUnmark_updatesStatusIconAndDisplay() {
        Task task = new Task("read");

        assertFalse(task.isDone());
        assertEquals(" ", task.getStatusIcon());
        assertEquals("[ ] read", task.toString());

        task.markAsDone();
        assertTrue(task.isDone());
        assertEquals("X", task.getStatusIcon());

        task.markAsNotDone();
        assertFalse(task.isDone());
    }

    @Test
    void hasSameDetails_comparesTypeAndNormalizedDescriptionIgnoringCase() {
        Task task = new Task("Buy Milk");

        assertTrue(task.hasSameDetails(new Task("buy   milk")));
        assertFalse(task.hasSameDetails(new Task("buy bread")));
        assertFalse(task.hasSameDetails(new ToDos("buy milk")));
        assertFalse(task.hasSameDetails(null));
    }
}
