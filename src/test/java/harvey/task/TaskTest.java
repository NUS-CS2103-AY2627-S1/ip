package harvey.task;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Tests {@link Task#hasKeyword(String)}, which decides what {@code find} matches, and
 * {@link Task#hasSameDetails(Task)}, which decides what counts as a duplicate.
 * <p>
 * Only the description is searched, so the tests also pin down what must not match:
 * the type marker, the done marker, and a deadline's date all appear in the displayed
 * form of a task but are not part of its description.
 */
public class TaskTest {
    /** A date for the deadline tests. */
    private static final LocalDate OCT_1 = LocalDate.of(2026, 10, 1);

    @Test
    public void hasKeyword_wholeWordInDescription_returnsTrue() {
        assertTrue(new Todo("read book").hasKeyword("book"));
    }

    @Test
    public void hasKeyword_partOfAWord_returnsTrue() {
        // Substring matching is deliberate: "boo" should find "book".
        assertTrue(new Todo("read book").hasKeyword("boo"));
    }

    @Test
    public void hasKeyword_differentCase_returnsTrue() {
        assertTrue(new Todo("read book").hasKeyword("BOOK"));
        assertTrue(new Todo("Read Book").hasKeyword("book"));
    }

    @Test
    public void hasKeyword_wordNotPresent_returnsFalse() {
        assertFalse(new Todo("read book").hasKeyword("sports"));
    }

    @Test
    public void hasKeyword_emptyKeyword_returnsTrue() {
        // Every string contains the empty string. FindCommand refuses an empty
        // keyword before reaching here, so this only documents the behavior.
        assertTrue(new Todo("read book").hasKeyword(""));
    }

    @Test
    public void hasKeyword_textFromTheDisplayedFormOnly_returnsFalse() {
        Todo todo = new Todo("read book");
        todo.markAsDone();
        // "[T]" and "X" show up in toString() but are not part of the description.
        assertFalse(todo.hasKeyword("[T]"));
        assertFalse(todo.hasKeyword("X"));
    }

    @Test
    public void hasKeyword_deadlineDate_returnsFalse() {
        Deadline deadline = new Deadline("return book", java.time.LocalDate.of(2019, 6, 6));
        assertTrue(deadline.hasKeyword("return"));
        // The date is a field of its own, not part of the description.
        assertFalse(deadline.hasKeyword("2019"));
    }

    @Test
    public void hasSameDetails_todosDifferingOnlyInCase_returnsTrue() {
        assertTrue(new Todo("Read Book").hasSameDetails(new Todo("read book")));
    }

    @Test
    public void hasSameDetails_oneDoneOneNot_stillReturnsTrue() {
        // Being done is the state of a task, not one of its details.
        Todo done = new Todo("read book");
        done.markAsDone();
        assertTrue(done.hasSameDetails(new Todo("read book")));
    }

    @Test
    public void hasSameDetails_todoAgainstDeadline_returnsFalseBothWays() {
        // Checked in both directions, because each call runs a different class's method.
        Todo todo = new Todo("essay");
        Deadline deadline = new Deadline("essay", OCT_1);
        assertFalse(todo.hasSameDetails(deadline));
        assertFalse(deadline.hasSameDetails(todo));
    }

    @Test
    public void hasSameDetails_deadlinesWithSameDate_returnsTrue() {
        assertTrue(new Deadline("essay", OCT_1).hasSameDetails(new Deadline("ESSAY", OCT_1)));
    }

    @Test
    public void hasSameDetails_deadlinesWithOtherDate_returnsFalse() {
        assertFalse(new Deadline("essay", OCT_1).hasSameDetails(new Deadline("essay", OCT_1.plusDays(1))));
    }

    @Test
    public void isDone_markedThenUnmarked_followsEachChange() {
        Todo todo = new Todo("read book");
        assertFalse(todo.isDone());
        todo.markAsDone();
        assertTrue(todo.isDone());
        todo.markAsNotDone();
        assertFalse(todo.isDone());
    }
}
