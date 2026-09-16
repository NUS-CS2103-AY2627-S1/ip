package tasque.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

public class TaskListTest {
    @Test
    public void constructor_suppliedListChanges_preservesCopiedContents() {
        Task firstTask = new Todo("first task");
        Task secondTask = new Todo("second task");
        List<Task> suppliedTasks = new ArrayList<>(List.of(firstTask, secondTask));
        TaskList tasks = new TaskList(suppliedTasks);

        suppliedTasks.clear();
        suppliedTasks.add(new Todo("replacement task"));

        assertEquals(List.of(firstTask, secondTask), tasks.getTasks());
        assertEquals(2, tasks.getSize());
    }

    @Test
    public void getTasks_listChanges_returnsUnmodifiableBackedView() {
        Task firstTask = new Todo("first task");
        Task secondTask = new Todo("second task");
        TaskList tasks = new TaskList(List.of(firstTask));
        List<Task> view = tasks.getTasks();

        assertThrows(UnsupportedOperationException.class, view::clear);
        assertEquals(List.of(firstTask), tasks.getTasks());

        tasks.add(secondTask);
        assertEquals(List.of(firstTask, secondTask), view);
        tasks.delete(1);
        assertEquals(List.of(secondTask), view);
    }

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

    @Test
    public void findTasks_mixedCaseAndCompletion_matchesSubstringsInListOrder() {
        Task substringMatch = new Todo("read notebook");
        Task differentCase = new Todo("Book club");
        Task completedMatch = new Todo("return book");
        completedMatch.markAsDone();
        TaskList tasks = new TaskList(List.of(substringMatch, differentCase, completedMatch));

        assertEquals(List.of(substringMatch, completedMatch), tasks.findTasks("book"));
        assertEquals(List.of(differentCase), tasks.findTasks("Book"));
    }

    @Test
    public void findTasks_keywordOnlyInMetadata_returnsNoMatches() {
        Task completedTask = new Todo("read book");
        completedTask.markAsDone();
        TaskList tasks = new TaskList(List.of(completedTask,
                new Deadline("submit report", "2026-08-26"),
                Event.fromStoredValues("team meeting", "Monday", "Tuesday")));

        for (String keyword : new String[] {"[D]", "[X]", "2026", "Monday"}) {
            assertEquals(List.of(), tasks.findTasks(keyword), keyword);
        }
    }
}
