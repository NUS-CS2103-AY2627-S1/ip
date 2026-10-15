package ev.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import ev.EvException;

public class EventTest {

    private Event camp() throws EvException {
        return new Event("camp",
                LocalDateTime.of(2019, 12, 1, 9, 0),
                LocalDateTime.of(2019, 12, 3, 17, 0));
    }

    @Test
    public void toString_bothTimes_shown() throws EvException {
        assertEquals("[E][ ] camp (from: Dec 1 2019, 9:00 AM to: Dec 3 2019, 5:00 PM)", camp().toString());
    }

    @Test
    public void toFileFormat_bothTimes_isoDatesStored() throws EvException {
        assertEquals("E | 0 | camp | 2019-12-01T09:00 | 2019-12-03T17:00", camp().toFileFormat());
    }

    @Test
    public void occursOn_firstDay_true() throws EvException {
        assertTrue(camp().occursOn(LocalDate.of(2019, 12, 1)));
    }

    @Test
    public void occursOn_dayInBetween_true() throws EvException {
        assertTrue(camp().occursOn(LocalDate.of(2019, 12, 2)));
    }

    @Test
    public void occursOn_lastDay_true() throws EvException {
        assertTrue(camp().occursOn(LocalDate.of(2019, 12, 3)));
    }

    @Test
    public void occursOn_dayBefore_false() throws EvException {
        assertFalse(camp().occursOn(LocalDate.of(2019, 11, 30)));
    }

    @Test
    public void occursOn_dayAfter_false() throws EvException {
        assertFalse(camp().occursOn(LocalDate.of(2019, 12, 4)));
    }

    @Test
    public void occursOn_singleDayEvent_onlyThatDay() throws EvException {
        Event meeting = new Event("meeting",
                LocalDateTime.of(2019, 12, 2, 14, 0),
                LocalDateTime.of(2019, 12, 2, 16, 0));
        assertTrue(meeting.occursOn(LocalDate.of(2019, 12, 2)));
        assertFalse(meeting.occursOn(LocalDate.of(2019, 12, 1)));
        assertFalse(meeting.occursOn(LocalDate.of(2019, 12, 3)));
    }

    @Test
    public void applyUpdate_from_endKept() throws EvException {
        Event camp = camp();
        camp.applyUpdate(Event.OPTION_FROM, "2019-11-30 0800");
        assertEquals("[E][ ] camp (from: Nov 30 2019, 8:00 AM to: Dec 3 2019, 5:00 PM)",
                camp.toString());
    }

    @Test
    public void applyUpdate_to_startKept() throws EvException {
        Event camp = camp();
        camp.applyUpdate(Event.OPTION_TO, "2019-12-04 1700");
        assertEquals("[E][ ] camp (from: Dec 1 2019, 9:00 AM to: Dec 4 2019, 5:00 PM)",
                camp.toString());
    }

    @Test
    public void applyUpdate_endBeforeStart_rejectedAndUnchanged() throws EvException {
        Event camp = camp();
        assertThrows(EvException.class, () ->
                camp.applyUpdate(Event.OPTION_TO, "2019-11-01 1700"));
        assertEquals("[E][ ] camp (from: Dec 1 2019, 9:00 AM to: Dec 3 2019, 5:00 PM)",
                camp.toString());
    }

    @Test
    public void applyUpdate_startAfterEnd_rejectedAndUnchanged() throws EvException {
        Event camp = camp();
        assertThrows(EvException.class, () ->
                camp.applyUpdate(Event.OPTION_FROM, "2019-12-31 0900"));
        assertEquals("[E][ ] camp (from: Dec 1 2019, 9:00 AM to: Dec 3 2019, 5:00 PM)",
                camp.toString());
    }

    @Test
    public void newEvent_endBeforeStart_exceptionThrown() {
        assertThrows(EvException.class, () -> new Event("camp",
                LocalDateTime.of(2019, 12, 3, 17, 0),
                LocalDateTime.of(2019, 12, 1, 9, 0)));
    }

    @Test
    public void newEvent_endEqualToStart_accepted() throws EvException {
        Event instant = new Event("standup",
                LocalDateTime.of(2019, 12, 2, 9, 0),
                LocalDateTime.of(2019, 12, 2, 9, 0));
        assertTrue(instant.occursOn(LocalDate.of(2019, 12, 2)));
    }

    @Test
    public void applyUpdate_optionThisTypeLacks_messageListsWhatItHas() throws EvException {
        EvException thrown = assertThrows(EvException.class, () ->
                camp().applyUpdate(Deadline.OPTION_BY, "2019-12-05"));
        assertTrue(thrown.getMessage().contains("an event"));
        assertTrue(thrown.getMessage().contains("/desc, /from, /to"));
    }
}
