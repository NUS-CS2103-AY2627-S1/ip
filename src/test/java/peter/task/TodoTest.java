package peter.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link Todo}, and through it the completion-status behavior
 * inherited from {@link Task}.
 *
 * <p>{@code Todo} is the simplest concrete task, which makes it the natural
 * place to test the shared {@code Task} methods without an unrelated schedule
 * getting in the way.
 */
public class TodoTest {

    // =====================================================================
    // toDataString()
    // =====================================================================

    @Test
    public void toDataString_notDone_zeroStatusWritten() {
        assertEquals("T | 0 | read book", new Todo("read book").toDataString());
    }

    @Test
    public void toDataString_done_oneStatusWritten() {
        Todo todo = new Todo("read book");
        todo.markAsDone();
        assertEquals("T | 1 | read book", todo.toDataString());
    }

    @Test
    public void toDataString_noScheduleFields_threeFieldsWritten() {
        // A todo line has exactly three fields; Storage rejects any other
        // count, so the two formats must agree.
        assertEquals(3, new Todo("read book").toDataString().split(" \\| ", -1).length);
    }

    // =====================================================================
    // getScheduleDetails()
    // =====================================================================

    @Test
    public void getScheduleDetails_todo_emptyStringReturned() {
        // A todo has no schedule, so it must contribute nothing to the
        // displayed line rather than an empty bracket pair.
        assertEquals("", new Todo("read book").getScheduleDetails());
    }

    // =====================================================================
    // occursOn(LocalDate): the inherited default
    // =====================================================================

    @Test
    public void occursOn_anyDate_falseReturned() {
        // Todos are unscheduled, so they never appear in an "on <date>" query.
        Todo todo = new Todo("read book");
        assertFalse(todo.occursOn(LocalDate.of(2019, 12, 2)));
        assertFalse(todo.occursOn(LocalDate.now()));
        assertFalse(todo.occursOn(LocalDate.of(1970, 1, 1)));
    }

    // =====================================================================
    // hasKeyword(String): the inherited keyword search
    // =====================================================================

    @Test
    public void hasKeyword_keywordInDescription_trueReturned() {
        assertTrue(new Todo("read book").hasKeyword("book"));
    }

    @Test
    public void hasKeyword_keywordAbsent_falseReturned() {
        assertFalse(new Todo("read book").hasKeyword("homework"));
    }

    @Test
    public void hasKeyword_differentCase_trueReturned() {
        // Searching is case-insensitive, so the user need not match the
        // capitalization they originally typed.
        Todo todo = new Todo("Read Book");
        assertTrue(todo.hasKeyword("book"));
        assertTrue(todo.hasKeyword("BOOK"));
        assertTrue(todo.hasKeyword("bOoK"));
    }

    @Test
    public void hasKeyword_partialWord_trueReturned() {
        // Matching is on any substring, not whole words.
        assertTrue(new Todo("read bookshop sign").hasKeyword("book"));
        assertTrue(new Todo("read book").hasKeyword("oo"));
    }

    @Test
    public void hasKeyword_wholeDescription_trueReturned() {
        assertTrue(new Todo("read book").hasKeyword("read book"));
    }

    @Test
    public void hasKeyword_keywordLongerThanDescription_falseReturned() {
        assertFalse(new Todo("book").hasKeyword("read book"));
    }

    @Test
    public void hasKeyword_emptyKeyword_trueReturned() {
        // Every string contains the empty string. Parser rejects a blank
        // search before this point and never produces an empty keyword, so
        // this only documents the behavior.
        assertTrue(new Todo("read book").hasKeyword(""));
    }

    @Test
    public void hasKeyword_keywordWithSpaces_matchedLiterally() {
        // This method tests a single keyword, so a string with a space in it
        // must appear contiguously. The find command splits its arguments into
        // separate keywords before calling this, so "find read book" does
        // match the description below.
        Todo todo = new Todo("read a book");
        assertTrue(todo.hasKeyword("a book"));
        assertFalse(todo.hasKeyword("read book"));
    }

    // =====================================================================
    // Completion status, inherited from Task
    // =====================================================================

    @Test
    public void isDone_newTask_falseReturned() {
        // A task must start out incomplete regardless of type.
        assertFalse(new Todo("read book").isDone());
    }

    @Test
    public void markAsDone_newTask_taskBecomesDone() {
        Todo todo = new Todo("read book");
        todo.markAsDone();
        assertTrue(todo.isDone());
    }

    @Test
    public void markAsDone_alreadyDone_remainsDone() {
        // Marking twice is not an error and must not toggle the status back.
        Todo todo = new Todo("read book");
        todo.markAsDone();
        todo.markAsDone();
        assertTrue(todo.isDone());
    }

    @Test
    public void unmarkAsDone_doneTask_taskBecomesNotDone() {
        Todo todo = new Todo("read book");
        todo.markAsDone();
        todo.unmarkAsDone();
        assertFalse(todo.isDone());
    }

    @Test
    public void unmarkAsDone_alreadyNotDone_remainsNotDone() {
        Todo todo = new Todo("read book");
        todo.unmarkAsDone();
        assertFalse(todo.isDone());
    }

    @Test
    public void getStatusIcon_notDone_spaceReturned() {
        // The icon is a single space so that "[ ]" and "[X]" line up in the
        // console listing.
        assertEquals(" ", new Todo("read book").getStatusIcon());
    }

    @Test
    public void getStatusIcon_done_crossReturned() {
        Todo todo = new Todo("read book");
        todo.markAsDone();
        assertEquals("X", todo.getStatusIcon());
    }

    @Test
    public void getTaskTypeIcon_todo_tIconReturned() {
        assertEquals("T", new Todo("read book").getTaskTypeIcon());
    }

    @Test
    public void getDescription_todo_descriptionReturned() {
        assertEquals("read book", new Todo("read book").getDescription());
    }
}
