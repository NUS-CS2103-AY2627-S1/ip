package tasque.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

public class TaskListTest {
    @Test
    public void hasIncompleteDuplicate_comparesDetailsAndIgnoresCompletedTasks() {
        Task[] tasks = {new Todo("task"), new Deadline("task", "2026-08-26"),
            new Event("task", "2026-08-26", "2026-08-27")};
        TaskList taskList = new TaskList(List.of(tasks));
        Task[] matching = {new Todo("task"), new Deadline("task", "2026-08-26"),
            new Event("task", "2026-08-26", "2026-08-27")};
        Task[] different = {new Todo("Task"), new Deadline("task", "2026-08-27"),
            new Event("task", "2026-08-25", "2026-08-27"),
            new Event("task", "2026-08-26", "2026-08-28"),
            Event.fromStoredValues("task", "Monday", "Tuesday")};

        for (Task candidate : different) {
            assertFalse(taskList.hasIncompleteDuplicate(candidate), candidate.toString());
        }
        for (int i = 0; i < matching.length; i++) {
            assertTrue(taskList.hasIncompleteDuplicate(matching[i]));
            tasks[i].markAsDone();
            assertFalse(taskList.hasIncompleteDuplicate(matching[i]));
        }
    }

    @Test
    public void markAsDone_unmarkedTask_returnsMarkedTask() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        Task markedTask = tasks.markAsDone(1);
        assertEquals("X", markedTask.getStatusIcon());
    }

    @Test
    public void markAsDone_secondTask_marksOnlySecondTask() {
        TaskList tasks = new TaskList();
        Task firstTask = new Todo("read book");
        Task secondTask = new Todo("submit report");
        tasks.add(firstTask);
        tasks.add(secondTask);

        Task markedTask = tasks.markAsDone(2);

        assertSame(secondTask, markedTask);
        assertEquals(" ", firstTask.getStatusIcon());
        assertEquals("X", secondTask.getStatusIcon());
    }

    @Test
    public void markAsDone_doneTask_remainsMarked() {
        TaskList tasks = new TaskList();
        Task task = new Todo("read book");
        task.markAsDone();
        tasks.add(task);

        Task markedTask = tasks.markAsDone(1);

        assertSame(task, markedTask);
        assertEquals("X", task.getStatusIcon());
    }

    @Test
    public void findTasks_keywordMatchesOneTask_returnsMatchingTask() {
        TaskList tasks = new TaskList();
        Task matchingTask = new Todo("read book");
        tasks.add(matchingTask);
        tasks.add(new Todo("submit report"));

        List<Task> matches = tasks.findTasks("book");

        assertEquals(List.of(matchingTask), matches);
    }

    @Test
    public void findTasks_keywordMatchesMultipleTasks_returnsAllMatchesInListOrder() {
        TaskList tasks = new TaskList();
        Task firstMatchingTask = new Todo("read book");
        Task secondMatchingTask = new Todo("return book");
        tasks.add(firstMatchingTask);
        tasks.add(new Todo("submit report"));
        tasks.add(secondMatchingTask);

        List<Task> matches = tasks.findTasks("book");

        assertEquals(List.of(firstMatchingTask, secondMatchingTask), matches);
    }

    @Test
    public void findTasks_noMatchingTask_returnsEmptyListWithoutChangingTaskList() {
        TaskList tasks = new TaskList();
        Task task = new Todo("read book");
        tasks.add(task);

        List<Task> matches = tasks.findTasks("report");

        assertEquals(List.of(), matches);
        assertEquals(List.of(task), tasks.getTasks());
    }
}
