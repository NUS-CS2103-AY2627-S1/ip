package happybot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import happybot.task.Event;

public class ParserEventTest {
    private static final String EVENT_USAGE =
            "Use: event <description> /from <yyyy-MM-dd> /to <yyyy-MM-dd>.";

    private static final String DATE_FORMAT_HINT =
            "Please write dates as yyyy-MM-dd or d/M/yyyy, with an optional 24-hour time that "
                    + "defaults to 0000, such as 2019-12-02 1800.";

    private static final String PIPE_MESSAGE = "A task cannot contain the '|' character.";

    @Test
    public void parseEvent_datesWithoutTimes_eventCreated() throws HappyBotException {
        Event event = Parser.parseEvent("orientation /from 2019-12-01 /to 2019-12-03");

        assertEquals("orientation", event.getDescription());
        assertEquals(LocalDateTime.of(2019, 12, 1, 0, 0), event.getStartTime());
        assertEquals(LocalDateTime.of(2019, 12, 3, 0, 0), event.getEndTime());
    }

    @Test
    public void parseEvent_datesWithTimes_timesKept() throws HappyBotException {
        Event event = Parser.parseEvent("orientation /from 2019-12-01 0900 /to 2019-12-03 1700");

        assertEquals(LocalDateTime.of(2019, 12, 1, 9, 0), event.getStartTime());
        assertEquals(LocalDateTime.of(2019, 12, 3, 17, 0), event.getEndTime());
    }

    @Test
    public void parseEventDetails_dateOnlyAndExplicitMidnight_timePresenceKept()
            throws HappyBotException {
        Parser.EventDetails eventDetails =
                Parser.parseEventDetails("orientation /from 2019-12-01 /to 2019-12-03 0000");

        assertFalse(eventDetails.getStartTime().hasTime());
        assertTrue(eventDetails.getEndTime().hasTime());
        assertEquals(LocalDateTime.of(2019, 12, 3, 0, 0),
                eventDetails.getEndTime().getDateTime());
    }

    @Test
    public void parseEvent_sameDayDifferentTimes_eventCreated() throws HappyBotException {
        Event event = Parser.parseEvent("lecture /from 2019-12-01 0900 /to 2019-12-01 1100");

        assertEquals(LocalDateTime.of(2019, 12, 1, 9, 0), event.getStartTime());
        assertEquals(LocalDateTime.of(2019, 12, 1, 11, 0), event.getEndTime());
    }

    @Test
    public void parseEvent_spacesAroundDescription_descriptionTrimmed() throws HappyBotException {
        Event event = Parser.parseEvent("   orientation  /from 2019-12-01 /to 2019-12-03");

        assertEquals("orientation", event.getDescription());
    }

    @Test
    public void parseEvent_noStartMarker_exceptionThrown() {
        HappyBotException e = assertThrows(HappyBotException.class,
                () -> Parser.parseEvent("orientation /to 2019-12-03"));

        assertEquals(EVENT_USAGE, e.getMessage());
    }

    @Test
    public void parseEvent_noEndMarker_exceptionThrown() {
        HappyBotException e = assertThrows(HappyBotException.class,
                () -> Parser.parseEvent("orientation /from 2019-12-01"));

        assertEquals(EVENT_USAGE, e.getMessage());
    }

    @Test
    public void parseEvent_endMarkerBeforeStartMarker_exceptionThrown() {
        // The end marker is searched for only after the start marker, so a /to typed first is
        // not mistaken for this event's end.
        HappyBotException e = assertThrows(HappyBotException.class,
                () -> Parser.parseEvent("orientation /to 2019-12-03 /from 2019-12-01"));

        assertEquals(EVENT_USAGE, e.getMessage());
    }

    @Test
    public void parseEvent_noDescription_exceptionThrown() {
        HappyBotException e = assertThrows(HappyBotException.class,
                () -> Parser.parseEvent(" /from 2019-12-01 /to 2019-12-03"));

        assertEquals(EVENT_USAGE, e.getMessage());
    }

    @Test
    public void parseEvent_blankStartDate_exceptionThrown() {
        HappyBotException e = assertThrows(HappyBotException.class,
                () -> Parser.parseEvent("orientation /from    /to 2019-12-03"));

        assertEquals(EVENT_USAGE, e.getMessage());
    }

    @Test
    public void parseEvent_blankEndDate_exceptionThrown() {
        HappyBotException e = assertThrows(HappyBotException.class,
                () -> Parser.parseEvent("orientation /from 2019-12-01 /to     "));

        assertEquals(EVENT_USAGE, e.getMessage());
    }

    @Test
    public void parseEvent_startEqualsEnd_exceptionThrown() {
        HappyBotException e = assertThrows(HappyBotException.class,
                () -> Parser.parseEvent("orientation /from 2019-12-01 /to 2019-12-01"));

        assertEquals("An event must start before it ends.", e.getMessage());
    }

    @Test
    public void parseEvent_startAfterEnd_exceptionThrown() {
        HappyBotException e = assertThrows(HappyBotException.class,
                () -> Parser.parseEvent("orientation /from 2019-12-03 /to 2019-12-01"));

        assertEquals("An event must start before it ends.", e.getMessage());
    }

    @Test
    public void parseEvent_unreadableStartDate_exceptionThrown() {
        HappyBotException e = assertThrows(HappyBotException.class,
                () -> Parser.parseEvent("orientation /from someday /to 2019-12-03"));

        assertEquals(DATE_FORMAT_HINT, e.getMessage());
    }

    @Test
    public void parseEvent_pipeInDescription_exceptionThrown() {
        HappyBotException e = assertThrows(HappyBotException.class,
                () -> Parser.parseEvent("talk | panel /from 2019-12-01 /to 2019-12-03"));

        assertEquals(PIPE_MESSAGE, e.getMessage());
    }

    @Test
    public void parseEvent_duplicateParameterMarker_exceptionThrown() {
        HappyBotException e = assertThrows(HappyBotException.class,
                () -> Parser.parseEvent("orientation /from 2019-12-01 /to 2019-12-03 /to 2019-12-04"));

        assertEquals(EVENT_USAGE, e.getMessage());
    }

    @Test
    public void parseEvent_duplicateStartMarker_exceptionThrown() {
        HappyBotException e = assertThrows(HappyBotException.class,
                () -> Parser.parseEvent("orientation /from 2019-12-01 /from 2019-12-02 /to 2019-12-03"));

        assertEquals(EVENT_USAGE, e.getMessage());
    }
}
