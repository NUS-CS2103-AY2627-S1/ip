package shrekanddonkey.task;

import static org.junit.jupiter.api.Assertions.assertSame;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/**
 * Tests sorting behavior for {@link TaskList}.
 */
public class TaskListSortTest {
    @Test
    public void sortByDate_mixedTasks_ordersDatedTasksThenTodos() {
        Todo todo = new Todo("read book");
        Deadline laterDeadline = new Deadline("later report", LocalDateTime.of(2026, 10, 15, 18, 0));
        Event earlierEvent = new Event("earlier meeting",
                LocalDateTime.of(2026, 9, 20, 10, 0), LocalDateTime.of(2026, 9, 20, 11, 0));
        TaskList taskList = createTaskList(todo, laterDeadline, earlierEvent);

        taskList.sortByDate();

        assertSame(earlierEvent, taskList.get(0));
        assertSame(laterDeadline, taskList.get(1));
        assertSame(todo, taskList.get(2));
    }

    @Test
    public void sortByDate_equalDates_preservesOriginalOrder() {
        Deadline firstDeadline = new Deadline("first report", LocalDateTime.of(2026, 10, 15, 18, 0));
        Deadline secondDeadline = new Deadline("second report", LocalDateTime.of(2026, 10, 15, 18, 0));
        TaskList taskList = createTaskList(secondDeadline, firstDeadline);

        taskList.sortByDate();

        assertSame(secondDeadline, taskList.get(0));
        assertSame(firstDeadline, taskList.get(1));
    }

    private TaskList createTaskList(Task... tasks) {
        TaskList taskList = new TaskList();
        for (Task task : tasks) {
            taskList.add(task);
        }
        return taskList;
    }
}
