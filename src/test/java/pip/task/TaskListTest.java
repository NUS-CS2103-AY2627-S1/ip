package pip.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests {@link TaskList}'s mutation methods and its iteration contract.
 */
class TaskListTest {

    @Test
    void newTaskList_isEmpty() {
        TaskList tasks = new TaskList();
        assertEquals(0, tasks.size());
    }

    @Test
    void constructor_copiesGivenList_defensively() {
        List<Task> source = new java.util.ArrayList<>();
        source.add(new Todo("original"));

        TaskList tasks = new TaskList(source);
        source.add(new Todo("added after construction"));

        // Mutating the list passed to the constructor must not affect the TaskList.
        assertEquals(1, tasks.size());
    }

    @Test
    void add_increasesSizeAndAppendsToEnd() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("first"));
        tasks.add(new Todo("second"));

        assertEquals(2, tasks.size());
        assertEquals("first", tasks.get(0).getName());
        assertEquals("second", tasks.get(1).getName());
    }

    @Test
    void get_validIndex_returnsCorrectTask() {
        TaskList tasks = new TaskList();
        Task todo = new Todo("water plants");
        tasks.add(todo);

        assertEquals(todo, tasks.get(0));
    }

    @Test
    void get_invalidIndex_throwsIndexOutOfBoundsException() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("only task"));

        assertThrows(IndexOutOfBoundsException.class, () -> tasks.get(1));
        assertThrows(IndexOutOfBoundsException.class, () -> tasks.get(-1));
    }

    @Test
    void remove_validIndex_removesAndReturnsCorrectTask() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("keep"));
        Task toRemove = new Todo("remove me");
        tasks.add(toRemove);
        tasks.add(new Todo("also keep"));

        Task removed = tasks.remove(1);

        assertEquals(toRemove, removed);
        assertEquals(2, tasks.size());
        assertEquals("keep", tasks.get(0).getName());
        assertEquals("also keep", tasks.get(1).getName());
    }

    @Test
    void remove_invalidIndex_throwsIndexOutOfBoundsException() {
        TaskList tasks = new TaskList();
        assertThrows(IndexOutOfBoundsException.class, () -> tasks.remove(0));
    }

    @Test
    void iterator_visitsTasksInInsertionOrder() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("a"));
        tasks.add(new Todo("b"));
        tasks.add(new Todo("c"));

        StringBuilder names = new StringBuilder();
        for (Task task : tasks) {
            names.append(task.getName());
        }

        assertEquals("abc", names.toString());
    }

    @Test
    void iterator_isUnmodifiable() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("a"));

        Iterator<Task> iterator = tasks.iterator();
        iterator.next();

        assertThrows(UnsupportedOperationException.class, iterator::remove);
        // Underlying list must be unaffected.
        assertFalse(tasks.size() == 0);
    }

    @Test
    void size_reflectsAddsAndRemoves() {
        TaskList tasks = new TaskList();
        assertEquals(0, tasks.size());

        tasks.add(new Todo("a"));
        assertEquals(1, tasks.size());

        tasks.add(new Todo("b"));
        assertEquals(2, tasks.size());

        tasks.remove(0);
        assertEquals(1, tasks.size());
    }

    @Test
    void find_matchingKeyword_returnsOnlyMatchingTasksInOriginalOrder() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Todo("write report"));
        tasks.add(new Todo("return book"));

        TaskList matches = tasks.find("book");

        assertEquals(2, matches.size());
        assertEquals("read book", matches.get(0).getName());
        assertEquals("return book", matches.get(1).getName());
    }

    @Test
    void find_isCaseInsensitive() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("Read Book"));

        TaskList matches = tasks.find("book");

        assertEquals(1, matches.size());
    }

    @Test
    void find_noMatches_returnsEmptyTaskList() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        TaskList matches = tasks.find("nonexistent");

        assertEquals(0, matches.size());
    }

    @Test
    void find_doesNotModifyOriginalTaskList() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Todo("write report"));

        tasks.find("book");

        assertEquals(2, tasks.size());
    }
}
