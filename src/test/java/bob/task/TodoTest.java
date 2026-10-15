package bob.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import bob.BobException;

/**
 * Tests {@link Todo}: the letter it answers with, and the behavior it takes
 * unchanged from {@link Task}.
 *
 * <p>{@code Todo} declares little of its own, so most of what is checked here is
 * inherited. It is still worth checking through a {@code Todo} rather than
 * skipped as "someone else's code": a todo is the simplest concrete task, so it
 * is the cheapest place to pin down the parts every task shares — how a task is
 * shown, what it saves, and how marking it done changes both. It is also the
 * one kind of task that has no date, and the promise that a dateless task is
 * left out of the date commands rather than counted as due at the beginning of
 * time is a promise {@code Todo} is making.
 */
public class TodoTest {

    /** A day used wherever a test needs some day to ask about. */
    private static final LocalDate SOME_DAY = LocalDate.of(2026, 12, 2);

    @Test
    public void getTypeIcon_anyTodo_returnsT() {
        Todo todo = new Todo("read book");

        assertEquals("T", todo.getTypeIcon());
        // Storage recognizes a saved todo by this same constant, so the two
        // must not be allowed to drift apart.
        assertEquals(Todo.TYPE_ICON, todo.getTypeIcon());
    }

    @Test
    public void getStatusIcon_newTodo_returnsBlank() {
        // A task the user has just mentioned is something still to do.
        assertEquals(" ", new Todo("read book").getStatusIcon());
    }

    @Test
    public void getStatusIcon_markedAsDone_returnsX() {
        Todo todo = new Todo("read book");
        todo.markAsDone();

        assertEquals("X", todo.getStatusIcon());
    }

    @Test
    public void getStatusIcon_markedAsNotDoneAgain_returnsBlank() {
        Todo todo = new Todo("read book");
        todo.markAsDone();
        todo.markAsNotDone();

        assertEquals(" ", todo.getStatusIcon());
    }

    @Test
    public void markAsDone_calledTwice_staysDone() {
        Todo todo = new Todo("read book");
        todo.markAsDone();
        todo.markAsDone();

        assertEquals("X", todo.getStatusIcon());
    }

    @Test
    public void toString_newTodo_showsTypeStatusAndDescription() {
        assertEquals("[T][ ] read book", new Todo("read book").toString());
    }

    @Test
    public void toString_doneTodo_showsCrossInStatusBox() {
        Todo todo = new Todo("read book");
        todo.markAsDone();

        assertEquals("[T][X] read book", todo.toString());
    }

    @Test
    public void toString_todo_addsNothingAfterTheDescription() {
        // Unlike a deadline or an event, a todo has nothing more to show, so
        // nothing should be appended to the shared form.
        assertEquals("[T][ ] borrow book", new Todo("borrow book").toString());
    }

    @Test
    public void toSaveFields_newTodo_returnsTypeNotDoneFlagAndDescription() {
        List<String> fields = new Todo("read book").toSaveFields();

        assertEquals(List.of(Todo.TYPE_ICON, Task.NOT_DONE_FLAG, "read book"), fields);
    }

    @Test
    public void toSaveFields_doneTodo_returnsDoneFlag() {
        Todo todo = new Todo("read book");
        todo.markAsDone();

        assertEquals(List.of(Todo.TYPE_ICON, Task.DONE_FLAG, "read book"), todo.toSaveFields());
    }

    @Test
    public void toSaveFields_descriptionWithSeparator_keptWhole() {
        // Splitting a description that contains the save file's separator is
        // Storage's problem; the task hands the text over untouched.
        List<String> fields = new Todo("read book | volume 2").toSaveFields();

        assertEquals("read book | volume 2", fields.get(2));
    }

    @Test
    public void getScheduledDate_anyTodo_returnsEmpty() {
        assertTrue(new Todo("read book").getScheduledDate().isEmpty());
    }

    @Test
    public void dateChecks_anyTodo_allReturnFalse() {
        Todo todo = new Todo("read book");

        // A todo is pinned to no day, so it belongs in no day's listing, and is
        // neither overdue nor still to come.
        assertFalse(todo.occursOn(SOME_DAY));
        assertFalse(todo.isBefore(SOME_DAY));
        assertFalse(todo.isAfter(SOME_DAY));
    }

    @Test
    public void matchesKeyword_wordInTheDescription_returnsTrue() {
        Todo todo = new Todo("read book");

        assertTrue(todo.matchesKeyword("read"));
        assertTrue(todo.matchesKeyword("book"));
    }

    @Test
    public void matchesKeyword_wordNotInTheDescription_returnsFalse() {
        assertFalse(new Todo("read book").matchesKeyword("swim"));
    }

