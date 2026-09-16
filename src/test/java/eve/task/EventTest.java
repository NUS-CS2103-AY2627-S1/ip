package eve.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

public class EventTest {
    private static final LocalDate FROM = LocalDate.of(2019, 10, 4);
    private static final LocalDate TO = LocalDate.of(2019, 10, 11);

    @Test
    public void occursOn_dateBeforeStart_false() {
        Event event = new Event("trip", FROM, TO);

        assertFalse(event.occursOn(FROM.minusDays(1)));
    }

    @Test
    public void occursOn_startDate_true() {
        Event event = new Event("trip", FROM, TO);

        assertTrue(event.occursOn(FROM));
    }

    @Test
    public void occursOn_dateBetweenStartAndEnd_true() {
        Event event = new Event("trip", FROM, TO);

        assertTrue(event.occursOn(FROM.plusDays(3)));
    }

    @Test
    public void occursOn_endDate_true() {
        Event event = new Event("trip", FROM, TO);

        assertTrue(event.occursOn(TO));
    }

    @Test
    public void occursOn_dateAfterEnd_false() {
        Event event = new Event("trip", FROM, TO);

        assertFalse(event.occursOn(TO.plusDays(1)));
    }

    @Test
    public void occursOn_singleDayEvent_trueOnlyOnThatDate() {
        LocalDate day = LocalDate.of(2019, 12, 2);
        Event event = new Event("meeting", day, day);

        assertTrue(event.occursOn(day));
        assertFalse(event.occursOn(day.minusDays(1)));
        assertFalse(event.occursOn(day.plusDays(1)));
    }

    @Test
    public void toString_prefixedWithETypeIconAndBothDates() {
        assertEquals("[E][ ] trip (from: Oct 4 2019 to: Oct 11 2019)", new Event("trip", FROM, TO).toString());
    }

    @Test
    public void toSaveFormat_startsWithEAndEndsWithBothIsoDates() {
        assertEquals("E | 0 | trip | 2019-10-04 | 2019-10-11", new Event("trip", FROM, TO).toSaveFormat());
    }

    @Test
    public void getScheduleDate_isTheStartDate() {
        assertEquals(FROM, new Event("trip", FROM, TO).getScheduleDate().orElseThrow());
    }

    @Test
    public void hashCode_equalEvents_sameHashCode() {
        assertEquals(new Event("trip", FROM, TO).hashCode(), new Event("trip", FROM, TO).hashCode());
    }
}
