package bot.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import bot.exception.BotException;
import bot.parser.TaskDateTime;

class EventTest {

    @Test
    void toString_showsFromAndTo() throws BotException {
        Event event = new Event("project meeting",
                TaskDateTime.parse("2019-10-15 1400"), TaskDateTime.parse("2019-10-15 1600"));
        assertEquals("[E][ ] project meeting (from: Oct 15 2019, 2:00PM to: Oct 15 2019, 4:00PM)", event.toString());
    }

    @Test
    void toSaveFormat_includesBothDateTimes() throws BotException {
        Event event = new Event("project meeting",
                TaskDateTime.parse("2019-10-15 1400"), TaskDateTime.parse("2019-10-15 1600"));
        assertEquals("E | 0 | project meeting | 2019-10-15 1400 | 2019-10-15 1600", event.toSaveFormat());
    }

    @Test
    void toSaveFormat_done_usesOneFlag() throws BotException {
        Event event = new Event("project meeting",
                TaskDateTime.parse("2019-10-15"), TaskDateTime.parse("2019-10-16"));
        event.markAsDone();
        assertEquals("E | 1 | project meeting | 2019-10-15 | 2019-10-16", event.toSaveFormat());
    }

    @Test
    void occursOn_startDate_returnsTrue() throws BotException {
        Event event = new Event("trip", TaskDateTime.parse("2019-10-15"), TaskDateTime.parse("2019-10-18"));
        assertTrue(event.occursOn(LocalDate.of(2019, 10, 15)));
    }

    @Test
    void occursOn_endDate_returnsTrue() throws BotException {
        Event event = new Event("trip", TaskDateTime.parse("2019-10-15"), TaskDateTime.parse("2019-10-18"));
        assertTrue(event.occursOn(LocalDate.of(2019, 10, 18)));
    }

    @Test
    void occursOn_dateInBetween_returnsTrue() throws BotException {
        Event event = new Event("trip", TaskDateTime.parse("2019-10-15"), TaskDateTime.parse("2019-10-18"));
        assertTrue(event.occursOn(LocalDate.of(2019, 10, 16)));
    }

    @Test
    void occursOn_dateBeforeStart_returnsFalse() throws BotException {
        Event event = new Event("trip", TaskDateTime.parse("2019-10-15"), TaskDateTime.parse("2019-10-18"));
        assertFalse(event.occursOn(LocalDate.of(2019, 10, 14)));
    }

    @Test
    void occursOn_dateAfterEnd_returnsFalse() throws BotException {
        Event event = new Event("trip", TaskDateTime.parse("2019-10-15"), TaskDateTime.parse("2019-10-18"));
        assertFalse(event.occursOn(LocalDate.of(2019, 10, 19)));
    }
}
