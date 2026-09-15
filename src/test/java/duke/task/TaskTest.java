package duke.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class TaskTest {

    @Test
    public void constructor_nullDescription_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> new Task(null));
    }

    @Test
    public void getStatusIcon_initiallyNotDone_returnsSpace() {
        Task task = new Task("read book");
        assertEquals(" ", task.getStatusIcon());
    }

    @Test
    public void getStatusIcon_markedDone_returnsX() {
        Task task = new Task("read book");
        task.markAsDone();
        assertEquals("X", task.getStatusIcon());
    }

    @Test
    public void markUndone_afterMarkDone_resetsStatus() {
        Task task = new Task("read book");
        task.markAsDone();
        task.markUndone();
        assertEquals(" ", task.getStatusIcon());
    }

    @Test
    public void toDataString_notDone_correctFormat() {
        Task task = new Task("read book");
        assertEquals("T | 0 | read book", task.toDataString());
    }

    @Test
    public void toDataString_done_correctFormat() {
        Task task = new Task("read book");
        task.markAsDone();
        assertEquals("T | 1 | read book", task.toDataString());
    }

    @Test
    public void toString_notDone_correctFormat() {
        Task task = new Task("read book");
        assertEquals("[ ] read book", task.toString());
    }

    @Test
    public void toString_done_correctFormat() {
        Task task = new Task("read book");
        task.markAsDone();
        assertEquals("[X] read book", task.toString());
    }

    @Test
    public void toString_emptyDescription_keepsSpaceAfterBracket() {
        Task task = new Task("");
        assertEquals("[ ] ", task.toString());
    }

    @Test
    public void hasSameDetails_sameTypeAndDescriptionIgnoringCase_returnsTrue() {
        Task first = new Task("Read Book");
        Task second = new Task("read book");
        second.markAsDone();

        assertTrue(first.hasSameDetails(second));
    }

    @Test
    public void hasSameDetails_differentDescription_returnsFalse() {
        assertFalse(new Task("read book").hasSameDetails(new Task("write notes")));
    }

    @Test
    public void hasSameDetails_differentTaskType_returnsFalse() {
        assertFalse(new Task("read book").hasSameDetails(new ToDo("read book")));
    }

    @Test
    public void hasSameDetails_nullTask_returnsFalse() {
        assertFalse(new Task("read book").hasSameDetails(null));
    }
}
