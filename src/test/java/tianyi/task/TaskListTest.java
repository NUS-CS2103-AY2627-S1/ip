package tianyi.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests task collection updates, returned tasks, and query filtering.
 */
public class TaskListTest {
    @Test
    public void constructor_sourceListChanged_taskListRemainsUnchanged() {
        List<Task> source = new ArrayList<>(List.of(new ToDo("first")));
        TaskList tasks = new TaskList(source);

        source.add(new ToDo("second"));

        assertEquals(1, tasks.size());
        assertEquals("T | 0 | first", tasks.getTasks().get(0).getData());
    }

    @Test
    public void getTasks_returnedListCannotModifyTaskList() {
        TaskList tasks = new TaskList(List.of(new ToDo("first")));

        assertThrows(UnsupportedOperationException.class, () -> tasks.getTasks().add(new ToDo("second")));
        assertEquals(1, tasks.size());
    }

    @Test
    public void addTask_validTask_addsTask() {
        TaskList tasks = new TaskList();

        tasks.addTask(new ToDo("read book"));

        assertEquals(1, tasks.size());
        assertEquals("T | 0 | read book", tasks.getTasks().get(0).getData());
    }

    @Test
    public void deleteTask_middleTask_removesTaskAndReturnsTask() {
        TaskList tasks = new TaskList(List.of(
                new ToDo("first"), new ToDo("second"), new ToDo("third")));

        Task result = tasks.deleteTask(1);

        assertEquals("T | 0 | second", result.getData());
        assertEquals(2, tasks.size());
        assertEquals("T | 0 | first", tasks.getTasks().get(0).getData());
        assertEquals("T | 0 | third", tasks.getTasks().get(1).getData());
    }

    @Test
    public void markTask_validIndex_marksTaskAndReturnsTask() {
        TaskList tasks = new TaskList(List.of(new ToDo("read book")));

        Task result = tasks.markTask(0);

        assertEquals("T | 1 | read book", result.getData());
        assertEquals("T | 1 | read book", tasks.getTasks().get(0).getData());
    }

    @Test
    public void unmarkTask_markedTask_unmarksTaskAndReturnsTask() {
        ToDo task = new ToDo("read book");
        task.markAsDone();
        TaskList tasks = new TaskList(List.of(task));

        Task result = tasks.unmarkTask(0);

        assertEquals("T | 0 | read book", result.getData());
        assertEquals("T | 0 | read book", tasks.getTasks().get(0).getData());
    }

    @Test
    public void listTasks_emptyList_returnsEmptyList() {
        assertTrue(new TaskList().listTasks().isEmpty());
    }

    @Test
    public void listTasks_multipleTasks_listsInOriginalOrderWithNumbers() {
        ToDo completedTask = new ToDo("first");
        completedTask.markAsDone();
        TaskList tasks = new TaskList(List.of(completedTask, new ToDo("second")));

        assertEquals(List.of("1.[T][X] first", "2.[T][ ] second"),
                tasks.listTasks().stream().map(IndexedTask::toString).toList());
    }

    @Test
    public void listTasks_date_filtersTodosExpiredDeadlinesAndInactiveEvents() {
        TaskList tasks = new TaskList(List.of(
                new ToDo("todo"),
                new Deadline("expired", new TaskTime("1-12-2019")),
                new Deadline("active deadline", new TaskTime("3-12-2019")),
                new Event("past event", new TaskTime("30-11-2019"),
                        new TaskTime("1-12-2019")),
                new Event("active event", new TaskTime("2-12-2019"),
                        new TaskTime("3-12-2019")),
                new Event("future event", new TaskTime("3-12-2019"),
                        new TaskTime("4-12-2019"))));

        List<IndexedTask> result = tasks.listTasks(new TaskTime("2-12-2019"));

        assertEquals(List.of(
                "3.[D][ ] active deadline (by: Tue, Dec 03 2019)",
                "5.[E][ ] active event (from: Mon, Dec 02 2019 to: Tue, Dec 03 2019)"
        ), result.stream().map(IndexedTask::toString).toList());
    }

    @Test
    public void listTasks_dateWithNoMatches_returnsEmptyList() {
        TaskList tasks = new TaskList(List.of(
                new ToDo("todo"),
                new Event("past event", new TaskTime("1-12-2019"),
                        new TaskTime("2-12-2019"))));

        assertTrue(tasks.listTasks(new TaskTime("3-12-2019")).isEmpty());
    }

    @Test
    public void listTasks_keyword_listsMatchingTasksInOriginalOrder() {
        TaskList tasks = new TaskList(List.of(
                new ToDo("read book"),
                new ToDo("buy milk"),
                new ToDo("return book")));

        List<IndexedTask> result = tasks.listTasks("book");

        assertEquals(List.of("1.[T][ ] read book", "3.[T][ ] return book"),
                result.stream().map(IndexedTask::toString).toList());
    }

    @Test
    public void listTasks_keywordWithNoMatches_returnsEmptyList() {
        TaskList tasks = new TaskList(List.of(new ToDo("read book")));

        assertTrue(tasks.listTasks("milk").isEmpty());
    }

    @Test
    public void countIncompleteTasks_taskLifecycle_countsCurrentIncompleteTasks() {
        TaskList tasks = new TaskList();
        assertEquals(0, tasks.countIncompleteTasks());

        tasks.addTask(new ToDo("first"));
        tasks.addTask(new ToDo("second"));
        assertEquals(2, tasks.countIncompleteTasks());

        tasks.markTask(0);
        tasks.markTask(0);
        assertEquals(1, tasks.countIncompleteTasks());

        tasks.markTask(1);
        assertEquals(0, tasks.countIncompleteTasks());

        tasks.unmarkTask(0);
        assertEquals(1, tasks.countIncompleteTasks());

        tasks.deleteTask(0);
        assertEquals(0, tasks.countIncompleteTasks());
    }

    @Test
    public void countIncompleteTasks_date_countsOnlyMatchingIncompleteTasks() {
        Deadline completedDeadline = new Deadline("completed", new TaskTime("3-12-2019"));
        completedDeadline.markAsDone();
        TaskList tasks = new TaskList(List.of(
                new ToDo("todo"),
                completedDeadline,
                new Deadline("incomplete", new TaskTime("3-12-2019")),
                new Event("past event", new TaskTime("30-11-2019"),
                        new TaskTime("1-12-2019"))));

        assertEquals(1, tasks.countIncompleteTasks(new TaskTime("2-12-2019")));
        assertEquals(0, tasks.countIncompleteTasks(new TaskTime("4-12-2019")));
    }
}
