package bo.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Tests for {@link Deadline}.
 *
 * Focuses on:
 *  - formatDate, a static method with a specific output format that is easy to get subtly wrong
 *    (e.g. wrong month abbreviation, missing zero-padding, wrong year length)
 *  - toFileString, which must produce a stable, parseable save format
 *  - toString, which must include the [D] marker, the status, description and formatted date
 *  - getDueDate, to confirm the constructor stores the date without altering it
 */
public class DeadlineTest {

    // ---------- formatDate(LocalDate) ----------

    @Test
    public void formatDate_typicalDate_matchesExpectedPattern() {
        LocalDate date = LocalDate.of(2024, 3, 2);

        assertEquals("Mar 02 2024", Deadline.formatDate(date));
    }

    @Test
    public void formatDate_singleDigitDay_isZeroPadded() {
        LocalDate date = LocalDate.of(2024, 12, 5);

        assertEquals("Dec 05 2024", Deadline.formatDate(date));
    }

    @Test
    public void formatDate_endOfYear_formatsMonthCorrectly() {
        LocalDate date = LocalDate.of(2023, 1, 31);

        assertEquals("Jan 31 2023", Deadline.formatDate(date));
    }

    // ---------- toString() ----------

    @Test
    public void toString_incompleteTask_showsEmptyCheckboxAndFormattedDate() {
        Deadline deadline = new Deadline("submit report", LocalDate.of(2024, 3, 2));

        assertEquals("[D][ ] submit report (by: Mar 02 2024)", deadline.toString());
    }

    @Test
    public void toString_completedTask_showsFilledCheckbox() {
        Deadline deadline = new Deadline("submit report", LocalDate.of(2024, 3, 2));
        deadline.markAsDone();

        assertEquals("[D][X] submit report (by: Mar 02 2024)", deadline.toString());
    }

    // ---------- toFileString() ----------

    @Test
    public void toFileString_incompleteTask_usesZeroStatus() {
        Deadline deadline = new Deadline("submit report", LocalDate.of(2024, 3, 2));

        assertEquals("D | 0 | submit report | 2024-03-02", deadline.toFileString());
    }

    @Test
    public void toFileString_completedTask_usesOneStatus() {
        Deadline deadline = new Deadline("submit report", LocalDate.of(2024, 3, 2));
        deadline.markAsDone();

        assertEquals("D | 1 | submit report | 2024-03-02", deadline.toFileString());
    }

    // ---------- getDueDate() ----------

    @Test
    public void getDueDate_returnsSameDatePassedToConstructor() {
        LocalDate date = LocalDate.of(2025, 7, 15);
        Deadline deadline = new Deadline("renew passport", date);

        assertEquals(date, deadline.getDueDate());
        assertTrue(deadline.getDueDate().isEqual(date));
    }

    @Test
    public void reschedule_replacesDueDate() {
        Deadline deadline = new Deadline("renew passport", LocalDate.of(2025, 7, 15));

        deadline.reschedule(LocalDate.of(2025, 8, 20));

        assertEquals(LocalDate.of(2025, 8, 20), deadline.getDueDate());
    }
}
