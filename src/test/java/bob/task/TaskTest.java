package bob.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/** Tests validation and shared behavior of the abstract task base class. */
class TaskTest {

    @Test
    void constructor_nullDescription_assertionErrorThrown() {
        AssertionError exception = assertThrows(AssertionError.class,
                () -> new TestTask(null, TaskType.TODO));

        assertEquals("A task must have a description", exception.getMessage());
    }

    @Test
    void constructor_nullType_assertionErrorThrown() {
        AssertionError exception = assertThrows(AssertionError.class,
                () -> new TestTask("read book", null));

        assertEquals("A task must have a type", exception.getMessage());
    }

    @Test
    void markAsDoneAndNotDone_updatesStatusAndIcon() {
        Task task = new TestTask("read book", TaskType.TODO);

        task.markAsDone();
        assertEquals(TaskStatus.DONE, task.getStatus());
        assertEquals("X", task.getStatusIcon());

        task.markAsNotDone();
        assertEquals(TaskStatus.NOT_DONE, task.getStatus());
        assertEquals(" ", task.getStatusIcon());
    }

    private static class TestTask extends Task {
        TestTask(String description, TaskType type) {
            super(description, type);
        }
    }
}
