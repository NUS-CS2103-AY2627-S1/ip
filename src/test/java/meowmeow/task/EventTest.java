package meowmeow.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import meowmeow.MeowmeowException;

/**
 * Tests for {@link Event} - specifically {@link Event#occursOn(LocalDate)},
 * whose "on the start day, the end day, or in between" span logic is the
 * most involved of the three task types, plus the rendering of both
 * endpoints.
 */
public class EventTest {

    private static Event event(String description, String from, String to) throws MeowmeowException {
        return new Event(description, TaskDateTime.parse(from), TaskDateTime.parse(to));
    }

    /** A three-day event, 1 Dec 2019 to 3 Dec 2019. */
    private static Event threeDayEvent() throws MeowmeowException {
        return event("camp", "1/12/2019", "3/12/2019");
    }

    // ---- occursOn: across the span ----

    @Test
    public void occursOn_startDay_true() throws MeowmeowException {
        assertTrue(threeDayEvent().occursOn(LocalDate.of(2019, 12, 1)));
    }

    @Test
    public void occursOn_endDay_true() throws MeowmeowException {
        assertTrue(threeDayEvent().occursOn(LocalDate.of(2019, 12, 3)));
    }

    @Test
    public void occursOn_dayInBetween_true() throws MeowmeowException {
        assertTrue(threeDayEvent().occursOn(LocalDate.of(2019, 12, 2)));
    }

    @Test
    public void occursOn_dayBeforeStart_false() throws MeowmeowException {
        assertFalse(threeDayEvent().occursOn(LocalDate.of(2019, 11, 30)));
    }

    @Test
    public void occursOn_dayAfterEnd_false() throws MeowmeowException {
        assertFalse(threeDayEvent().occursOn(LocalDate.of(2019, 12, 4)));
    }

    @Test
    public void occursOn_singleDayEventOnThatDay_true() throws MeowmeowException {
        Event sameDay = event("meeting", "2/12/2019 1400", "2/12/2019 1600");

        assertTrue(sameDay.occursOn(LocalDate.of(2019, 12, 2)));
        assertFalse(sameDay.occursOn(LocalDate.of(2019, 12, 3)));
    }

    // ---- toString ----

    @Test
    public void toString_withTimes_showsBothEndpointsInParentheses() throws MeowmeowException {
        assertEquals(
                "[E][ ] project meeting (from: Dec 2 2019, 2:00 pm to: Dec 2 2019, 4:00 pm)",
                event("project meeting", "2/12/2019 1400", "2/12/2019 1600").toString());
    }

    @Test
    public void toString_done_showsDoneIcon() throws MeowmeowException {
        Event e = event("camp", "1/12/2019", "3/12/2019");
        e.setStatus(TaskStatus.DONE);

        assertEquals("[E][X] camp (from: Dec 1 2019 to: Dec 3 2019)", e.toString());
    }

    // ---- toFileString ----

    @Test
    public void toFileString_withTimes_pipeSeparatedWithCanonicalEndpoints() throws MeowmeowException {
        assertEquals("E | 0 | project meeting | 2019-12-02 1400 | 2019-12-02 1600",
                event("project meeting", "2/12/2019 1400", "2/12/2019 1600").toFileString());
    }

    @Test
    public void toFileString_dateOnlyEndpoints_omitsTimes() throws MeowmeowException {
        assertEquals("E | 0 | camp | 2019-12-01 | 2019-12-03",
                event("camp", "1/12/2019", "3/12/2019").toFileString());
    }

    // ---- priority ----

    @Test
    public void toString_priorityHigh_suffixComesAfterBothEndpoints() throws MeowmeowException {
        Event e = threeDayEvent();
        e.setPriority(TaskPriority.HIGH);

        assertEquals("[E][ ] camp (from: Dec 1 2019 to: Dec 3 2019) (priority: HIGH)", e.toString());
    }

    @Test
    public void toFileString_priorityMedium_fieldComesAfterBothEndpoints() throws MeowmeowException {
        Event e = threeDayEvent();
        e.setPriority(TaskPriority.MEDIUM);

        assertEquals("E | 0 | camp | 2019-12-01 | 2019-12-03 | MEDIUM", e.toFileString());
    }
}
