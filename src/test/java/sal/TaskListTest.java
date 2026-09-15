package sal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;

/**
 * Tests {@link TaskList} add, lookup, mark, and delete behaviour, including invalid indexes.
 */
public class TaskListTest {
    private Todo createTodo(String description) {
        return new Todo(description);
    }

    @Test
    public void constructor_emptyList_hasSizeZero() {
        TaskList tasks = new TaskList();
        assertEquals(0, tasks.size());
    }

    @Test
    public void constructor_existingTasks_copiesList() throws SalException {
        ArrayList<Task> original = new ArrayList<>();
        original.add(createTodo("read book"));
        TaskList tasks = new TaskList(original);

        original.add(createTodo("extra task"));
        assertEquals(1, tasks.size());
        assertEquals("read book", tasks.get(0).description);
    }

    @Test
    public void add_multipleTasks_appendsInOrder() throws SalException {
        TaskList tasks = new TaskList();
        Todo first = createTodo("first");
        Todo second = createTodo("second");
        tasks.add(first);
        tasks.add(second);

        assertEquals(2, tasks.size());
        assertSame(first, tasks.get(0));
        assertSame(second, tasks.get(1));
    }

    @Test
    public void get_validIndex_returnsTask() throws SalException {
        TaskList tasks = new TaskList();
        Todo expected = createTodo("read book");
        tasks.add(expected);
        assertSame(expected, tasks.get(0));
    }

    @Test
    public void get_negativeIndex_exceptionThrown() {
        TaskList tasks = new TaskList();
        tasks.add(createTodo("read book"));
        SalException exception = assertThrows(SalException.class, () -> tasks.get(-1));
        assertTrue(exception.getMessage().contains("out of bounds"));
    }

    @Test
    public void get_indexEqualToSize_exceptionThrown() {
        TaskList tasks = new TaskList();
        tasks.add(createTodo("read book"));
        SalException exception = assertThrows(SalException.class, () -> tasks.get(1));
        assertTrue(exception.getMessage().contains("out of bounds"));
    }

    @Test
    public void get_emptyList_exceptionThrown() {
        TaskList tasks = new TaskList();
        assertThrows(SalException.class, () -> tasks.get(0));
    }

    @Test
    public void delete_validIndex_removesAndReturnsTask() throws SalException {
        TaskList tasks = new TaskList();
        Todo first = createTodo("first");
        Todo second = createTodo("second");
        Todo third = createTodo("third");
        tasks.add(first);
        tasks.add(second);
        tasks.add(third);

        Task removed = tasks.delete(1);
        assertSame(second, removed);
        assertEquals(2, tasks.size());
        assertSame(first, tasks.get(0));
        assertSame(third, tasks.get(1));
    }

    @Test
    public void delete_invalidIndex_exceptionThrown() {
        TaskList tasks = new TaskList();
        tasks.add(createTodo("read book"));
        assertThrows(SalException.class, () -> tasks.delete(-1));
        assertThrows(SalException.class, () -> tasks.delete(1));
        assertEquals(1, tasks.size());
    }

    @Test
    public void markAsDone_validIndex_marksTask() throws SalException {
        TaskList tasks = new TaskList();
        tasks.add(createTodo("read book"));

        Task marked = tasks.markAsDone(0);
        assertTrue(marked.isDone);
        assertTrue(tasks.get(0).isDone);
        assertEquals("X", tasks.get(0).getStatusIcon());
    }

    @Test
    public void markAsDone_invalidIndex_exceptionThrown() {
        TaskList tasks = new TaskList();
        tasks.add(createTodo("read book"));
        assertThrows(SalException.class, () -> tasks.markAsDone(3));
        assertFalse(tasks.getTasks().get(0).isDone);
    }

    @Test
    public void markAsNotDone_afterMarking_unmarksTask() throws SalException {
        TaskList tasks = new TaskList();
        tasks.add(createTodo("read book"));
        tasks.markAsDone(0);

        Task unmarked = tasks.markAsNotDone(0);
        assertFalse(unmarked.isDone);
        assertEquals(" ", tasks.get(0).getStatusIcon());
    }

    @Test
    public void markAsNotDone_invalidIndex_exceptionThrown() {
        TaskList tasks = new TaskList();
        assertThrows(SalException.class, () -> tasks.markAsNotDone(0));
    }

    @Test
    public void tag_validIndex_addsTag() throws SalException {
        TaskList tasks = new TaskList();
        tasks.add(createTodo("read book"));

        Task tagged = tasks.tag(0, "fun");
        assertEquals(1, tagged.getTags().size());
        assertEquals("fun", tagged.getTags().get(0));
        assertEquals("fun", tasks.get(0).getTags().get(0));
    }

    @Test
    public void tag_invalidIndex_exceptionThrown() {
        TaskList tasks = new TaskList();
        tasks.add(createTodo("read book"));
        assertThrows(SalException.class, () -> tasks.tag(1, "fun"));
        assertTrue(tasks.getTasks().get(0).getTags().isEmpty());
    }

    @Test
    public void find_matchingDescriptions_returnsMatchesInOrder() {
        TaskList tasks = new TaskList();
        Todo readBook = createTodo("read book");
        Deadline returnBook = new Deadline("return book", new TaskDateTime(LocalDate.of(2019, 6, 6)));
        Todo joinClub = createTodo("join sports club");
        tasks.add(readBook);
        tasks.add(returnBook);
        tasks.add(joinClub);

        ArrayList<Task> matches = tasks.find("book");
        assertEquals(2, matches.size());
        assertSame(readBook, matches.get(0));
        assertSame(returnBook, matches.get(1));
        assertEquals(3, tasks.size());
    }

    @Test
    public void find_noMatches_returnsEmptyList() {
        TaskList tasks = new TaskList();
        tasks.add(createTodo("read book"));
        tasks.add(createTodo("join sports club"));

        ArrayList<Task> matches = tasks.find("meeting");
        assertTrue(matches.isEmpty());
        assertEquals(2, tasks.size());
    }

    @Test
    public void find_partialWord_matchesSubstring() {
        TaskList tasks = new TaskList();
        Todo reading = createTodo("reading notes");
        tasks.add(reading);
        tasks.add(createTodo("buy milk"));

        ArrayList<Task> matches = tasks.find("read");
        assertEquals(1, matches.size());
        assertSame(reading, matches.get(0));
    }

    @Test
    public void find_caseSensitive_doesNotMatchDifferentCase() {
        TaskList tasks = new TaskList();
        tasks.add(createTodo("read book"));

        assertTrue(tasks.find("Book").isEmpty());
        assertEquals(1, tasks.find("book").size());
    }

    @Test
    public void find_emptyList_returnsEmptyList() {
        TaskList tasks = new TaskList();
        assertTrue(tasks.find("book").isEmpty());
    }

    @Test
    public void operations_withDeadlineAndEvent_preserveTaskTypes() throws SalException {
        TaskList tasks = new TaskList();
        Deadline deadline = new Deadline("return book", new TaskDateTime(LocalDate.of(2019, 10, 15)));
        Event event = new Event("meeting",
                new TaskDateTime(LocalDate.of(2019, 10, 15)),
                new TaskDateTime(LocalDate.of(2019, 10, 16)));
        tasks.add(deadline);
        tasks.add(event);

        assertEquals(deadline, tasks.get(0));
        assertEquals(event, tasks.get(1));
        tasks.markAsDone(1);
        assertTrue(event.isDone);
    }
}
