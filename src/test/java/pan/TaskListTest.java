package pan;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link TaskList#find(String)}, the keyword search behind the
 * {@code find} command.
 *
 * <p>{@code find} is pure list logic (tasks in, filtered {@link TaskList} out),
 * so it can be exercised directly without touching files or the console. The
 * other {@code TaskList} methods are one-line wrappers over {@code ArrayList}
 * and are left untested per the project's coverage target.
 */
public class TaskListTest {

    /** Builds a 3-task list: two descriptions contain "book", one does not. */
    private static TaskList sampleList() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Deadline("return book", LocalDateTime.of(2019, 6, 6, 18, 0)));
        tasks.add(new Todo("buy milk"));
        return tasks;
    }

    @Test
    public void find_keywordMatchesMultipleTasks_returnsAllMatches() {
        TaskList matches = sampleList().find("book");

        assertEquals(2, matches.size());
        assertEquals("read book", matches.get(0).getDescription());
        assertEquals("return book", matches.get(1).getDescription());
    }

    @Test
    public void find_keywordIsSubstringOfDescription_matches() {
        TaskList matches = sampleList().find("ilk");

        assertEquals(1, matches.size());
        assertEquals("buy milk", matches.get(0).getDescription());
    }

    @Test
    public void find_noTaskMatches_returnsEmptyList() {
        assertTrue(sampleList().find("zzz").isEmpty());
    }

    @Test
    public void find_differentCase_doesNotMatch() {
        // Matching is case-sensitive: "Book" must not match "book".
        assertTrue(sampleList().find("Book").isEmpty());
    }

    @Test
    public void find_calledOnList_doesNotMutateOriginal() {
        TaskList tasks = sampleList();

        tasks.find("book");

        assertEquals(3, tasks.size());
    }
}
