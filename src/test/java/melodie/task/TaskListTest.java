package melodie.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

public class TaskListTest {

    @Test
    public void find_caseInsensitivePartialKeyword_returnsMatchingTasksInOrder() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read a book about Teyvat"));
        tasks.add(new Todo("win a League ranked game"));
        tasks.add(new Deadline(
                "return BOOK to Lisa",
                LocalDateTime.of(2027, 1, 8, 23, 59)));

        List<Task> actualTasks = tasks.find("book");

        assertEquals(2, actualTasks.size());
        assertEquals("read a book about Teyvat", actualTasks.get(0).getDescription());
        assertEquals("return BOOK to Lisa", actualTasks.get(1).getDescription());
    }

    @Test
    public void find_noMatchingDescription_returnsEmptyList() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("complete daily commissions"));
        tasks.add(new Todo("place a control ward at Baron pit"));

        List<Task> actualTasks = tasks.find("Teemo");

        assertTrue(actualTasks.isEmpty());
    }

    @Test
    public void find_keywordOnlyInFormattedDate_returnsEmptyList() {
        TaskList tasks = new TaskList();
        tasks.add(new Deadline(
                "farm Primogems for Chasca",
                LocalDateTime.of(2026, 12, 8, 23, 59)));

        List<Task> actualTasks = tasks.find("Dec");

        assertTrue(actualTasks.isEmpty());
    }

    @Test
    public void update_validIndex_replacesOnlySelectedTask() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("first task"));
        tasks.add(new Todo("second task"));
        Todo replacementTask = new Todo("updated first task");

        Task actualTask = tasks.update(0, replacementTask);

        assertEquals(replacementTask, actualTask);
        assertEquals(replacementTask, tasks.get(0));
        assertEquals("second task", tasks.get(1).getDescription());
    }
}
