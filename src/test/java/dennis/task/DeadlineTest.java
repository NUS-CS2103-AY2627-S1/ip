package dennis.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import dennis.DennisException;

/**
 * Tests for {@link Deadline}: constructor validation, the {@code occursOn}
 * date match, and the save-file / display text.
 */
public class DeadlineTest {

    private static final String DESC = "return book";
    private static final String BY = "2019-12-01";

    // --- constructor validation --------------------------------------

    @Test
    public void constructor_validArguments_doesNotThrow() throws DennisException {
        new Deadline(DESC, BY);
    }

    @Test
    public void constructor_blankDescription_throwsWithDeadlineMessage() {
        DennisException e = assertThrows(DennisException.class, () ->
                new Deadline("  ", BY));
        assertEquals("I'm sorry, deadline must contain a task.", e.getMessage());
    }

    @Test
    public void constructor_descriptionWithPipe_throwsSeparatorMessage() {
        DennisException e = assertThrows(DennisException.class, () ->
                new Deadline("a | b", BY));
        assertEquals("A task description cannot contain the '|' character.",
                e.getMessage());
    }

    @Test
    public void constructor_blankDueDate_throwsWithDeadlineMessage() {
        DennisException e = assertThrows(DennisException.class, () ->
                new Deadline(DESC, "   "));
        assertEquals("deadline must contain a date and time.", e.getMessage());
    }

    @Test
    public void constructor_unparseableDueDate_throwsDateFormatMessage() {
        DennisException e = assertThrows(DennisException.class, () ->
                new Deadline(DESC, "next friday"));
        assertEquals(
                "A deadline date must be a date in yyyy-MM-dd form, e.g. 2019-12-01.",
                e.getMessage());
    }

    // --- occursOn ---------------------------------------------------

    @Test
    public void occursOn_sameDate_isTrue() throws DennisException {
        assertTrue(new Deadline(DESC, BY).occursOn(LocalDate.of(2019, 12, 1)));
    }

    @Test
    public void occursOn_dayBefore_isFalse() throws DennisException {
        assertFalse(new Deadline(DESC, BY).occursOn(LocalDate.of(2019, 11, 30)));
    }

    @Test
    public void occursOn_dayAfter_isFalse() throws DennisException {
        assertFalse(new Deadline(DESC, BY).occursOn(LocalDate.of(2019, 12, 2)));
    }

    // --- text forms ----------------------------------------------

    @Test
    public void toString_notDone_showsTypeStatusDescriptionAndFormattedDate()
            throws DennisException {
        assertEquals("[D][ ] return book (by: Dec 01 2019)",
                new Deadline(DESC, BY).toString());
    }

    @Test
    public void toString_done_showsCheckedBox() throws DennisException {
        Deadline d = new Deadline(DESC, BY);
        d.markAsDone();
        assertEquals("[D][X] return book (by: Dec 01 2019)", d.toString());
    }

    @Test
    public void toFileFormat_notDone_usesZeroFlagAndIsoDate()
            throws DennisException {
        assertEquals("D | 0 | return book | 2019-12-01",
                new Deadline(DESC, BY).toFileFormat());
    }

    @Test
    public void toFileFormat_done_usesOneFlag() throws DennisException {
        Deadline d = new Deadline(DESC, BY);
        d.markAsDone();
        assertEquals("D | 1 | return book | 2019-12-01", d.toFileFormat());
    }

    // --- equals / hashCode: used by TaskList to reject duplicates ------

    @Test
    public void equals_sameDescriptionAndDate_isTrue() throws DennisException {
        assertEquals(new Deadline(DESC, BY), new Deadline(DESC, BY));
    }

    @Test
    public void equals_differentDescription_isFalse() throws DennisException {
        assertNotEquals(new Deadline(DESC, BY), new Deadline("other task", BY));
    }

    @Test
    public void equals_differentDate_isFalse() throws DennisException {
        assertNotEquals(new Deadline(DESC, BY), new Deadline(DESC, "2019-12-02"));
    }

    @Test
    public void hashCode_equalDeadlines_haveSameHashCode() throws DennisException {
        assertEquals(new Deadline(DESC, BY).hashCode(), new Deadline(DESC, BY).hashCode());
    }
}
