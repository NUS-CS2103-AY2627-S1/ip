package sylveon;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;

/** Tests task-list sorting behavior. */
public class TaskListTest {
    @Test
    public void sortAlphabetically_tasksWithDifferentDescriptions_returnsAlphabeticalOrder() {
        ArrayList<Task> initialTasks = new ArrayList<>();
        initialTasks.add(new Todo("zebra"));
        initialTasks.add(new Deadline("alpha", LocalDate.of(2026, 9, 20)));
        initialTasks.add(new Todo("Beta"));

        TaskList taskList = new TaskList(initialTasks);

        ArrayList<Task> sortedTasks = taskList.sortAlphabetically();

        assertEquals("alpha", sortedTasks.get(0).getDescription());
        assertEquals("Beta", sortedTasks.get(1).getDescription());
        assertEquals("zebra", sortedTasks.get(2).getDescription());
    }

    @Test
    public void sortByType_deadlinesInMixedList_sortsOnlyDeadlines() {
        ArrayList<Task> initialTasks = new ArrayList<>();
        Todo todo = new Todo("todo");
        Deadline later = new Deadline("later", LocalDate.of(2026, 9, 20));
        Deadline earlier = new Deadline("earlier", LocalDate.of(2026, 9, 10));
        initialTasks.add(later);
        initialTasks.add(todo);
        initialTasks.add(earlier);

        TaskList taskList = new TaskList(initialTasks);

        ArrayList<Task> sortedDeadlines = taskList.sortByType("deadline");

        assertEquals(2, sortedDeadlines.size());
        assertEquals(earlier, sortedDeadlines.get(0));
        assertEquals(later, sortedDeadlines.get(1));
        assertEquals(earlier, taskList.get(0));
        assertEquals(todo, taskList.get(1));
        assertEquals(later, taskList.get(2));
    }
}
