package lumine.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import lumine.LumineException;

/**
 * Tests {@link Task}.
 *
 * <p>Task is the base class for all task types. Its most critical logic is
 * {@code escapeStorageField}, which ensures that pipe (|), backslash (\),
 * and newline characters are escaped before being written to the save file.
 * The other methods tested here are foundational display and state logic
 * inherited by every subclass.
 */
class TaskTest {

    // -------------------------------------------------------------------------
    // escapeStorageField  (static helper — accessed via a concrete subclass)
    // -------------------------------------------------------------------------

    // We use Todo (the simplest subclass) so we can construct a Task and
    // call toStorageString(), which in turn calls escapeStorageField internally.
    // For the static method itself we reach it via the package-visible
    // inheritance; testing the output of toStorageString() covers it end-to-end.

    @Test
    void escapeStorageField_plainText_unchanged() {
        Todo todo = new Todo("buy groceries");
        assertEquals("T | 0 | buy groceries", todo.toStorageString());
    }

    @Test
    void escapeStorageField_pipeCharacter_escapedWithBackslash() {
        Todo todo = new Todo("a|b");
        assertEquals("T | 0 | a\\|b", todo.toStorageString());
    }

    @Test
    void escapeStorageField_backslash_doubledUp() {
        Todo todo = new Todo("a\\b");
        assertEquals("T | 0 | a\\\\b", todo.toStorageString());
    }

    @Test
    void escapeStorageField_newline_escapedAsBackslashN() {
        Todo todo = new Todo("line1\nline2");
        assertEquals("T | 0 | line1\\nline2", todo.toStorageString());
    }

    @Test
    void escapeStorageField_carriageReturn_escapedAsBackslashR() {
        Todo todo = new Todo("line1\rline2");
        assertEquals("T | 0 | line1\\rline2", todo.toStorageString());
    }

    @Test
    void escapeStorageField_multipleSpecialChars_allEscaped() {
        Todo todo = new Todo("a|b\\c\nd");
        assertEquals("T | 0 | a\\|b\\\\c\\nd", todo.toStorageString());
    }

    // -------------------------------------------------------------------------
    // toStorageString
    // -------------------------------------------------------------------------

    @Test
    void toStorageString_undoneTodo_containsZeroFlag() {
        Todo todo = new Todo("read book");
        assertEquals("T | 0 | read book", todo.toStorageString());
    }

    @Test
    void toStorageString_doneTodo_containsOneFlag() {
        Todo todo = new Todo("read book");
        todo.markDone();
        assertEquals("T | 1 | read book", todo.toStorageString());
    }

    @Test
    void toStorageString_undoneTodo_containsNoFlag() {
        Todo todo = new Todo("read book");
        todo.markDone();
        assertEquals("T | 1 | read book", todo.toStorageString());
        todo.markUndone();
        assertEquals("T | 0 | read book", todo.toStorageString());
    }

    // -------------------------------------------------------------------------
    // toString
    // -------------------------------------------------------------------------

    @Test
    void toString_undoneTask_showsBlankStatusIcon() {
        Todo todo = new Todo("read book");
        assertEquals("[T][ ] read book", todo.toString());
    }

    @Test
    void toString_doneTask_showsXStatusIcon() {
        Todo todo = new Todo("read book");
        todo.markDone();
        assertEquals("[T][X] read book", todo.toString());
    }

    @Test
    void toString_undoneTask_hideXStatusIcon() {
        Todo todo = new Todo("read book");
        todo.markDone();
        assertEquals("[T][X] read book", todo.toString());
        todo.markUndone();
        assertEquals("[T][ ] read book", todo.toString());
    }

    // -------------------------------------------------------------------------
    // markDone / markUndone
    // -------------------------------------------------------------------------

    @Test
    void markDone_undoneTask_becomesMarkedDone() {
        Todo todo = new Todo("read book");
        todo.markDone();
        assertEquals("X", todo.getStatusIcon());
    }

    @Test
    void markUndone_doneTask_becomesMarkedUndone() {
        Todo todo = new Todo("read book");
        todo.markDone();
        todo.markUndone();
        assertEquals(" ", todo.getStatusIcon());
    }

    @Test
    void isDueOn_todo_returnsFalse() {
        Todo todo = new Todo("read book");

        assertFalse(todo.isDueOn(LocalDate.of(2026, 1, 1)));
    }

    // -------------------------------------------------------------------------
    // requireText (tested indirectly via constructor)
    // -------------------------------------------------------------------------

    @Test
    void constructor_nullDescription_throwsLumineException() {
        assertThrows(LumineException.class, () -> new Todo(null));
    }

    @Test
    void constructor_blankDescription_throwsLumineException() {
        assertThrows(LumineException.class, () -> new Todo("   "));
    }
}
