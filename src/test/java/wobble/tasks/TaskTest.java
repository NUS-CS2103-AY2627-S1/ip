package wobble.tasks;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Tests task state changes and common task behavior. */
class TaskTest {
    @Test
    void newTask_isNotDoneAndHasDescription() {
        Task task = new Task("read book");
        assertFalse(task.isDone());
        assertEquals("read book", task.getDescription());
        assertEquals(TaskType.TODO, task.getType());
        assertEquals("[T][ ] read book", task.toString());
    }

    @Test
    void newTask_blankDescription_rejectsInvalidTask() {
        assertThrows(IllegalArgumentException.class, () -> new Task("   "));
    }

    @Test
    void newTask_nullDescription_rejectsInvalidTask() {
        assertThrows(IllegalArgumentException.class, () -> new Task(null));
    }

    @Test
    void newTask_nullType_rejectsInvalidTask() {
        assertThrows(IllegalArgumentException.class, () -> new Task("read book", null));
    }

    @Test
    void markAsDone_changesStatusToDone() {
        Task task = new Task("read book");
        task.markAsDone();
        assertTrue(task.isDone());
        assertEquals("X", task.getStatusIcon());
    }

    @Test
    void markAsNotDone_changesDoneTaskBackToNotDone() {
        Task task = new Task("read book");
        task.markAsDone();
        task.markAsNotDone();
        assertFalse(task.isDone());
        assertEquals(" ", task.getStatusIcon());
    }

    @Test
    void hasSameDetailsAs_sameTaskDetails_returnsTrue() {
        Task task = new Task("read book");

        assertTrue(task.hasSameDetailsAs(new Task("read book")));
    }

    @Test
    void hasSameDetailsAs_differentOrNullTask_returnsFalse() {
        Task task = new Task("read book");

        assertFalse(task.hasSameDetailsAs(new Task("buy bread")));
        assertFalse(task.hasSameDetailsAs(new Deadline("read book", java.time.LocalDateTime.of(2099, 8, 27, 0, 0))));
        assertFalse(task.hasSameDetailsAs(null));
    }
}
