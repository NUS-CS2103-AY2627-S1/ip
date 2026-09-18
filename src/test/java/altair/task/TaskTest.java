package altair.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link Task}, the base class of every task type.
 *
 * <p>{@code Task} is a good JUnit target for the same reason as
 * {@link altair.command.CommandType}: its interesting methods are
 * deterministic and side-effect free. {@link Task#getStatusIcon()},
 * {@link Task#toString()}, and {@link Task#toFileString()} are pure functions
 * of the object's state, and {@link Task#markAsDone()} /
 * {@link Task#markAsNotDone()} only flip an in-memory flag whose effect is
 * observable through those methods.</p>
 *
 * <p>The two protected methods {@code getTaskType()} and {@code getTypeIcon()}
 * are not tested directly; their result (the {@code T} marker for a plain
 * task) is checked through {@code toString} and {@code toFileString}.</p>
 */
public class TaskTest {

    // ----- constructor -----

    @Test
    public void getDescription_returnsTextGivenToConstructor() {
        Task task = new Task("read book");
        assertEquals("read book", task.getDescription());
    }

    @Test
    public void constructor_blankDescription_throwsAssertionError() {
        // The constructor asserts that callers have already rejected blank
        // descriptions; this also confirms assertions are enabled in the tests.
        assertThrows(AssertionError.class, () -> new Task("   "));
    }

    // ----- getStatusIcon -----

    @Test
    public void getStatusIcon_newTask_returnsBlankSpace() {
        Task task = new Task("read book");
        assertEquals(" ", task.getStatusIcon());
    }

    @Test
    public void getStatusIcon_afterMarkAsDone_returnsX() {
        Task task = new Task("read book");
        task.markAsDone();
        assertEquals("X", task.getStatusIcon());
    }

    // ----- descriptionContains -----

    @Test
    public void descriptionContains_keywordIsASubstring_returnsTrue() {
        Task task = new Task("read book");
        assertTrue(task.descriptionContains("book"));
    }

    @Test
    public void descriptionContains_keywordDiffersInCase_returnsTrue() {
        // The search ignores case, so "BOOK" still matches "read book".
        Task task = new Task("read book");
        assertTrue(task.descriptionContains("BOOK"));
    }

    @Test
    public void descriptionContains_keywordAbsent_returnsFalse() {
        Task task = new Task("read book");
        assertFalse(task.descriptionContains("milk"));
    }

    // ----- isDuplicateOf -----

    @Test
    public void isDuplicateOf_sameDescriptionAndType_returnsTrue() {
        Task first = new Task("read book");
        Task second = new Task("read book");
        assertTrue(first.isDuplicateOf(second));
    }

    @Test
    public void isDuplicateOf_descriptionDiffersInCaseAndSpacing_returnsTrue() {
        // Comparison ignores case and collapses internal whitespace, so a typo
        // with extra spaces still counts as the same description.
        Task first = new Task("read book");
        Task second = new Task("READ   book");
        assertTrue(first.isDuplicateOf(second));
    }

    @Test
    public void isDuplicateOf_differentDescription_returnsFalse() {
        Task first = new Task("read book");
        Task second = new Task("buy milk");
        assertFalse(first.isDuplicateOf(second));
    }

    @Test
    public void isDuplicateOf_completionStatusIgnored_returnsTrue() {
        Task first = new Task("read book");
        Task second = new Task("read book");
        second.markAsDone();
        assertTrue(first.isDuplicateOf(second));
    }

    @Test
    public void isDuplicateOf_sameDescriptionDifferentTaskType_returnsTrue() {
        // Different concrete types with the same wording are still flagged,
        // even though a Deadline carries a date and a Todo does not.
        Task todo = new Todo("read book");
        Task deadline = new Deadline("read book", LocalDate.of(2019, 10, 15));
        assertTrue(todo.isDuplicateOf(deadline));
    }

    // ----- markAsDone / markAsNotDone -----

    @Test
    public void markAsNotDone_afterMarkAsDone_returnsToNotDone() {
        Task task = new Task("read book");
        task.markAsDone();
        task.markAsNotDone();
        assertEquals(" ", task.getStatusIcon());
    }

    @Test
    public void markAsDone_calledTwice_staysDone() {
        // Marking a task done is idempotent: a second call must not "toggle" it back.
        Task task = new Task("read book");
        task.markAsDone();
        task.markAsDone();
        assertEquals("X", task.getStatusIcon());
    }

    @Test
    public void markAsNotDone_onNewTask_staysNotDone() {
        Task task = new Task("read book");
        task.markAsNotDone();
        assertEquals(" ", task.getStatusIcon());
    }

    // ----- toString -----

    @Test
    public void toString_newTask_showsTypeMarkerAndBlankStatus() {
        Task task = new Task("read book");
        assertEquals("[T][ ] read book", task.toString());
    }

    @Test
    public void toString_doneTask_showsXStatus() {
        Task task = new Task("read book");
        task.markAsDone();
        assertEquals("[T][X] read book", task.toString());
    }

    // ----- toFileString -----

    @Test
    public void toFileString_newTask_usesPipeSeparatedFieldsWithZeroStatus() {
        Task task = new Task("read book");
        assertEquals("T | 0 | read book", task.toFileString());
    }

    @Test
    public void toFileString_doneTask_usesOneStatus() {
        Task task = new Task("read book");
        task.markAsDone();
        assertEquals("T | 1 | read book", task.toFileString());
    }
}
