package xiaozhi.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

/**
 * Tests {@link TaskList}, the thin wrapper other classes use instead of a
 * raw {@code ArrayList<Task>} directly.
 */
public class TaskListTest {

    @Test
    public void constructor_noArgs_startsEmpty() {
        TaskList tasks = new TaskList();

        assertEquals(0, tasks.size());
    }

    @Test
    public void constructor_withExistingList_wrapsIt() {
        ArrayList<Task> existing = new ArrayList<>();
        existing.add(new Todo("read book"));

        TaskList tasks = new TaskList(existing);

        assertEquals(1, tasks.size());
        assertSame(existing.get(0), tasks.get(0));
    }

    @Test
    public void add_appendsToEndAndIncreasesSize() {
        TaskList tasks = new TaskList();
        Todo first = new Todo("read book");
        Todo second = new Todo("write essay");

        tasks.add(first);
        tasks.add(second);

        assertEquals(2, tasks.size());
        assertSame(first, tasks.get(0));
        assertSame(second, tasks.get(1));
    }

    @Test
    public void remove_returnsRemovedTaskAndShrinksList() {
        TaskList tasks = new TaskList();
        Todo first = new Todo("read book");
        tasks.add(first);
        tasks.add(new Todo("write essay"));

        Task removed = tasks.remove(0);

        assertSame(first, removed);
        assertEquals(1, tasks.size());
        assertEquals("write essay", tasks.get(0).getDescription());
    }

    @Test
    public void insert_atMiddleIndex_shiftsLaterTasksUp() {
        TaskList tasks = new TaskList();
        Todo first = new Todo("read book");
        Todo third = new Todo("write essay");
        tasks.add(first);
        tasks.add(third);
        Todo inserted = new Todo("return book");

        tasks.insert(1, inserted);

        assertEquals(3, tasks.size());
        assertSame(first, tasks.get(0));
        assertSame(inserted, tasks.get(1));
        assertSame(third, tasks.get(2));
    }

    @Test
    public void insert_atEnd_appendsLikeAdd() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        Todo appended = new Todo("write essay");

        tasks.insert(1, appended);

        assertEquals(2, tasks.size());
        assertSame(appended, tasks.get(1));
    }

    @Test
    public void get_doesNotRemoveTheTask() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        Task first = tasks.get(0);

        assertEquals("read book", first.getDescription());
        assertEquals(1, tasks.size());
    }

    @Test
    public void asList_reflectsTheSameUnderlyingTasks() {
        TaskList tasks = new TaskList();
        Todo only = new Todo("read book");
        tasks.add(only);

        ArrayList<Task> asList = tasks.asList();

        assertEquals(1, asList.size());
        assertSame(only, asList.get(0));
    }

    @Test
    public void find_keywordInSomeDescriptions_returnsOnlyMatchesInOriginalOrder() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Todo("write essay"));
        tasks.add(new Todo("return book"));

        ArrayList<Task> matches = tasks.find("book");

        assertEquals(2, matches.size());
        assertEquals("read book", matches.get(0).getDescription());
        assertEquals("return book", matches.get(1).getDescription());
    }

    @Test
    public void find_isCaseInsensitive() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("Read Book"));

        ArrayList<Task> matches = tasks.find("READ");

        assertEquals(1, matches.size());
    }

    @Test
    public void find_noDescriptionContainsKeyword_returnsEmptyList() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        ArrayList<Task> matches = tasks.find("homework");

        assertTrue(matches.isEmpty());
    }
}
