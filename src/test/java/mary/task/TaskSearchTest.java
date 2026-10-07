package mary.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Test;

/**
 * Tests description matching and the ownership of search result collections.
 */
class TaskSearchTest {
    /**
     * Checks that stream filtering retains every matching subtype and snapshot ownership.
     */
    @Test
    void find_mixedTaskTypes_preservesOrderAndIndependentSnapshot() {
        Task todo = new Todo("read book");
        Task deadline = new Deadline("return BOOK", LocalDateTime.of(2026, 12, 2, 18, 0));
        Task event = new Event("book club", LocalDateTime.of(2026, 12, 2, 14, 0),
                LocalDateTime.of(2026, 12, 2, 16, 0));
        TaskList tasks = new TaskList(List.of(todo, new Todo("buy bread"), deadline, event));
        List<Task> matches = tasks.find("book");
        assertEquals(List.of(todo, deadline, event), matches);
        tasks.add(new Todo("another book"));
        assertEquals(List.of(todo, deadline, event), matches);
        matches.add(new Todo("result only"));
        assertEquals(5, tasks.size());
        assertTrue(tasks.find("result only").isEmpty());
    }

    /**
     * Checks literal substrings, punctuation, and non-ASCII descriptions.
     */
    @Test
    void matchesDescription_substrings_ignoresCaseWithoutUsingRegex() {
        Task task = new Todo("Read Notebook [draft] CAFÉ 书");
        assertTrue(task.matchesDescription("book"));
        assertTrue(task.matchesDescription("READ"));
        assertTrue(task.matchesDescription("[draft]"));
        assertTrue(task.matchesDescription("café"));
        assertTrue(task.matchesDescription("书"));
        assertFalse(task.matchesDescription(".*"));
        assertFalse(task.matchesDescription("missing"));
    }

    /**
     * Checks that date formatting and completion/type markers are not searchable.
     */
    @Test
    void matchesDescription_metadataOnly_doesNotMatch() {
        Task task = new Deadline("return book", LocalDateTime.of(2019, 12, 2, 18, 0));
        task.markAsDone();
        for (String keyword : new String[] {"Dec", "2019", "[D]", "[X]", "by:"}) {
            assertFalse(task.matchesDescription(keyword), keyword);
        }
    }

    /**
     * Checks case folding is independent of the machine's default locale.
     */
    @Test
    void matchesDescription_turkishLocale_matchesAsciiCase() {
        Locale original = Locale.getDefault();
        try {
            Locale.setDefault(Locale.forLanguageTag("tr-TR"));
            assertTrue(new Todo("VISIT library").matchesDescription("visit"));
        } finally {
            Locale.setDefault(original);
        }
    }

    /**
     * Checks duplicate matches, stable order, and an independently mutable result list.
     */
    @Test
    void find_duplicateTasks_preservesOrderWithoutExposingOwnedList() {
        Task first = new Todo("book");
        Task second = new Todo("book");
        TaskList tasks = new TaskList(List.of(first, new Todo("bread"), second));
        List<Task> matches = tasks.find("book");
        assertEquals(List.of(first, second), matches);
        matches.clear();
        assertEquals(3, tasks.size());
        assertTrue(tasks.find("absent").isEmpty());
        assertTrue(new TaskList().find("book").isEmpty());
    }
}
