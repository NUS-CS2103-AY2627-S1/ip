package henry.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests task-list mutations and protection of its internal list in {@link TaskList}.
 */
public class TaskListTest {
    @Test
    public void constructor_sourceListChanged_doesNotChangeTaskList() {
        ArrayList<Task> source = new ArrayList<>();
        source.add(new Todo("first"));
        TaskList taskList = new TaskList(source);

        source.add(new Todo("second"));

        assertEquals(1, taskList.size());
    }

    @Test
    public void asList_addAttempt_throwsUnsupportedOperationException() {
        TaskList taskList = new TaskList(List.of(new Todo("existing")));

        assertThrows(UnsupportedOperationException.class, () ->
                taskList.asList().add(new Todo("new")));
    }

    @Test
    public void addAtIndex_task_insertsTaskAtRequestedPosition() {
        Task first = new Todo("first");
        Task inserted = new Todo("inserted");
        TaskList taskList = new TaskList(List.of(first));

        taskList.add(0, inserted);

        assertEquals(2, taskList.size());
        assertSame(inserted, taskList.get(0));
        assertSame(first, taskList.get(1));
    }

    @Test
    public void delete_validIndex_removesAndReturnsSelectedTask() {
        Task first = new Todo("first");
        Task second = new Todo("second");
        TaskList taskList = new TaskList(List.of(first, second));

        Task deleted = taskList.delete(0);

        assertSame(first, deleted);
        assertEquals(1, taskList.size());
        assertSame(second, taskList.get(0));
    }

    @Test
    public void setDone_trueThenFalse_updatesCompletionStatus() {
        Task task = new Todo("read book");
        TaskList taskList = new TaskList(List.of(task));

        taskList.setDone(0, true);
        assertTrue(task.isDone());

        taskList.setDone(0, false);
        assertFalse(task.isDone());
    }

    @Test
    public void find_matchingKeyword_returnsMatchingTasksInOriginalOrder() {
        Task firstMatch = new Todo("read book");
        Task nonMatch = new Todo("buy groceries");
        Task secondMatch = new Todo("return book");
        TaskList taskList = new TaskList(List.of(firstMatch, nonMatch, secondMatch));

        List<Task> matches = taskList.find("book");

        assertEquals(List.of(firstMatch, secondMatch), matches);
    }

    @Test
    public void find_noMatchingKeyword_returnsEmptyList() {
        TaskList taskList = new TaskList(List.of(new Todo("read book")));

        assertTrue(taskList.find("groceries").isEmpty());
    }
}
