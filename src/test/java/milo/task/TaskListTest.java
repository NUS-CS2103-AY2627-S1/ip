package milo.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

/** Tests task-list storage and search operations. */
public class TaskListTest {

    @Test
    public void add_tasksAndGetSize_expectedTasksStoredInOrder() {
        TaskList tasks = new TaskList();
        Task first = new ToDo("first");
        Task second = new ToDo("second");

        tasks.add(first);
        tasks.add(second);

        assertEquals(2, tasks.size());
        assertEquals(first, tasks.get(0));
        assertEquals(second, tasks.get(1));
    }

    @Test
    public void removeThenClear_tasks_expectedTasksRemovedAndListEmptied() {
        TaskList tasks = new TaskList(List.of(new ToDo("first"), new ToDo("second")));

        tasks.remove(0);
        assertEquals(1, tasks.size());
        assertEquals("[T] [ ] second", tasks.get(0).toString());

        tasks.clear();
        assertEquals(0, tasks.size());
    }

    @Test
    public void asList_existingTasks_expectedReadOnlySnapshot() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("first"));

        assertEquals(1, tasks.asList().size());
        assertThrows(UnsupportedOperationException.class, () -> tasks.asList().clear());
    }

    /** Verifies matching ignores case and retains the original task order. */
    @Test
    public void find_keywordDifferentCase_expectedMatchingTasksInOriginalOrder() {
        Task first = new ToDo("read book");
        Task second = new Deadline("return book", "2019-06-06");
        Task third = new ToDo("buy milk");
        TaskList tasks = new TaskList(List.of(first, second, third));

        TaskList matches = tasks.find("BOOK");

        assertEquals(2, matches.size());
        assertEquals(first, matches.get(0));
        assertEquals(second, matches.get(1));
    }

    /** Verifies a keyword absent from every description produces no results. */
    @Test
    public void find_keywordWithNoMatches_expectedEmptyTaskList() {
        TaskList tasks = new TaskList(List.of(new ToDo("read book"), new ToDo("buy milk")));

        TaskList matches = tasks.find("holiday");

        assertEquals(0, matches.size());
    }
}
