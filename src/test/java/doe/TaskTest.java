package doe;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/** Tests task status changes and the display and storage formats of each task type. */
class TaskTest {

    @Test
    void constructor_nullDescription_assertionErrorThrown() {
        assertThrows(AssertionError.class, () -> new Task(null));
    }

    @Test
    void task_markAndUnmark_statusAndStorageFormatUpdated() {
        Task task = new Task("read book");

        assertEquals(" ", task.getStatusIcon());
        assertEquals("false|read book", task.toFileFormat());
        task.markAsDone();
        assertEquals("x", task.getStatusIcon());
        assertEquals("true|read book", task.toFileFormat());
        task.markAsNotDone();
        assertEquals(" ", task.getStatusIcon());
    }

    @Test
    void taskTypes_toStringAndToFileFormat_correctTypeAndDateFormatting() {
        Todo todo = new Todo("read book");
        Deadline deadline = new Deadline("submit report", "17-07-2004 1800");
        Event event = new Event("team meeting", "18-07-2004 0905");

        deadline.markAsDone();

        assertEquals("[t][ ] read book", todo.toString());
        assertEquals("TODO|false|read book", todo.toFileFormat());
        assertEquals("[d][x] submit report (by: 17 Jul 2004, 18:00)", deadline.toString());
        assertEquals("DEADLINE|true|submit report|17-07-2004 1800", deadline.toFileFormat());
        assertEquals("[e][ ] team meeting (at: 18 Jul 2004, 09:05)", event.toString());
        assertEquals("EVENT|false|team meeting|18-07-2004 0905", event.toFileFormat());
    }
}
