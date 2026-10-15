package lumine.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import lumine.LumineException;

/**
 * Tests {@link Deadline}.
 *
 * <p>The deadline value supports three representations:
 * <ol>
 *   <li>Plain text  (stored and displayed as-is)</li>
 *   <li>Date only   "yyyy MM dd" (displayed as "MMM dd yyyy")</li>
 *   <li>Date + time "yyyy MM dd HHmm" (displayed as "MMM dd yyyy HH:mm")</li>
 * </ol>
 * The integration between {@link Deadline} and {@link TaskDateTime} is verified
 * through {@code toString}, {@code toStorageString}, {@code getDueDate}, and {@code isDueOn}.
 */
class DeadlineTest {

    // -------------------------------------------------------------------------
    // toString
    // -------------------------------------------------------------------------

    @Test
    void toString_plainTextBy_displayedAsIs() {
        Deadline d = new Deadline("test", "Monday 5pm");
        assertEquals("[D][ ] test (by: Monday 5pm)", d.toString());
    }

    @Test
    void toString_dateOnlyBy_formatsToMmmDdYyyy() {
        Deadline d = new Deadline("test", "2026 01 01");
        assertEquals("[D][ ] test (by: Jan 01 2026)", d.toString());
    }

    @Test
    void toString_dateTimeBy_formatsToMmmDdYyyyHhMm() {
        Deadline d = new Deadline("test", "2026 01 01 1200");
        assertEquals("[D][ ] test (by: Jan 01 2026 12:00)", d.toString());
    }

    @Test
    void toString_doneDeadline_showsXStatusIcon() {
        Deadline d = new Deadline("test", "Monday");
        d.markDone();
        assertEquals("[D][X] test (by: Monday)", d.toString());
    }

    @Test
    void toString_undoneDeadline_hideXStatusIcon() {
        Deadline d = new Deadline("test", "Monday");
        d.markDone();
        assertEquals("[D][X] test (by: Monday)", d.toString());
        d.markUndone();
        assertEquals("[D][ ] test (by: Monday)", d.toString());
    }

    // -------------------------------------------------------------------------
    // toStorageString
    // -------------------------------------------------------------------------

    @Test
    void toStorageString_plainTextBy_storedAsIs() {
        Deadline d = new Deadline("test", "Monday 2pm");
        assertEquals("D | 0 | test | Monday 2pm", d.toStorageString());
    }

    @Test
    void toStorageString_dateOnlyBy_storedInYyyyMmDdFormat() {
        Deadline d = new Deadline("test", "2026 01 01");
        assertEquals("D | 0 | test | 2026 01 01", d.toStorageString());
    }

    @Test
    void toStorageString_dateTimeBy_storedInYyyyMmDdHhmmFormat() {
        Deadline d = new Deadline("test", "2026 01 01 1200");
        assertEquals("D | 0 | test | 2026 01 01 1200", d.toStorageString());
    }

    @Test
    void toStorageString_doneDeadline_containsOneFlag() {
        Deadline d = new Deadline("test", "Monday");
        d.markDone();
        assertEquals("D | 1 | test | Monday", d.toStorageString());
    }

    @Test
    void toStorageString_undoneDeadline_containsNoFlag() {
        Deadline d = new Deadline("test", "Monday");
        d.markDone();
        assertEquals("D | 1 | test | Monday", d.toStorageString());
        d.markUndone();
        assertEquals("D | 0 | test | Monday", d.toStorageString());
    }

    // -------------------------------------------------------------------------
    // getDueDate
    // -------------------------------------------------------------------------

    @Test
    void getDueDate_plainTextBy_returnsNull() {
        Deadline d = new Deadline("test", "Monday 2pm");
        assertNull(d.getDueDate());
    }

    @Test
    void getDueDate_dateOnlyBy_returnsCorrectLocalDate() {
        Deadline d = new Deadline("test", "2026 01 01");
        assertEquals(LocalDate.of(2026, 1, 1), d.getDueDate());
    }

    @Test
    void getDueDate_dateTimeBy_returnsDatePortionOnly() {
        Deadline d = new Deadline("test", "2026 01 01 1200");
        assertEquals(LocalDate.of(2026, 1, 1), d.getDueDate());
    }

    @Test
    void isDueOn_matchingDate_returnsTrue() {
        Deadline deadline = new Deadline("test", "2026 01 01 1200");

        assertTrue(deadline.isDueOn(LocalDate.of(2026, 1, 1)));
    }

    @Test
    void isDueOn_differentOrUnstructuredDate_returnsFalse() {
        Deadline datedDeadline = new Deadline("dated", "2026 01 01");
        Deadline freeTextDeadline = new Deadline("free text", "Monday");

        assertFalse(datedDeadline.isDueOn(LocalDate.of(2026, 1, 2)));
        assertFalse(freeTextDeadline.isDueOn(LocalDate.of(2026, 1, 1)));
    }

    // -------------------------------------------------------------------------
    // constructor validation
    // -------------------------------------------------------------------------

    @Test
    void constructor_emptyBy_throwsLumineException() {
        assertThrows(LumineException.class, () -> new Deadline("test", "  "));
    }

    @Test
    void constructor_impossibleByDate_throwsDateNotFound() {
        LumineException exception = assertThrows(LumineException.class, () -> new Deadline("test", "2026 02 30"));

        assertEquals("Date not found :<.\nPlease enter a valid calendar date or time.", exception.getMessage());
    }

    @Test
    void constructor_nullDescription_throwsLumineException() {
        assertThrows(LumineException.class, () -> new Deadline(null, "Monday"));
    }
}
