package fox.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import fox.exception.FoxException;
import fox.task.Deadline;
import fox.task.Event;

/** JUnit tests for successful and malformed Fox task commands. */
class TaskParserJUnitTest {
    private final TaskParser parser = new TaskParser();

    @Test
    void parsesTodoDeadlineAndEvent() throws FoxException {
        assertEquals("read book", parser.parse("todo", "todo   read   book").getDescription());
        Deadline deadline = assertInstanceOf(Deadline.class,
                parser.parse("deadline", "deadline submit /by 2026-12-02"));
        Event event = assertInstanceOf(Event.class,
                parser.parse("event", "event meeting /from 10am /to 11am"));

        assertEquals("2026-12-02", deadline.getBy());
        assertEquals("10am", event.getFrom());
        assertEquals("11am", event.getTo());
    }

    @Test
    void rejectsUnknownAndIncompleteCommands() {
        assertThrows(FoxException.class, () -> parser.parse("todo", "todo"));
        assertThrows(FoxException.class, () -> parser.parse("deadline", "deadline submit"));
        assertThrows(FoxException.class, () ->
                parser.parse("event", "event meeting /from 10am"));
        assertThrows(FoxException.class, () ->
                parser.parse("event", "event meeting /from 10am /to"));
        FoxException exception = assertThrows(FoxException.class, () ->
                parser.parse("remove", "remove book"));
        assertEquals("☹ OOPS!!! I don't recognize 'remove'. Try list, find, todo, deadline, "
                + "event, mark, unmark, delete, or bye.", exception.getMessage());
    }

    @Test
    void rejectsInvalidDeadlineDateAndBlankDescriptions() {
        assertThrows(FoxException.class, () ->
                parser.parse("deadline", "deadline submit /by 2026-02-30"));
        assertThrows(FoxException.class, () ->
                parser.parse("deadline", "deadline /by 2026-12-02"));
        assertThrows(FoxException.class, () ->
                parser.parse("event", "event /from 10am /to 11am"));
    }

    @Test
    void rejectsRepeatedParametersAndInvalidEventTimes() {
        FoxException repeatedDeadline = assertThrows(FoxException.class, () ->
                parser.parse("deadline", "deadline submit /by 2026-12-02 /by 2026-12-03"));
        FoxException repeatedEventStart = assertThrows(FoxException.class, () ->
                parser.parse("event", "event meeting /from 9am /from 10am /to 11am"));
        FoxException repeatedEventEnd = assertThrows(FoxException.class, () ->
                parser.parse("event", "event meeting /from 9am /to 10am /to 11am"));

        assertEquals("☹ OOPS!!! Please specify /by only once.", repeatedDeadline.getMessage());
        assertEquals("☹ OOPS!!! Please specify /from only once.", repeatedEventStart.getMessage());
        assertEquals("☹ OOPS!!! Please specify /to only once.", repeatedEventEnd.getMessage());
        assertThrows(FoxException.class, () ->
                parser.parse("event", "event meeting /from lunchtime /to 2pm"));
        assertThrows(FoxException.class, () ->
                parser.parse("event", "event meeting /from 11am /to 10am"));
        assertThrows(FoxException.class, () ->
                parser.parse("event", "event meeting /from 10am /to 10am"));
    }

    @Test
    void acceptsSupportedEventTimeFormatsRegardlessOfLetterCase() throws FoxException {
        Event twelveHourEvent = assertInstanceOf(Event.class,
                parser.parse("event", "event consultation /from 9:30AM /to 11am"));
        Event twentyFourHourEvent = assertInstanceOf(Event.class,
                parser.parse("event", "event workshop /from 14:00 /to 16:30"));

        assertEquals("9:30AM", twelveHourEvent.getFrom());
        assertEquals("11am", twelveHourEvent.getTo());
        assertEquals("14:00", twentyFourHourEvent.getFrom());
        assertEquals("16:30", twentyFourHourEvent.getTo());
    }
}
