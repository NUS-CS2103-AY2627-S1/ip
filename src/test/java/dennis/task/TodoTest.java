package dennis.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import dennis.DennisException;

/**
 * Tests for {@link Todo}: description validation and the save-file / display
 * text. (The {@code '|'} guard itself is covered in {@code TaskTest}; here we
 * check that {@code Todo} actually applies it.)
 */
public class TodoTest {

    @Test
    public void constructor_validDescription_doesNotThrow()
            throws DennisException {
        new Todo("read book");
    }

    @Test
    public void constructor_blankDescription_throwsWithTodoMessage() {
        DennisException e = assertThrows(DennisException.class, () ->
                new Todo(""));
        assertEquals("I'm sorry, todo must contain a task.", e.getMessage());
    }

    @Test
    public void constructor_whitespaceOnlyDescription_throwsWithTodoMessage() {
        assertThrows(DennisException.class, () -> new Todo("   "));
    }

    @Test
    public void constructor_descriptionWithPipe_throwsSeparatorMessage() {
        DennisException e = assertThrows(DennisException.class, () ->
                new Todo("wash | iron"));
        assertEquals("A task description cannot contain the '|' character.",
                e.getMessage());
    }

    @Test
    public void toString_notDone_showsTypeAndEmptyBox() throws DennisException {
        assertEquals("[T][ ] read book", new Todo("read book").toString());
    }

    @Test
    public void toString_done_showsCheckedBox() throws DennisException {
        Todo t = new Todo("read book");
        t.markAsDone();
        assertEquals("[T][X] read book", t.toString());
    }

    @Test
    public void toFileFormat_notDone_usesTypeTagAndZeroFlag()
            throws DennisException {
        assertEquals("T | 0 | read book", new Todo("read book").toFileFormat());
    }

    @Test
    public void toFileFormat_done_usesOneFlag() throws DennisException {
        Todo t = new Todo("read book");
        t.markAsDone();
        assertEquals("T | 1 | read book", t.toFileFormat());
    }

    // --- equals / hashCode: used by TaskList to reject duplicates ------

    @Test
    public void equals_sameDescription_isTrue() throws DennisException {
        assertEquals(new Todo("read book"), new Todo("read book"));
    }

    @Test
    public void equals_differentDescription_isFalse() throws DennisException {
        assertNotEquals(new Todo("read book"), new Todo("write book"));
    }

    @Test
    public void equals_doneStatusIgnored() throws DennisException {
        Todo done = new Todo("read book");
        done.markAsDone();
        assertEquals(done, new Todo("read book"));
    }

    @Test
    public void equals_differentTaskType_isFalse() throws DennisException {
        assertNotEquals(new Todo("read book"), new Deadline("read book", "2019-12-01"));
    }

    @Test
    public void hashCode_equalTodos_haveSameHashCode() throws DennisException {
        assertEquals(new Todo("read book").hashCode(), new Todo("read book").hashCode());
    }
}
