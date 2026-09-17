package della.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

public class TaskListTest {
    @Test
    public void taskList_addGetDelete_tasksManagedInOrder() {
        TaskList taskList = new TaskList();
        Task task = new Todo("read book");
        taskList.add(task);
        assertEquals(1, taskList.size());
        assertEquals(task, taskList.get(0));
        assertEquals(task, taskList.delete(0));
        assertEquals(0, taskList.size());
    }

    @Test
    public void taskList_addDuplicate_exceptionThrown() {
        TaskList taskList = new TaskList(List.of(new Todo("read book")));
        assertThrows(IllegalArgumentException.class, () -> taskList.add(new Todo("read book")));
    }

    @Test
    public void taskList_markUnmarkTodo_statusChanged() {
        TaskList taskList = new TaskList(List.of(new Todo("read book")));
        taskList.mark(0);
        assertEquals("[T][X] read book", taskList.get(0).toString());
        taskList.unmark(0);
        assertEquals("[T][ ] read book", taskList.get(0).toString());
    }

    @Test
    public void taskList_markUnmarkDeadline_statusChanged() {
        TaskList taskList = new TaskList(List.of(new Deadline("read book", LocalDateTime.of(2030, 9, 9, 21, 30))));
        taskList.mark(0);
        assertEquals("[D][X] read book (by Sept 09 2030 9:30pm)", taskList.get(0).toString());
        taskList.unmark(0);
        assertEquals("[D][ ] read book (by Sept 09 2030 9:30pm)", taskList.get(0).toString());
    }

    @Test
    public void taskList_markUnmarkEvent_statusChanged() {
        TaskList taskList = new TaskList(
                List.of(
                        new Event(
                                "read book",
                                LocalDateTime.of(2030, 9, 9, 21, 30),
                                LocalDateTime.of(2050, 9, 9, 21, 30))));
        taskList.mark(0);
        assertEquals(
                "[E][X] read book (from: Sept 09 2030 9:30pm to: Sept 09 2050 9:30pm)", taskList.get(0).toString());
        taskList.unmark(0);
        assertEquals(
                "[E][ ] read book (from: Sept 09 2030 9:30pm to: Sept 09 2050 9:30pm)", taskList.get(0).toString());
    }

    @Test
    public void taskList_getTasks_returnsIndependentList() {
        TaskList taskList = new TaskList(List.of(new Todo("read book")));
        taskList.getTasks().clear();
        assertEquals(1, taskList.size());
    }

    @Test
    public void taskList_invalidIndex_exceptionThrown() {
        TaskList taskList = new TaskList();
        assertThrows(IndexOutOfBoundsException.class, () -> taskList.get(0));
    }
}
