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
 * Tests for {@link WithinPeriodTask}: constructor validation (including the
 * from-after-to rejection, which mirrors {@link Event}'s), the
 * inclusive-range {@code occursOn} logic, and the save-file / display text.
 */
public class WithinPeriodTaskTest {

    private static final String DESC = "collect certificate";
    private static final String FROM = "2019-01-15";
    private static final String TO = "2019-01-25";

    private static WithinPeriodTask sampleTask() throws DennisException {
        return new WithinPeriodTask(DESC, FROM, TO);
    }

    // --- constructor validation --------------------------------------

    @Test
    public void constructor_validArguments_doesNotThrow() throws DennisException {
        sampleTask();
    }

    @Test
    public void constructor_fromEqualsTo_doesNotThrow() throws DennisException {
        // A one-day window is a legitimate period, not an inverted one.
        new WithinPeriodTask(DESC, FROM, FROM);
    }

    @Test
    public void constructor_blankDescription_throwsWithWithinMessage() {
        DennisException e = assertThrows(DennisException.class, () ->
                new WithinPeriodTask(" ", FROM, TO));
        assertEquals("I'm sorry, within must contain a task.", e.getMessage());
    }

    @Test
    public void constructor_descriptionWithPipe_throwsSeparatorMessage() {
        DennisException e = assertThrows(DennisException.class, () ->
                new WithinPeriodTask("a|b", FROM, TO));
        assertEquals("A task description cannot contain the '|' character.",
                e.getMessage());
    }

    @Test
    public void constructor_blankStart_throwsWithStartMessage() {
        DennisException e = assertThrows(DennisException.class, () ->
                new WithinPeriodTask(DESC, "  ", TO));
        assertEquals("The start of the period cannot be empty.", e.getMessage());
    }

    @Test
    public void constructor_blankEnd_throwsWithEndMessage() {
        DennisException e = assertThrows(DennisException.class, () ->
                new WithinPeriodTask(DESC, FROM, ""));
        assertEquals("The end of the period cannot be empty.", e.getMessage());
    }

    @Test
    public void constructor_unparseableStart_throwsDateFormatMessage() {
        DennisException e = assertThrows(DennisException.class, () ->
                new WithinPeriodTask(DESC, "soon", TO));
        assertEquals(
                "A period start must be a date in yyyy-MM-dd form, e.g. 2019-12-01.",
                e.getMessage());
    }

    @Test
    public void constructor_unparseableEnd_throwsDateFormatMessage() {
        DennisException e = assertThrows(DennisException.class, () ->
                new WithinPeriodTask(DESC, FROM, "later"));
        assertEquals(
                "A period end must be a date in yyyy-MM-dd form, e.g. 2019-12-01.",
                e.getMessage());
    }

    @Test
    public void constructor_startAfterEnd_throwsOrderMessage() {
        DennisException e = assertThrows(DennisException.class, () ->
                new WithinPeriodTask(DESC, TO, FROM));
        assertEquals("The period's start date must not be after its end date.",
                e.getMessage());
    }

    // --- occursOn: inclusive on both ends ---------------------------

    @Test
    public void occursOn_dayBeforeStart_isFalse() throws DennisException {
        assertFalse(sampleTask().occursOn(LocalDate.of(2019, 1, 14)));
    }

    @Test
    public void occursOn_startDate_isTrue() throws DennisException {
        assertTrue(sampleTask().occursOn(LocalDate.of(2019, 1, 15)));
    }

    @Test
    public void occursOn_dateInsideRange_isTrue() throws DennisException {
        assertTrue(sampleTask().occursOn(LocalDate.of(2019, 1, 20)));
    }

    @Test
    public void occursOn_endDate_isTrue() throws DennisException {
        assertTrue(sampleTask().occursOn(LocalDate.of(2019, 1, 25)));
    }

    @Test
    public void occursOn_dayAfterEnd_isFalse() throws DennisException {
        assertFalse(sampleTask().occursOn(LocalDate.of(2019, 1, 26)));
    }

    // --- text forms ---------------------------------------------

    @Test
    public void toString_notDone_showsBothFormattedDates() throws DennisException {
        assertEquals(
                "[W][ ] collect certificate (within: Jan 15 2019 to: Jan 25 2019)",
                sampleTask().toString());
    }

    @Test
    public void toFileFormat_notDone_usesZeroFlagAndIsoDates()
            throws DennisException {
        assertEquals("W | 0 | collect certificate | 2019-01-15 | 2019-01-25",
                sampleTask().toFileFormat());
    }

    @Test
    public void toFileFormat_done_usesOneFlag() throws DennisException {
        WithinPeriodTask task = sampleTask();
        task.markAsDone();
        assertEquals("W | 1 | collect certificate | 2019-01-15 | 2019-01-25",
                task.toFileFormat());
    }

    // --- equals / hashCode: used by TaskList to reject duplicates ------

    @Test
    public void equals_sameFields_isTrue() throws DennisException {
        assertEquals(sampleTask(), sampleTask());
    }

    @Test
    public void equals_differentDescription_isFalse() throws DennisException {
        assertNotEquals(sampleTask(), new WithinPeriodTask("other task", FROM, TO));
    }

    @Test
    public void equals_differentFrom_isFalse() throws DennisException {
        assertNotEquals(sampleTask(), new WithinPeriodTask(DESC, "2019-01-16", TO));
    }

    @Test
    public void equals_differentTo_isFalse() throws DennisException {
        assertNotEquals(sampleTask(), new WithinPeriodTask(DESC, FROM, "2019-01-24"));
    }

    @Test
    public void hashCode_equalTasks_haveSameHashCode() throws DennisException {
        assertEquals(sampleTask().hashCode(), sampleTask().hashCode());
    }
}
