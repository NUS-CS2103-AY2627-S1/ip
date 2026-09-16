package anaconda.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.ResourceLock;

/**
 * Tests deadline date retention and English date formatting independent of the machine's locale.
 */
public class DeadlineTest {
    @Test
    public void toString_incompleteAndCompletedDeadline_showsFormattedDate() {
        LocalDate date = LocalDate.of(2026, 8, 9);
        Deadline task = new Deadline("return book", date);
        assertEquals(date, task.getBy());
        assertEquals(date, task.getEndDate());
        assertEquals("[D][ ] return book (by: Aug 09 2026)", task.toString());
        task.markAsDone();
        assertEquals("[D][X] return book (by: Aug 09 2026)", task.toString());
    }

    @Test
    @ResourceLock("DEFAULT_LOCALE")
    public void toString_nonEnglishLocale_keepsEnglishMonthName() {
        Locale original = Locale.getDefault(Locale.Category.FORMAT);
        try {
            Locale.setDefault(Locale.Category.FORMAT, Locale.FRENCH);
            assertEquals("[D][ ] leap day (by: Feb 29 2024)",
                    new Deadline("leap day", LocalDate.of(2024, 2, 29)).toString());
        } finally {
            Locale.setDefault(Locale.Category.FORMAT, original);
        }
    }
}
