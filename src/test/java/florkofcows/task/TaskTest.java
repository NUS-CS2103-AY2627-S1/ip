package florkofcows.task;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class TaskTest {
    @Test
    public void toString_notDone_showsEmptyBracket() {
        Task task = new Task("read book");
        assertEquals("[ ] read book", task.toString());
    }

    @Test
    public void toString_done_showsXBracket() {
        Task task = new Task("read book");
        task.markAsDone();
        assertEquals("[X] read book", task.toString());
    }

    @Test
    public void toSaveFormat_notDone_correctFormat() {
        Task task = new Task("read book");
        assertEquals("0 | read book", task.toSaveFormat());
    }

    @Test
    public void toSaveFormat_done_correctFormat() {
        Task task = new Task("read book");
        task.markAsDone();
        assertEquals("1 | read book", task.toSaveFormat());
    }

    @Test
    public void taskList_addAndGet_validTaskWorks() {
        TaskList taskList = new TaskList();
        Task task = new Task("read book");

        assertDoesNotThrow(() -> taskList.add(task));
        assertEquals(task, taskList.get(0));
    }

    @Test
    public void constructor_blankDescription_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Task("   "));
    }
}
