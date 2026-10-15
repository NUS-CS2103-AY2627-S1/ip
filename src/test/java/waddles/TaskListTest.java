package waddles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

public class TaskListTest {

    @Test
    public void add_singleTask_sizeIncreasesAndTaskRetrievable() {
        TaskList taskList = new TaskList();
        taskList.add(new ToDo("read book"));

        assertEquals(1, taskList.size());
        assertEquals("read book", taskList.get(0).getDescription());
    }

    @Test
    public void add_multipleTasks_sizeMatchesCount() {
        TaskList taskList = new TaskList();
        taskList.add(new ToDo("read book"));
        taskList.add(new ToDo("return book"));
        taskList.add(new ToDo("join club"));

        assertEquals(3, taskList.size());
    }

    @Test
    public void delete_existingIndex_taskRemovedAndReturned() {
        TaskList taskList = new TaskList();
        taskList.add(new ToDo("read book"));
        taskList.add(new ToDo("return book"));

        Task removed = taskList.delete(0);

        assertEquals("read book", removed.getDescription());
        assertEquals(1, taskList.size());
        assertEquals("return book", taskList.get(0).getDescription());
    }

    @Test
    public void delete_invalidIndex_exceptionThrown() {
        TaskList taskList = new TaskList();
        taskList.add(new ToDo("read book"));

        assertThrows(IndexOutOfBoundsException.class, () -> taskList.delete(5));
    }

    @Test
    public void size_emptyList_returnsZero() {
        TaskList taskList = new TaskList();
        assertEquals(0, taskList.size());
    }

    @Test
    public void findOnDate_deadlineOnThatDate_isIncluded() {
        TaskList taskList = new TaskList();
        taskList.add(new Deadline("submit report", "2024-12-01"));

        assertEquals(1, taskList.findOnDate(LocalDate.of(2024, 12, 1)).size());
        assertTrue(taskList.findOnDate(LocalDate.of(2024, 12, 2)).isEmpty());
    }

    @Test
    public void findOnDate_eventSpanningThatDate_isIncluded() {
        TaskList taskList = new TaskList();
        taskList.add(new Event("conference", "2024-12-01", "2024-12-03"));

        assertEquals(1, taskList.findOnDate(LocalDate.of(2024, 12, 2)).size());
        assertTrue(taskList.findOnDate(LocalDate.of(2024, 12, 4)).isEmpty());
    }

    @Test
    public void findOnDate_todo_neverIncluded() {
        TaskList taskList = new TaskList();
        taskList.add(new ToDo("read book"));

        assertTrue(taskList.findOnDate(LocalDate.now()).isEmpty());
    }
}