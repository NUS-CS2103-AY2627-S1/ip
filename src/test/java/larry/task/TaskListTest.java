package larry.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests operations on Larry's task list.
 */
class TaskListTest {
    private TaskList tasks;
    private Todo readBook;
    private Todo returnBook;
    private Todo buyNotebook;

    @BeforeEach
    void setUp() {
        tasks = new TaskList();
        readBook = new Todo("Read Book");
        returnBook = new Todo("return book");
        buyNotebook = new Todo("buy notebook");
        tasks.add(readBook);
        tasks.add(new Todo("write report"));
        tasks.add(returnBook);
        tasks.add(buyNotebook);
    }

    @Test
    void findByDescription_matchingSubstring_matchesInOriginalOrderIgnoringCase() {
        List<Task> matchingTasks = tasks.findByDescription("BOOK");

        assertEquals(3, matchingTasks.size());
        assertSame(readBook, matchingTasks.get(0));
        assertSame(returnBook, matchingTasks.get(1));
        assertSame(buyNotebook, matchingTasks.get(2));
    }

    @Test
    void findByDescription_multiWordPhrase_onlyFullPhraseMatches() {
        List<Task> matchingTasks = tasks.findByDescription("read book");

        assertEquals(List.of(readBook), matchingTasks);
    }

    @Test
    void findByDescription_noMatchingDescription_emptyListReturned() {
        assertEquals(List.of(), tasks.findByDescription("exercise"));
    }

    @Test
    void findByDescription_keywordOnlyInRenderedDetails_emptyListReturned() {
        TaskList deadlines = new TaskList();
        deadlines.add(new Deadline("return item", LocalDateTime.of(2026, 6, 6, 18, 0)));

        assertEquals(List.of(), deadlines.findByDescription("Jun"));
    }

    @Test
    void findByDescription_nullKeyword_nullPointerExceptionThrown() {
        assertThrows(NullPointerException.class, () -> tasks.findByDescription(null));
    }

    @Test
    void add_nullTask_assertionErrorThrown() {
        assertThrows(AssertionError.class, () -> tasks.add(null));
    }

    @Test
    void get_invalidIndex_assertionErrorThrown() {
        assertThrows(AssertionError.class, () -> tasks.get(-1));
        assertThrows(AssertionError.class, () -> tasks.get(tasks.size()));
    }

    @Test
    void delete_invalidIndex_assertionErrorThrown() {
        assertThrows(AssertionError.class, () -> tasks.delete(-1));
        assertThrows(AssertionError.class, () -> tasks.delete(tasks.size()));
    }
}
