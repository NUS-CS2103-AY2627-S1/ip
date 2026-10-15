package carlo.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests for {@link Task}.
 */
class TaskTest {

    @Test
    void newTask_isNotDoneByDefault() {
        Task task = new Task("read book");
        assertFalse(task.isDone());
    }

    @Test
    void getDescription_returnsDescriptionGivenAtConstruction() {
        Task task = new Task("read book");
        assertEquals("read book", task.getDescription());
    }

    @Test
    void markAsDone_setsIsDoneTrue() {
        Task task = new Task("read book");
        task.markAsDone();
        assertTrue(task.isDone());
    }

    @Test
    void markAsNotDone_afterMarkAsDone_setsIsDoneFalse() {
        Task task = new Task("read book");
        task.markAsDone();
        task.markAsNotDone();
        assertFalse(task.isDone());
    }

    @Test
    void markAsDone_calledTwice_remainsDone() {
        Task task = new Task("read book");
        task.markAsDone();
        task.markAsDone();
        assertTrue(task.isDone());
    }

    @Test
    void getStatusIcon_notDone_returnsSpace() {
        Task task = new Task("read book");
        assertEquals(" ", task.getStatusIcon());
    }

    @Test
    void getStatusIcon_done_returnsX() {
        Task task = new Task("read book");
        task.markAsDone();
        assertEquals("X", task.getStatusIcon());
    }

    @Test
    void toString_notDone_showsSpaceStatusIcon() {
        Task task = new Task("read book");
        assertEquals("[ ] read book", task.toString());
    }

    @Test
    void toString_done_showsXStatusIcon() {
        Task task = new Task("read book");
        task.markAsDone();
        assertEquals("[X] read book", task.toString());
    }

    @Test
    void toFileFormat_notDone_usesZeroFlag() {
        Task task = new Task("read book");
        assertEquals("0 | read book", task.toFileFormat());
    }

    @Test
    void toFileFormat_done_usesOneFlag() {
        Task task = new Task("read book");
        task.markAsDone();
        assertEquals("1 | read book", task.toFileFormat());
    }
}
