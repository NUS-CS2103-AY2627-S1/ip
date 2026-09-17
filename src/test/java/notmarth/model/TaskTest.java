package notmarth.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Tests generic task state, validation, and task-type display markers. */
class TaskTest {
    /** Verifies that completion operations update the status icon and state. */
    @Test
    void taskCompletion_markAndUnmark_updatesStatus() {
        Task task = new Task("mission");

        assertFalse(task.isDone());
        assertEquals("[ ] mission", task.toString());
        task.markAsDone();
        assertTrue(task.isDone());
        assertEquals("[X] mission", task.toString());
        task.markAsUndone();
        assertFalse(task.isDone());
    }

    /** Verifies that task construction rejects missing descriptions and types. */
    @Test
    void taskCreation_missingDescriptionOrType_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Task(null));
        assertThrows(IllegalArgumentException.class, () -> new Task(" "));
        assertThrows(IllegalArgumentException.class,
                () -> new ExposedTask("mission", null));
    }

    /** Verifies that task-detail comparison includes specialized date values. */
    @Test
    void hasSameDetailsAs_differentSpecializedValues_distinguishesTasks() {
        Deadline first = new Deadline("report", "2019-10-15");
        Deadline second = new Deadline("report", "2019-10-16");
        Event event = new Event("report", "2019-10-15", "2019-10-16");

        assertFalse(first.hasSameDetailsAs(second));
        assertFalse(first.hasSameDetailsAs(event));
        assertTrue(first.hasSameDetailsAs(new Deadline("report", "2019-10-15")));
    }

    private static final class ExposedTask extends Task {
        private ExposedTask(String description, TaskType type) {
            super(description, type);
        }
    }
}
