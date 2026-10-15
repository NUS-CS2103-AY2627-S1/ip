package amigobot.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Tests for Deadline's toString formatting and getter behavior.
 */
public class DeadlineTest {

    @Test
    public void toString_withLocalDate_formatsAsMmmDYyyy() {
        Deadline d = new Deadline("return book", LocalDate.of(2025, 12, 2));
        assertEquals("[D][ ] return book (by: Dec 2 2025)", d.toString());
    }

    @Test
    public void toString_withStringDate_displaysStringAsIs() {
        Deadline d = new Deadline("return book", "next Tuesday");
        assertEquals("[D][ ] return book (by: next Tuesday)", d.toString());
    }

    @Test
    public void toString_markedDone_showsX() {
        Deadline d = new Deadline("submit report", LocalDate.of(2025, 1, 15));
        d.markAsDone();
        assertEquals("[D][X] submit report (by: Jan 15 2025)", d.toString());
    }

    @Test
    public void getByDate_withLocalDate_returnsDate() {
        LocalDate date = LocalDate.of(2025, 6, 1);
        Deadline d = new Deadline("homework", date);
        assertEquals(date, d.getByDate());
        assertNull(d.getByString());
    }

    @Test
    public void getByString_withString_returnsString() {
        Deadline d = new Deadline("homework", "tomorrow");
        assertEquals("tomorrow", d.getByString());
        assertNull(d.getByDate());
    }
}
