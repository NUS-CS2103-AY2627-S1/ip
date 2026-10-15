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
 * Tests for {@link Event}: constructor validation, the inclusive-range
 * {@code occursOn} logic, and the save-file / display text.
 */
public class EventTest {

    private static final String DESC = "project meeting";
    private static final String FROM = "2019-12-02";
    private static final String TO = "2019-12-05";

    private static Event sampleEvent() throws DennisException {
        return new Event(DESC, FROM, TO);
    }

    // --- constructor validation --------------------------------------

    @Test
    public void constructor_validArguments_doesNotThrow() throws DennisException {
        sampleEvent();
    }

    @Test
    public void constructor_blankDescription_throwsWithEventMessage() {
        DennisException e = assertThrows(DennisException.class, () ->
                new Event(" ", FROM, TO));
        assertEquals("I'm sorry, event must contain a task.", e.getMessage());
    }

    @Test
    public void constructor_descriptionWithPipe_throwsSeparatorMessage() {
        DennisException e = assertThrows(DennisException.class, () ->
                new Event("a|b", FROM, TO));
        assertEquals("A task description cannot contain the '|' character.",
                e.getMessage());
    }

    @Test
    public void constructor_blankStart_throwsWithStartMessage() {
        DennisException e = assertThrows(DennisException.class, () ->
                new Event(DESC, "  ", TO));
        assertEquals("The start of an event cannot be empty.", e.getMessage());
    }

    @Test
    public void constructor_blankEnd_throwsWithEndMessage() {
        DennisException e = assertThrows(DennisException.class, () ->
                new Event(DESC, FROM, ""));
        assertEquals("The end of an event cannot be empty.", e.getMessage());
    }

    @Test
    public void constructor_unparseableStart_throwsDateFormatMessage() {
        DennisException e = assertThrows(DennisException.class, () ->
                new Event(DESC, "soon", TO));
        assertEquals(
                "An event start must be a date in yyyy-MM-dd form, e.g. 2019-12-01.",
                e.getMessage());
    }

    @Test
    public void constructor_unparseableEnd_throwsDateFormatMessage() {
        DennisException e = assertThrows(DennisException.class, () ->
                new Event(DESC, FROM, "later"));
        assertEquals(
                "An event end must be a date in yyyy-MM-dd form, e.g. 2019-12-01.",
                e.getMessage());
    }

    @Test
    public void constructor_startAfterEnd_throwsOrderMessage() {
        DennisException e = assertThrows(DennisException.class, () ->
                new Event(DESC, "2019-12-05", "2019-12-02"));
        assertEquals("The event's start date must not be after its end date.",
                e.getMessage());
    }

    @Test
    public void constructor_fromEqualsTo_doesNotThrow() throws DennisException {
        new Event(DESC, "2019-12-02", "2019-12-02");
    }

    // --- occursOn: inclusive on both ends ---------------------------

    @Test
    public void occursOn_dayBeforeStart_isFalse() throws DennisException {
        assertFalse(sampleEvent().occursOn(LocalDate.of(2019, 12, 1)));
    }

    @Test
    public void occursOn_startDate_isTrue() throws DennisException {
        assertTrue(sampleEvent().occursOn(LocalDate.of(2019, 12, 2)));
    }

    @Test
    public void occursOn_dateInsideRange_isTrue() throws DennisException {
        assertTrue(sampleEvent().occursOn(LocalDate.of(2019, 12, 3)));
    }

    @Test
    public void occursOn_endDate_isTrue() throws DennisException {
        assertTrue(sampleEvent().occursOn(LocalDate.of(2019, 12, 5)));
    }

    @Test
    public void occursOn_dayAfterEnd_isFalse() throws DennisException {
        assertFalse(sampleEvent().occursOn(LocalDate.of(2019, 12, 6)));
    }

    // --- text forms ---------------------------------------------

    @Test
    public void toString_notDone_showsBothFormattedDates() throws DennisException {
        assertEquals(
                "[E][ ] project meeting (from: Dec 02 2019 to: Dec 05 2019)",
                sampleEvent().toString());
    }

    @Test
    public void toFileFormat_notDone_usesZeroFlagAndIsoDates()
            throws DennisException {
        assertEquals("E | 0 | project meeting | 2019-12-02 | 2019-12-05",
                sampleEvent().toFileFormat());
    }

    @Test
    public void toFileFormat_done_usesOneFlag() throws DennisException {
        Event e = sampleEvent();
        e.markAsDone();
        assertEquals("E | 1 | project meeting | 2019-12-02 | 2019-12-05",
                e.toFileFormat());
    }

    // --- equals / hashCode: used by TaskList to reject duplicates ------

    @Test
    public void equals_sameFields_isTrue() throws DennisException {
        assertEquals(sampleEvent(), sampleEvent());
    }

    @Test
    public void equals_differentDescription_isFalse() throws DennisException {
        assertNotEquals(sampleEvent(), new Event("other meeting", FROM, TO));
    }

    @Test
    public void equals_differentFrom_isFalse() throws DennisException {
        assertNotEquals(sampleEvent(), new Event(DESC, "2019-12-03", TO));
    }

    @Test
    public void equals_differentTo_isFalse() throws DennisException {
        assertNotEquals(sampleEvent(), new Event(DESC, FROM, "2019-12-06"));
    }

    @Test
    public void hashCode_equalEvents_haveSameHashCode() throws DennisException {
        assertEquals(sampleEvent().hashCode(), sampleEvent().hashCode());
    }
}
