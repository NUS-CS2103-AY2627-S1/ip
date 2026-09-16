package eve.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/** Tests {@link Deadline}'s own display, save-format, and date-matching behavior. */
public class DeadlineTest {
    private static final LocalDate BY = LocalDate.of(2019, 6, 6);

    @Test
    public void toString_prefixedWithDTypeIconAndDueDate() {
        assertEquals("[D][ ] return book (by: Jun 6 2019)", new Deadline("return book", BY).toString());
    }

    @Test
    public void toSaveFormat_startsWithDAndEndsWithIsoDate() {
        assertEquals("D | 0 | return book | 2019-06-06", new Deadline("return book", BY).toSaveFormat());
    }

    @Test
    public void occursOn_exactDueDate_true() {
        assertTrue(new Deadline("return book", BY).occursOn(BY));
    }

    @Test
    public void occursOn_differentDate_false() {
        assertFalse(new Deadline("return book", BY).occursOn(BY.plusDays(1)));
    }

    @Test
    public void getScheduleDate_isTheDueDate() {
        assertEquals(BY, new Deadline("return book", BY).getScheduleDate().orElseThrow());
    }
}
