package tasklist;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

public class MyDateTimeTest {

    @Test
    public void hasTime_dateTimeConstructor_returnsTrue() {
        LocalDateTime dt = LocalDateTime.of(2026, 8, 28, 14, 30);
        MyDateTime myDateTime = new MyDateTime(dt);
        assertTrue(myDateTime.hasTime());
    }

    @Test
    public void hasTime_dateConstructor_returnsFalse() {
        LocalDate d = LocalDate.of(2026, 8, 28);
        MyDateTime myDateTime = new MyDateTime(d);
        assertFalse(myDateTime.hasTime());
    }

    @Test
    public void toDisplayString_withTime_correctFormat() {
        LocalDateTime dt = LocalDateTime.of(2026, 8, 28, 14, 30);
        MyDateTime myDateTime = new MyDateTime(dt);
        assertEquals("Aug 28 2026, 2:30PM", myDateTime.toDisplayString());
    }

    @Test
    public void toDisplayString_dateOnly_correctFormat() {
        LocalDate d = LocalDate.of(2026, 8, 28);
        MyDateTime myDateTime = new MyDateTime(d);
        assertEquals("Aug 28 2026", myDateTime.toDisplayString());
    }

    @Test
    public void toFileString_withTime_correctFormat() {
        LocalDateTime dt = LocalDateTime.of(2026, 8, 28, 14, 30);
        MyDateTime myDateTime = new MyDateTime(dt);
        assertEquals("2026-08-28 1430", myDateTime.toFileString());
    }

    @Test
    public void toFileString_dateOnly_correctFormat() {
        LocalDate d = LocalDate.of(2026, 8, 28);
        MyDateTime myDateTime = new MyDateTime(d);
        assertEquals("2026-08-28", myDateTime.toFileString());
    }
}
