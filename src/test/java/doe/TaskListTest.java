package doe;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

/** Tests task-list mutations and the user-facing task lookup rules. */
class TaskListTest {

    @Test
    void sortByDate_mixedTasks_chronologicalStableOrderWithUndatedLast() {
        Task undated = new Todo("read");
        Task later = new Deadline("submit", "01-01-2027 0900");
        Task earlier = new Event("meeting", "31-12-2026 0900");
        Task tied = new Deadline("prepare", "31-12-2026 0900");
        Task afternoon = new Event("call", "31-12-2026 1500");
        Task anotherUndated = new Todo("clean");
        earlier.markAsDone();
        TaskList tasks = new TaskList(new ArrayList<>(
                List.of(undated, later, earlier, anotherUndated, tied, afternoon)));

        tasks.sortByDate();

        assertEquals(List.of(earlier, tied, afternoon, later, undated, anotherUndated), tasks.getTasks());
        assertEquals("x", tasks.getTask(0).getStatusIcon());
        tasks.sortByDate();
        assertSame(earlier, tasks.getTask(0));
        assertSame(undated, tasks.removeTask(4));
    }

    @Test
    void sortByDate_emptyAndUndatedLists_orderPreserved() {
        TaskList tasks = new TaskList(new ArrayList<>());
        tasks.sortByDate();
        assertEquals(0, tasks.size());
        Task first = new Todo("first");
        Task second = new Todo("second");
        tasks.addTask(first);
        tasks.addTask(second);
        tasks.sortByDate();
        assertEquals(List.of(first, second), tasks.getTasks());
    }

    @Test
    void constructor_nullBackingList_assertionErrorThrown() {
        assertThrows(AssertionError.class, () -> new TaskList(null));
    }

    @Test
    void addTask_nullTask_assertionErrorThrown() {
        TaskList taskList = new TaskList(new ArrayList<>());

        assertThrows(AssertionError.class, () -> taskList.addTask(null));
    }

    @Test
    void addGetRemoveAndSize_tasksUpdatedInExpectedOrder() {
        TaskList taskList = new TaskList(new ArrayList<>());
        Task firstTask = new Todo("read book");
        Task secondTask = new Todo("write report");

        taskList.addTask(firstTask);
        taskList.addTask(secondTask);

        assertEquals(2, taskList.size());
        assertSame(firstTask, taskList.getTask(0));
        assertSame(secondTask, taskList.removeTask(1));
        assertEquals(1, taskList.size());
        assertSame(firstTask, taskList.getTask(0));
    }

    @Test
    void findTaskIndex_numberAndDescription_ambiguousNamesRejected() {
        TaskList taskList = new TaskList(new ArrayList<>());
        taskList.addTask(new Todo("read book"));
        taskList.addTask(new Todo("write report"));
        taskList.addTask(new Deadline("read book", "01-01-2027 1800"));

        assertEquals(0, taskList.findTaskIndex("1"));
        assertEquals(1, taskList.findTaskIndex("2"));
        assertThrows(IllegalArgumentException.class, () -> taskList.findTaskIndex("read book"));
        assertEquals(0, taskList.findTaskIndex(" 1 "));
        assertEquals(-1, taskList.findTaskIndex("0"));
        assertEquals(-1, taskList.findTaskIndex("4"));
        assertEquals(-1, taskList.findTaskIndex("missing task"));
    }
}
