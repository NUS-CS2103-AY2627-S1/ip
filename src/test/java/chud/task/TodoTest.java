package chud.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/** Also exercises Task's shared behavior (mark/unmark/status/format), via the simplest subclass. */
class TodoTest {
    @Test
    void newTask_isNotDoneByDefault() {
        Todo todo = new Todo("borrow book");

        assertEquals("[T][ ] borrow book", todo.toString());
    }

    @Test
    void markAsDone_thenToString_showsXStatus() {
        Todo todo = new Todo("borrow book");

        todo.markAsDone();

        assertEquals("[T][X] borrow book", todo.toString());
    }

    @Test
    void markAsNotDone_afterMarkAsDone_revertsToNotDoneStatus() {
        Todo todo = new Todo("borrow book");
        todo.markAsDone();

        todo.markAsNotDone();

        assertEquals("[T][ ] borrow book", todo.toString());
    }

    @Test
    void toFileString_encodesTagDoneFlagAndDescription() {
        Todo todo = new Todo("borrow book");

        assertEquals("T | 0 | borrow book", todo.toFileString());
    }

    @Test
    void toFileString_markedDone_encodesDoneFlagAsOne() {
        Todo todo = new Todo("borrow book");
        todo.markAsDone();

        assertEquals("T | 1 | borrow book", todo.toFileString());
    }

    @Test
    void occursOn_anyDate_returnsFalse() {
        Todo todo = new Todo("borrow book");

        assertFalse(todo.occursOn(LocalDate.of(2019, 12, 2)));
    }

    @Test
    void matchesKeyword_descriptionContainsKeyword_returnsTrue() {
        Todo todo = new Todo("borrow book");

        assertTrue(todo.matchesKeyword("book"));
    }

    @Test
    void matchesKeyword_descriptionDoesNotContainKeyword_returnsFalse() {
        Todo todo = new Todo("borrow book");

        assertFalse(todo.matchesKeyword("laundry"));
    }

    @Test
    void matchesKeyword_differentCase_matchesCaseInsensitively() {
        Todo todo = new Todo("Borrow Book");

        assertTrue(todo.matchesKeyword("book"));
    }

    @Test
    void getSortDate_todoHasNoDate_returnsNull() {
        Todo todo = new Todo("borrow book");

        assertNull(todo.getSortDate());
    }
}
