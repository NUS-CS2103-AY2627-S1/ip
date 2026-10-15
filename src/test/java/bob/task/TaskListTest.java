package bob.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

/**
 * Tests the behavior of {@link TaskList}.
 */
public class TaskListTest {

    @Test
    public void defaultConstructor_createsEmptyList() {
        TaskList tasks = new TaskList();
        assertTrue(tasks.isEmpty());
        assertEquals(0, tasks.size());
    }

    @Test
    public void parameterizedConstructor_initializesWithTasks() {
        List<Task> list = new ArrayList<>();
        list.add(new ToDo("task 1"));
        list.add(new ToDo("task 2"));

        TaskList tasks = new TaskList(list);
        assertEquals(2, tasks.size());
        assertFalse(tasks.isEmpty());
        assertEquals("[T][ ] task 1", tasks.get(0).toString());
    }

    @Test
    public void addAndGet_validTasks_storesAndRetrievesCorrectly() {
        TaskList tasks = new TaskList();
        Task todo = new ToDo("sample task");

        tasks.add(todo);

        assertEquals(1, tasks.size());
        assertEquals(todo, tasks.get(0));
    }

    @Test
    public void remove_validIndex_removesAndReturnsTask() {
        TaskList tasks = new TaskList();
        Task task1 = new ToDo("task 1");
        Task task2 = new ToDo("task 2");
        tasks.add(task1);
        tasks.add(task2);

        Task removed = tasks.remove(0);

        assertEquals(task1, removed);
        assertEquals(1, tasks.size());
        assertEquals(task2, tasks.get(0));
    }

    @Test
    public void set_validIndex_replacesTask() {
        TaskList tasks = new TaskList();
        Task task1 = new ToDo("task 1");
        Task task2 = new ToDo("task 2");
        tasks.add(task1);

        tasks.set(0, task2);

        assertEquals(1, tasks.size());
        assertEquals(task2, tasks.get(0));
    }

    @Test
    public void getAndRemoveAndSet_invalidIndex_throwsIndexOutOfBoundsException() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("task 1"));

        assertThrows(IndexOutOfBoundsException.class, () -> tasks.get(1));
        assertThrows(IndexOutOfBoundsException.class, () -> tasks.get(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> tasks.remove(1));
        assertThrows(IndexOutOfBoundsException.class, () -> tasks.remove(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> tasks.set(1, new ToDo("other")));
        assertThrows(IndexOutOfBoundsException.class, () -> tasks.set(-1, new ToDo("other")));
    }

    @Test
    public void iterator_iteratesAllElements() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("item 1"));
        tasks.add(new ToDo("item 2"));

        Iterator<Task> it = tasks.iterator();
        assertTrue(it.hasNext());
        assertEquals("[T][ ] item 1", it.next().toString());
        assertTrue(it.hasNext());
        assertEquals("[T][ ] item 2", it.next().toString());
        assertFalse(it.hasNext());
    }

    @Test
    public void find_matchingAndNonMatchingKeyword_returnsFilteredTaskList() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("read book")); // index 1
        tasks.add(new ToDo("return book")); // index 2
        tasks.add(new ToDo("buy groceries")); // index 3
        tasks.add(new ToDo("read another book")); // index 4

        List<Map.Entry<Integer, Task>> matching = tasks.find("book");
        assertEquals(3, matching.size());
        assertEquals(1, matching.get(0).getKey());
        assertEquals("[T][ ] read book", matching.get(0).getValue().toString());
        assertEquals(2, matching.get(1).getKey());
        assertEquals("[T][ ] return book", matching.get(1).getValue().toString());
        assertEquals(4, matching.get(2).getKey());
        assertEquals("[T][ ] read another book", matching.get(2).getValue().toString());

        List<Map.Entry<Integer, Task>> caseInsensitive = tasks.find("BOOK");
        assertEquals(3, caseInsensitive.size());

        List<Map.Entry<Integer, Task>> none = tasks.find("swimming");
        assertEquals(0, none.size());
        assertTrue(none.isEmpty());

        List<Map.Entry<Integer, Task>> emptyKeyword = tasks.find("");
        assertEquals(0, emptyKeyword.size());
    }
}
