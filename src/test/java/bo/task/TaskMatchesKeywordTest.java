package bo.task;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/**
 * Tests for {@link Task#matchesKeyword(String)}.
 *
 * Task is abstract, so a concrete subclass (Todo) is used purely as a vehicle
 * to construct instances - the method under test is defined on Task itself
 * and is not overridden by any subclass, so testing through Todo exercises
 * the real Task behaviour.
 *
 * Focuses on:
 *  - exact and partial (substring) matches
 *  - case sensitivity, since it's easy to assume case-insensitive matching
 *    when it isn't (or vice versa) - this is exactly the kind of behaviour
 *    that should be pinned down by a test rather than left ambiguous
 *  - no match, empty keyword, and whole-word vs partial-word matches
 */
public class TaskMatchesKeywordTest {

    @Test
    public void matchesKeyword_exactWordPresent_returnsTrue() {
        Task task = new Todo("read book");

        assertTrue(task.matchesKeyword("book"));
    }

    @Test
    public void matchesKeyword_keywordAsSubstringOfLongerWord_returnsTrue() {
        // "book" is a substring of "bookstore" - matchesKeyword does plain
        // substring matching, not whole-word matching, so this should match.
        Task task = new Todo("visit bookstore");

        assertTrue(task.matchesKeyword("book"));
    }

    @Test
    public void matchesKeyword_wordNotPresent_returnsFalse() {
        Task task = new Todo("read newspaper");

        assertFalse(task.matchesKeyword("book"));
    }

    @Test
    public void matchesKeyword_differentCase_returnsFalse() {
        // Matching is case-sensitive: "Book" (capital B) should not match "book".
        Task task = new Todo("read Book");

        assertFalse(task.matchesKeyword("book"));
    }

    @Test
    public void matchesKeyword_sameCase_returnsTrue() {
        Task task = new Todo("read Book");

        assertTrue(task.matchesKeyword("Book"));
    }

    @Test
    public void matchesKeyword_emptyKeyword_returnsTrue() {
        // An empty string is a substring of every string, so this documents
        // the (perhaps surprising) actual behaviour rather than assuming it.
        Task task = new Todo("read book");

        assertTrue(task.matchesKeyword(""));
    }

    @Test
    public void matchesKeyword_worksOnNonTodoSubclasses_deadline() {
        Task task = new Deadline("return book", LocalDate.of(2024, 3, 2));

        assertTrue(task.matchesKeyword("book"));
    }

    @Test
    public void matchesKeyword_worksOnNonTodoSubclasses_event() {
        Task task = new Event("book club meeting",
                LocalDateTime.of(2026, 10, 10, 14, 0),
                LocalDateTime.of(2026, 10, 10, 16, 0));

        assertTrue(task.matchesKeyword("club"));
        assertFalse(task.matchesKeyword("dinner"));
    }
}
