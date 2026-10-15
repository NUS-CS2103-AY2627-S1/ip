package jaylen;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

/** Tests one-based task lookup, deletion, and read-only task access. */
public class TaskListTest {
    @Test
    public void add_nullTask_assertionThrown() {
        TaskList tasks = new TaskList();

        assertThrows(AssertionError.class, () -> tasks.add(null));
    }

    @Test
    public void getAndRemove_validOneBasedPositions_returnExpectedTasks() {
        TaskList tasks = new TaskList(List.of(new Todo("first"), new Todo("second")));

        assertEquals("first", tasks.get(1).getDescription());
        assertEquals("second", tasks.remove(2).getDescription());
        assertEquals(1, tasks.size());
    }

    @Test
    public void addAtPosition_removedTask_restoresOriginalOrder() {
        TaskList tasks = new TaskList(List.of(new Todo("first"), new Todo("third")));

        tasks.add(2, new Todo("second"));

        assertEquals(List.of("first", "second", "third"),
                tasks.getTasks().stream().map(Task::getDescription).toList());
    }

    @Test
    public void get_invalidPosition_throwsException() {
        TaskList tasks = new TaskList();

        JaylenException exception = assertThrows(JaylenException.class, () -> tasks.get(1));

        assertEquals("That task number does not exist.", exception.getMessage());
    }

    @Test
    public void getTasks_anyList_returnsUnmodifiableView() {
        TaskList tasks = new TaskList(List.of(new Todo("first")));

        assertThrows(UnsupportedOperationException.class, () -> tasks.getTasks().add(new Todo("second")));
    }

    @Test
    public void find_keyword_matchesDescriptionsCaseInsensitively() {
        TaskList tasks = new TaskList(List.of(
                new Todo("read book"),
                new Todo("buy milk"),
                new Todo("return BOOK")));

        assertEquals(List.of(1, 3), tasks.find("book"));
    }

    @Test
    public void find_multiplePartialTerms_matchesDescriptionsInAnyOrder() {
        TaskList tasks = new TaskList(List.of(
                new Todo("read book"),
                new Todo("return BOOK"),
                new Todo("book return form")));

        assertEquals(List.of(2, 3), tasks.find("BOO urn"));
    }
}
