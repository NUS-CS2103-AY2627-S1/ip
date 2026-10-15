package duncan.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.DateTimeException;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;

public class EventTest {
    @Test
    public void toFileFormat_notDone_correctFileFormat() {
        Event event = new Event("project fair", LocalDate.of(2019, 12, 1), LocalDate.of(2019, 12, 2));

        assertEquals("E\t0\tproject fair\t2019-12-01\t2019-12-02", event.toFileFormat());
    }

    @Test
    public void toFileFormat_markedDone_correctFileFormat() {
        Event event = new Event("project fair", LocalDate.of(2019, 12, 1), LocalDate.of(2019, 12, 2));
        event.markAsDone();

        assertEquals("E\t1\tproject fair\t2019-12-01\t2019-12-02", event.toFileFormat());
    }

    @Test
    public void toString_notDone_correctDisplayFormat() {
        Event event = new Event("project fair", LocalDate.of(2019, 12, 1), LocalDate.of(2019, 12, 2));

        assertEquals("[E][ ] project fair (from: Dec 1 2019 to: Dec 2 2019)", event.toString());
    }

    @Test
    public void toString_markedDone_correctDisplayFormat() {
        Event event = new Event("project fair", LocalDate.of(2019, 12, 1), LocalDate.of(2019, 12, 2));
        event.markAsDone();

        assertEquals("[E][X] project fair (from: Dec 1 2019 to: Dec 2 2019)", event.toString());
    }

    @Test
    public void reschedule_markedDone_datesChangedAndStillDone() {
        Event event = new Event("project fair", LocalDate.of(2019, 12, 1), LocalDate.of(2019, 12, 2));
        event.markAsDone();

        event.reschedule(LocalDate.of(2019, 12, 5), LocalDate.of(2019, 12, 6));

        assertEquals("[E][X] project fair (from: Dec 5 2019 to: Dec 6 2019)", event.toString());
    }

    @Test
    public void snooze_markedDone_bothDatesMovedAndStillDone() {
        Event event = new Event("project fair", LocalDate.of(2019, 12, 1), LocalDate.of(2019, 12, 2));
        event.markAsDone();

        event.snooze(7);

        assertEquals("[E][X] project fair (from: Dec 8 2019 to: Dec 9 2019)", event.toString());
    }

    @Test
    public void snooze_endPastLatestSupportedDate_exceptionThrownAndBothDatesUnchanged() {
        LocalDate from = LocalDate.MAX.minusDays(1);
        Event event = new Event("project fair", from, LocalDate.MAX);

        assertThrows(DateTimeException.class, () -> event.snooze(1));

        assertEquals("E\t0\tproject fair\t" + from + "\t" + LocalDate.MAX, event.toFileFormat());
    }

    @Test
    public void isDuplicateOf_sameDescriptionAndDates_true() {
        Event event = new Event("project fair", LocalDate.of(2019, 12, 1), LocalDate.of(2019, 12, 2));

        assertTrue(event.isDuplicateOf(
                new Event("project fair", LocalDate.of(2019, 12, 1), LocalDate.of(2019, 12, 2))));
    }

    @Test
    public void isDuplicateOf_differentEndDate_false() {
        Event event = new Event("project fair", LocalDate.of(2019, 12, 1), LocalDate.of(2019, 12, 2));

        assertFalse(event.isDuplicateOf(
                new Event("project fair", LocalDate.of(2019, 12, 1), LocalDate.of(2019, 12, 3))));
    }
}