    @Test
    public void matchesKeyword_differentCase_returnsTrue() {
        Todo todo = new Todo("Read Book");

        // Neither side's capitalization is the user's problem when searching.
        assertTrue(todo.matchesKeyword("read book"));
        assertTrue(new Todo("read book").matchesKeyword("BOOK"));
    }

    @Test
    public void matchesKeyword_extraSpacesOnEitherSide_returnsTrue() {
        // The duplicate check ignores extra spaces, so a search must too: adding
        // "sports club" a second time is refused for matching the task below, and a
        // search that missed it would leave the user unable to find what they have.
        Todo todo = new Todo("join sports  club");

        assertTrue(todo.matchesKeyword("sports club"));
        assertTrue(new Todo("join sports club").matchesKeyword("sports   club"));
        assertTrue(todo.matchesKeyword(" sports club "));
    }

    @Test
    public void matchesKeyword_partOfAWord_returnsTrue() {
        // Half a word is a search someone means, so the keyword is looked for
        // anywhere in the description rather than as a whole word.
        assertTrue(new Todo("visit the bookshop").matchesKeyword("book"));
        assertTrue(new Todo("read book").matchesKeyword("ad bo"));
    }

    @Test
    public void matchesKeyword_todoFoundUnlikeInTheDateListings_returnsTrue() {
        Todo todo = new Todo("read book");

        // A todo has no date and so is left out of on, before and after; it has
        // a description like any other task, so find must not leave it out too.
        assertFalse(todo.occursOn(SOME_DAY));
        assertTrue(todo.matchesKeyword("read"));
    }

    @Test
    public void withEdit_newDescription_descriptionChanged() throws BobException {
        Task edited = new Todo("read book").withEdit(descriptionEdit("read library book"));

        assertEquals("[T][ ] read library book", edited.toString());
    }

    @Test
    public void withEdit_doneTodo_staysDone() throws BobException {
        Todo todo = new Todo("read book");
        todo.markAsDone();

        Task edited = todo.withEdit(descriptionEdit("read library book"));

        // Rewording a task is not the same as undoing it.
        assertEquals("[T][X] read library book", edited.toString());
    }

    @Test
    public void withEdit_anyEdit_originalUnchanged() throws BobException {
        Todo todo = new Todo("read book");

        todo.withEdit(descriptionEdit("read library book"));

        // A copy is built, so the task itself is left exactly as it was.
        assertEquals("[T][ ] read book", todo.toString());
    }

    @Test
    public void withEdit_anyDate_exceptionThrown() throws BobException {
        Todo todo = new Todo("read book");
        TaskDateTime date = TaskDateTime.parse("2026-12-02");

        BobException exception = assertThrows(BobException.class, () ->
                todo.withEdit(new TaskEdit(null, date, null, null)));

        assertEquals("A todo has no dates to change, only its description.", exception.getMessage());
        assertThrows(BobException.class, () -> todo.withEdit(new TaskEdit(null, null, date, null)));
        assertThrows(BobException.class, () -> todo.withEdit(new TaskEdit("read", null, null, date)));
    }

    @Test
    public void isSameTaskAs_descriptionDifferingInCapitalsAndSpaces_true() {
        Todo todo = new Todo("read book");
        Todo retyped = new Todo("Read   BOOK");

        assertTrue(todo.isSameTaskAs(retyped));
        assertTrue(retyped.isSameTaskAs(todo));
        // Only the comparison is loose: each keeps its description as typed.
        assertEquals("[T][ ] Read   BOOK", retyped.toString());
    }

    @Test
    public void isSameTaskAs_oneOfTheTwoDone_stillTrue() {
        Todo done = new Todo("read book");
        done.markAsDone();

        // Marking a task done does not make it a different task.
        assertTrue(done.isSameTaskAs(new Todo("read book")));
    }

    @Test
    public void isSameTaskAs_differentWordsOrKind_false() throws BobException {
        Todo todo = new Todo("read book");
        Deadline deadline = new Deadline("read book", TaskDateTime.parse("2026-12-02"));

        assertFalse(todo.isSameTaskAs(new Todo("read books")));
        // Spaces between words are only ignored when there is at least one.
        assertFalse(todo.isSameTaskAs(new Todo("readbook")));
        // However alike the words, a deadline is a different task from a todo.
        assertFalse(todo.isSameTaskAs(deadline));
        assertFalse(deadline.isSameTaskAs(todo));
    }

    /** Returns an edit that changes nothing but the description, to {@code description}. */
    private static TaskEdit descriptionEdit(String description) {
        return new TaskEdit(description, null, null, null);
    }
}
