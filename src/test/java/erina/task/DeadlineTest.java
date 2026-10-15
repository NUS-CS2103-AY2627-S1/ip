package erina.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.Locale;

import org.junit.jupiter.api.Test;

/**
 * Tests for {@link Deadline}, focusing on how its date is shown and saved.
 *
 * <p>The two formats are deliberately different: the display form is for
 * people, the save form must round-trip through {@code LocalDate.parse}.
 */
public class DeadlineTest {
    @Test
    public void toString_showsDateInReadableForm() {
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 10, 15));
        assertEquals("[D][ ] return book (by: Oct 15 2019)", deadline.toString());
    }

    @Test
    public void toString_septemberOnSingaporeLocale_stillUsesThreeLetterMonth() {
        Locale original = Locale.getDefault();
        try {
            // English (Singapore) abbreviates September as "Sept" unless the
            // formatter's locale is fixed.
            Locale.setDefault(Locale.forLanguageTag("en-SG"));
            Deadline deadline = new Deadline("submit iP", LocalDate.of(2026, 9, 18));
            assertEquals("[D][ ] submit iP (by: Sep 18 2026)", deadline.toString());
        } finally {
            // Restore the default so other tests are not affected.
            Locale.setDefault(original);
        }
    }

    @Test
    public void toSaveString_keepsIsoDateAndDoneFlag() {
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 10, 15));
        deadline.markAsDone();
        assertEquals("D | 1 | return book | 2019-10-15", deadline.toSaveString());
    }

    @Test
    public void isSameTask_sameDetailsDifferentCapitals_isTrue() {
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 10, 15));
        Deadline other = new Deadline("Return Book", LocalDate.of(2019, 10, 15));
        assertTrue(deadline.isSameTask(other));
    }

    @Test
    public void isSameTask_differentDate_isFalse() {
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 10, 15));
        Deadline other = new Deadline("return book", LocalDate.of(2019, 10, 16));
        assertFalse(deadline.isSameTask(other));
    }
}
