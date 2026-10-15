package heisenberg;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

public class TaskListTest {
    @Test
    public void deleteTask_validTaskNumber_removesAndReturnsTask() {
        TaskList taskList = new TaskList();
        Task firstTask = new ToDo("read book");
        Task secondTask = new ToDo("return book");
        taskList.addTask(firstTask);
        taskList.addTask(secondTask);

        Task deletedTask = taskList.deleteTask(1);

        assertSame(firstTask, deletedTask);
        assertEquals(1, taskList.size());
        assertSame(secondTask, taskList.getTask(1));
    }

    @Test
    public void deleteTask_invalidTaskNumber_throwsInvalidTaskNumberException() {
        TaskList taskList = new TaskList();
        taskList.addTask(new ToDo("read book"));

        assertThrows(InvalidTaskNumberException.class, () -> taskList.deleteTask(0));
        assertThrows(InvalidTaskNumberException.class, () -> taskList.deleteTask(2));
    }

    @Test
    public void sortByDeadline_mixedTasks_sortsDeadlinesAndKeepsNewTaskAtBottom() {
        TaskList taskList = new TaskList();
        Task todo = new ToDo("read book");
        Task laterDeadline = new Deadline("submit report",
                LocalDateTime.parse("2026-10-02T18:00"));
        Task earlierDeadline = new Deadline("return book",
                LocalDateTime.parse("2026-09-15T12:00"));
        Task event = new Event("meeting",
                LocalDateTime.parse("2026-09-20T14:00"),
                LocalDateTime.parse("2026-09-20T16:00"));
        taskList.addTask(todo);
        taskList.addTask(laterDeadline);
        taskList.addTask(earlierDeadline);
        taskList.addTask(event);

        taskList.sortByDeadline();

        assertSame(earlierDeadline, taskList.getTask(1));
        assertSame(laterDeadline, taskList.getTask(2));
        assertSame(todo, taskList.getTask(3));
        assertSame(event, taskList.getTask(4));

        Task newTask = new ToDo("buy bread");
        taskList.addTask(newTask);
        assertSame(newTask, taskList.getTask(5));
    }
}
