package percy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

/**
 * Tests {@link TaskList}, focusing on the boundary logic of
 * {@code isValidIndex} and the list-mutating operations.
 */
public class TaskListTest {

    @Test
    public void isValidIndex_emptyList_alwaysFalse() {
        TaskList tasks = new TaskList();
        assertFalse(tasks.isValidIndex(0));
        assertFalse(tasks.isValidIndex(-1));
    }

    @Test
    public void isValidIndex_checksBothEnds() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("a"));
        tasks.add(new Todo("b"));

        assertTrue(tasks.isValidIndex(0));
        assertTrue(tasks.isValidIndex(1));
        assertFalse(tasks.isValidIndex(2));   // one past the last valid index
        assertFalse(tasks.isValidIndex(-1));  // below the first
    }

    @Test
    public void remove_returnsRemovedTaskAndShiftsTheRest() {
        TaskList tasks = new TaskList();
        Todo a = new Todo("a");
        Todo b = new Todo("b");
        Todo c = new Todo("c");
        tasks.add(a);
        tasks.add(b);
        tasks.add(c);

        Task removed = tasks.remove(1);

        assertSame(b, removed);
        assertEquals(2, tasks.size());
        assertSame(a, tasks.get(0));
        assertSame(c, tasks.get(1));
    }

    @Test
    public void seededConstructor_wrapsTheGivenList() {
        ArrayList<Task> seed = new ArrayList<>();
        seed.add(new Todo("x"));
        seed.add(new Deadline("y", java.time.LocalDate.of(2020, 1, 1)));

        TaskList tasks = new TaskList(seed);

        assertEquals(2, tasks.size());
        assertEquals("x", tasks.get(0).getRawDescription());
    }

    @Test
    public void find_returnsOnlyMatchingTasksInOrder() {
        TaskList tasks = new TaskList();
        Todo readBook = new Todo("read book");
        Todo buyMilk = new Todo("buy milk");
        Deadline returnBook = new Deadline("return book", java.time.LocalDate.of(2019, 6, 6));
        tasks.add(readBook);
        tasks.add(buyMilk);
        tasks.add(returnBook);

        TaskList matches = tasks.find("book");

        assertEquals(2, matches.size());
        assertSame(readBook, matches.get(0));
        assertSame(returnBook, matches.get(1));
    }

    @Test
    public void find_isCaseInsensitive() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("Read Book"));

        assertEquals(1, tasks.find("book").size());
        assertEquals(1, tasks.find("READ").size());
    }

    @Test
    public void find_noMatch_returnsEmptyList() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        assertEquals(0, tasks.find("homework").size());
    }

    @Test
    public void stream_yieldsTasksInOrder() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("a"));
        tasks.add(new Todo("b"));

        java.util.List<String> descriptions = tasks.stream()
                .map(Task::getRawDescription)
                .toList();

        assertEquals(java.util.List.of("a", "b"), descriptions);
    }
}
