package oz.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import oz.exception.OzException;

/**
 * Tests task management operations of {@link TaskList}.
 */
public class TaskListTest {

    @Test
    public void add_validTask_sizeIncreases() throws OzException {
        TaskList taskList = new TaskList();
        assertEquals(0, taskList.size());

        taskList.add(new ToDo("read book"));
        assertEquals(1, taskList.size());
        assertEquals("[T][ ] read book", taskList.get(0).toString());
    }

    @Test
    public void delete_validIndex_taskRemoved() throws OzException {
        TaskList taskList = new TaskList();
        taskList.add(new ToDo("task 1"));
        taskList.add(new ToDo("task 2"));

        Task removed = taskList.delete(0);
        assertEquals("[T][ ] task 1", removed.toString());
        assertEquals(1, taskList.size());
        assertEquals("[T][ ] task 2", taskList.get(0).toString());
    }

    @Test
    public void delete_outOfBoundsIndex_exceptionThrown() {
        TaskList taskList = new TaskList();
        taskList.add(new ToDo("only task"));

        OzException exception = assertThrows(OzException.class, () -> taskList.delete(5));
        assertEquals("That task number does not exist.", exception.getMessage());

        OzException negativeException = assertThrows(OzException.class, () -> taskList.delete(-1));
        assertEquals("That task number does not exist.", negativeException.getMessage());
    }

    @Test
    public void get_validAndInvalidIndex_correctBehavior() throws OzException {
        TaskList taskList = new TaskList();
        taskList.add(new ToDo("first"));

        assertEquals("[T][ ] first", taskList.get(0).toString());

        OzException exception = assertThrows(OzException.class, () -> taskList.get(1));
        assertEquals("That task number does not exist.", exception.getMessage());
    }

    @Test
    public void markAndUnmark_validIndex_statusUpdated() throws OzException {
        TaskList taskList = new TaskList();
        taskList.add(new ToDo("homework"));

        taskList.markAsDone(0);
        assertEquals("[T][X] homework", taskList.get(0).toString());

        taskList.markAsNotDone(0);
        assertEquals("[T][ ] homework", taskList.get(0).toString());
    }

    @Test
    public void markAndUnmark_outOfBoundsIndex_exceptionThrown() {
        TaskList taskList = new TaskList();

        OzException markException = assertThrows(OzException.class, () -> taskList.markAsDone(0));
        assertEquals("That task number does not exist.", markException.getMessage());

        OzException unmarkException = assertThrows(OzException.class, () -> taskList.markAsNotDone(-1));
        assertEquals("That task number does not exist.", unmarkException.getMessage());
    }

    @Test
    public void findTasksOn_matchingDate_returnsFilteredTasks() throws OzException {
        TaskList taskList = new TaskList();
        TaskDateTime targetDateTime = TaskDateTime.parse("2026-09-01 1800");
        TaskDateTime otherDateTime = TaskDateTime.parse("2026-09-05 1800");

        taskList.add(new Deadline("submit report", targetDateTime));
        taskList.add(new Deadline("pay bill", otherDateTime));
        taskList.add(new ToDo("read book"));

        List<Task> matchingTasks = taskList.findTasksOn(LocalDate.of(2026, 9, 1));
        assertEquals(1, matchingTasks.size());
        assertEquals("submit report", matchingTasks.get(0).description);

        List<Task> noMatches = taskList.findTasksOn(LocalDate.of(2026, 12, 31));
        assertEquals(0, noMatches.size());
    }

    @Test
    public void findTasksByKeyword_matchingKeyword_returnsMatchingTasks() throws OzException {
        TaskList taskList = new TaskList();
        taskList.add(new ToDo("read book"));
        taskList.add(new Deadline("return book", TaskDateTime.parse("2026-09-01 1800")));
        taskList.add(new ToDo("buy milk"));

        List<Task> matchingTasks = taskList.findTasksByKeyword("book");
        assertEquals(2, matchingTasks.size());
        assertEquals("[T][ ] read book", matchingTasks.get(0).toString());
        assertEquals("[D][ ] return book (by: Sep 01 2026, 6pm)", matchingTasks.get(1).toString());
    }

    @Test
    public void findTasksByKeyword_caseInsensitiveKeyword_returnsMatchingTasks() throws OzException {
        TaskList taskList = new TaskList();
        taskList.add(new ToDo("Read Novel"));
        taskList.add(new ToDo("write code"));

        List<Task> matchingTasks = taskList.findTasksByKeyword("READ");
        assertEquals(1, matchingTasks.size());
        assertEquals("[T][ ] Read Novel", matchingTasks.get(0).toString());
    }

    @Test
    public void findTasksByKeyword_nonMatchingKeyword_returnsEmptyList() {
        TaskList taskList = new TaskList();
        taskList.add(new ToDo("clean room"));

        List<Task> matchingTasks = taskList.findTasksByKeyword("exercise");
        assertEquals(0, matchingTasks.size());
    }

    @Test
    public void add_withIndex_insertsTaskAtSpecifiedIndex() throws OzException {
        TaskList taskList = new TaskList();
        taskList.add(new ToDo("task 1"));
        taskList.add(new ToDo("task 3"));

        taskList.add(1, new ToDo("task 2"));
        assertEquals(3, taskList.size());
        assertEquals("[T][ ] task 1", taskList.get(0).toString());
        assertEquals("[T][ ] task 2", taskList.get(1).toString());
        assertEquals("[T][ ] task 3", taskList.get(2).toString());

        taskList.add(0, new ToDo("task 0"));
        assertEquals(4, taskList.size());
        assertEquals("[T][ ] task 0", taskList.get(0).toString());

        taskList.add(4, new ToDo("task 4"));
        assertEquals(5, taskList.size());
        assertEquals("[T][ ] task 4", taskList.get(4).toString());
    }

    /** Verifies later changes to a constructor argument do not alter the task list. */
    @Test
    public void constructor_mutableSource_copiesTasks() throws OzException {
        ArrayList<Task> sourceTasks = new ArrayList<>();
        sourceTasks.add(new ToDo("original task"));
        TaskList taskList = new TaskList(sourceTasks);

        sourceTasks.add(new ToDo("external task"));

        assertEquals(1, taskList.size());
        assertEquals("[T][ ] original task", taskList.get(0).toString());
    }

    /** Verifies callers cannot mutate the task collection returned for reading. */
    @Test
    public void getTasks_returnedSnapshot_cannotBeModified() {
        TaskList taskList = new TaskList();
        taskList.add(new ToDo("original task"));
        List<Task> tasks = taskList.getTasks();

        assertThrows(UnsupportedOperationException.class, () ->
                tasks.add(new ToDo("external task")));
        assertEquals(1, taskList.size());
    }

    @Test
    public void constructor_nullTaskList_initializesEmptyList() {
        TaskList taskList = new TaskList(null);
        assertEquals(0, taskList.size());
    }
}
