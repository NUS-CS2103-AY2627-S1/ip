package lion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests behavior shared by every task type, via the simplest concrete subclass, Todo.
 */
public class TaskTest {

    @Test
    void constructor_nullDescription_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> new Todo(null));
    }

    @Test
    void getStatusIcon_newTask_isZero() {
        assertEquals("0", new Todo("read book").getStatusIcon());
    }

    @Test
    void getStatusIcon_afterMarkAsDone_isOne() {
        Task task = new Todo("read book");

        task.markAsDone();

        assertEquals("1", task.getStatusIcon());
    }

    @Test
    void markAsNotDone_afterMarkAsDone_revertsStatus() {
        Task task = new Todo("read book");
        task.markAsDone();

        task.markAsNotDone();

        assertEquals("0", task.getStatusIcon());
    }

    @Test
    void getDescription_returnsConstructorValue() {
        assertEquals("read book", new Todo("read book").getDescription());
    }

    @Test
    void toString_notDone_showsEmptyCheckbox() {
        assertEquals("[T] [ ] read book", new Todo("read book").toString());
    }

    @Test
    void toString_done_showsCheckedBox() {
        Task task = new Todo("read book");
        task.markAsDone();

        assertEquals("[T] [X] read book", task.toString());
    }

    @Test
    void hasSameDetails_nullOther_returnsFalse() {
        assertFalse(new Todo("read book").hasSameDetails(null));
    }

    @Test
    void hasSameDetails_sameTypeAndDescription_returnsTrue() {
        assertTrue(new Todo("read book").hasSameDetails(new Todo("read book")));
    }

    @Test
    void hasSameDetails_differentDescription_returnsFalse() {
        assertFalse(new Todo("read book").hasSameDetails(new Todo("write notes")));
    }

    @Test
    void hasSameDetails_differentTaskType_returnsFalse() {
        assertFalse(new Todo("read book").hasSameDetails(new Deadline("read book", "2/12/2026 1800")));
    }
}
