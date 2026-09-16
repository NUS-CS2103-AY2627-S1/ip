package alice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TaskListTest {

    private TaskList taskList;

    @BeforeEach
    void setUp() {
        // Reset the task list before each test
        taskList = new TaskList();
    }

    @Test
    void addAndSize_worksCorrectly() {
        assertEquals(0, taskList.size());
        assertTrue(taskList.isEmpty());

        taskList.add(new ToDos("read book"));
        assertEquals(1, taskList.size());
        assertFalse(taskList.isEmpty());

        taskList.add(new ToDos("buy milk"));
        assertEquals(2, taskList.size());

        taskList.add(new Deadlines("return book", LocalDate.parse("2024-12-25")));
        assertEquals(3, taskList.size());
    }

    @Test
    void add_rejectsNullTask_whenAssertionsEnabled() {
        assertThrows(AssertionError.class, () -> taskList.add(null));
    }

    @Test
    void get_returnsCorrectTask() {
        ToDos todo = new ToDos("read book");
        taskList.add(todo);
        taskList.add(new ToDos("buy milk"));

        Task retrieved = taskList.get(0);
        assertEquals(todo, retrieved);
        assertEquals("read book", retrieved.getDescription());

        // Test out-of-bounds
        assertThrows(IndexOutOfBoundsException.class, () -> taskList.get(99));
        assertThrows(IndexOutOfBoundsException.class, () -> taskList.get(-1));
    }

    @Test
    void remove_removesTaskAndReturnsIt() {
        ToDos todo1 = new ToDos("read book");
        ToDos todo2 = new ToDos("buy milk");
        taskList.add(todo1);
        taskList.add(todo2);

        assertEquals(2, taskList.size());

        Task removed = taskList.remove(0);
        assertEquals(todo1, removed);
        assertEquals(1, taskList.size());
        assertEquals(todo2, taskList.get(0));

        // Test out-of-bounds
        assertThrows(IndexOutOfBoundsException.class, () -> taskList.remove(99));
    }

    @Test
    void markAndUnmark_updatesStatusCorrectly() {
        taskList.add(new ToDos("read book"));
        taskList.add(new ToDos("buy milk"));

        // Initially both should be undone
        assertFalse(taskList.get(0).isDone());
        assertFalse(taskList.get(1).isDone());

        // Mark first task as done
        taskList.mark(0);
        assertTrue(taskList.get(0).isDone());
        assertFalse(taskList.get(1).isDone()); // Second unchanged

        // Unmark first task
        taskList.unmark(0);
        assertFalse(taskList.get(0).isDone());

        // Test out-of-bounds
        assertThrows(IndexOutOfBoundsException.class, () -> taskList.mark(99));
        assertThrows(IndexOutOfBoundsException.class, () -> taskList.unmark(99));
    }

    @Test
    void isValidIndex_worksCorrectly() {
        taskList.add(new ToDos("read book"));
        taskList.add(new ToDos("buy milk"));

        assertTrue(taskList.isValidIndex(0));
        assertTrue(taskList.isValidIndex(1));
        assertFalse(taskList.isValidIndex(2));
        assertFalse(taskList.isValidIndex(-1));
        assertFalse(taskList.isValidIndex(99));
    }

    @Test
    void getTasks_returnsInternalArrayList() {
        taskList.add(new ToDos("read book"));
        taskList.add(new ToDos("buy milk"));

        ArrayList<Task> tasks = taskList.getTasks();
        assertNotNull(tasks);
        assertEquals(2, tasks.size());
    }

    @Test
    void copy_createsIndependentTaskList() {
        ToDos todo = new ToDos("read book");
        todo.toggleStatus();
        taskList.add(todo);
        taskList.add(new Deadlines("return book", LocalDate.parse("2024-12-25")));
        taskList.add(new Events("conference", LocalDate.parse("2024-12-20"),
                LocalDate.parse("2024-12-22")));

        TaskList copy = taskList.copy();
        copy.get(0).toggleStatus();
        copy.remove(1);

        assertEquals(3, taskList.size());
        assertTrue(taskList.get(0).isDone());
        assertEquals("return book", taskList.get(1).getDescription());
        assertEquals("conference", taskList.get(2).getDescription());
    }
}
