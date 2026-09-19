package eli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/** Tests the task collection's user-facing operations. */
public class TaskListTest {
    @Test
    public void addAndRemoveTaskByUserNumber() {
        TaskList tasks = new TaskList();
        tasks.addTask(new Todo("first"));
        tasks.addTask(new Todo("second"));

        assertEquals("first", tasks.getTask(1).toString().substring(7));
        assertEquals("second", tasks.removeTask(2).toString().substring(7));
        assertEquals(1, tasks.size());
    }

    @Test
    public void getTask_invalidUserNumber_throwsAssertionError() {
        TaskList tasks = new TaskList();
        tasks.addTask(new Todo("only task"));

        assertThrows(AssertionError.class, () -> tasks.getTask(0));
        assertThrows(AssertionError.class, () -> tasks.getTask(2));
    }

    @Test
    public void addTask_nullTask_throwsAssertionError() {
        TaskList tasks = new TaskList();

        assertThrows(AssertionError.class, () -> tasks.addTask(null));
    }

    @Test
    public void containsTaskWithSameDetails_comparesTypeAndTaskDetails() {
        TaskList tasks = new TaskList();
        tasks.addTask(new Todo("read notes"));
        tasks.addTask(new Deadline("submit report", "2026-09-18"));

        assertEquals(true, tasks.containsTaskWithSameDetails(new Todo("READ NOTES")));
        assertEquals(true, tasks.containsTaskWithSameDetails(
                new Deadline("submit report", "2026-09-18")));
        assertEquals(false, tasks.containsTaskWithSameDetails(
                new Deadline("submit report", "2026-09-19")));
    }

    @Test
    public void sortTasks_mixedTasks_groupsAndSortsTasks() {
        TaskList tasks = new TaskList();
        tasks.addTask(new Todo("write report"));
        tasks.addTask(new Deadline("later deadline", "2026-09-20"));
        tasks.addTask(new Event("later event", "2026-09-15 1400", "2026-09-15 1600"));
        tasks.addTask(new Deadline("earlier deadline", "2026-09-10"));
        tasks.addTask(new Event("earlier event", "2026-09-12 1400", "2026-09-12 1600"));
        tasks.addTask(new Todo("buy groceries"));

        tasks.sortTasks();

        assertEquals("earlier deadline", tasks.get(0).getDescription());
        assertEquals("later deadline", tasks.get(1).getDescription());
        assertEquals("earlier event", tasks.get(2).getDescription());
        assertEquals("later event", tasks.get(3).getDescription());
        assertEquals("buy groceries", tasks.get(4).getDescription());
        assertEquals("write report", tasks.get(5).getDescription());
    }

    @Test
    public void sortTasks_undatedDeadlines_placesThemAfterDatedThenAlphabetically() {
        TaskList tasks = new TaskList();
        tasks.addTask(new Deadline("zebra", "someday"));
        tasks.addTask(new Deadline("dated", "2026-09-10"));
        tasks.addTask(new Deadline("alpha", "tomorrow"));

        tasks.sortTasks();

        assertEquals("dated", tasks.get(0).getDescription());
        assertEquals("alpha", tasks.get(1).getDescription());
        assertEquals("zebra", tasks.get(2).getDescription());
    }
}
