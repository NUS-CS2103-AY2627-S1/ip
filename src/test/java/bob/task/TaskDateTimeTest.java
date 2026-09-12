package bob.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;

/** Tests task date-time construction and optional time behavior. */
class TaskDateTimeTest {

    @Test
    void constructor_dateAndTime_preservesDateAndTime() {
        LocalDate date = LocalDate.of(2019, 12, 2);
        LocalTime time = LocalTime.of(18, 5);
        TaskDateTime taskDateTime = new TaskDateTime(date, time);

        assertEquals(date, taskDateTime.getDate());
        assertEquals(time, taskDateTime.getTime().orElseThrow());
    }

    @Test
    void constructor_dateOnly_returnsEmptyTime() {
        TaskDateTime taskDateTime = new TaskDateTime(LocalDate.of(2019, 12, 2), null);

        assertEquals(LocalDate.of(2019, 12, 2), taskDateTime.getDate());
        assertEquals(Optional.empty(), taskDateTime.getTime());
    }

    @Test
    void constructor_nullDate_assertionErrorThrown() {
        AssertionError exception = assertThrows(AssertionError.class,
                () -> new TaskDateTime(null, LocalTime.NOON));

        assertEquals("A task date-time must have a date", exception.getMessage());
    }
}
