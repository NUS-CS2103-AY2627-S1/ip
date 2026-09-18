package littledaisy.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import littledaisy.exception.LittleDaisyException;
import littledaisy.task.Deadline;
import littledaisy.task.Event;

class ParserTest {
    @Test
    void parse_commandWithArguments_commandAndTrimmedArgumentsReturned() {
        ParsedCommand parsed = Parser.parse("  deadline submit report /by 2026-09-01  ");

        assertEquals(Command.DEADLINE, parsed.command());
        assertEquals("submit report /by 2026-09-01", parsed.arguments());
    }

    @Test
    void parse_blankOrUnknownInput_unknownCommandReturned() {
        assertEquals(Command.UNKNOWN, Parser.parse("   ").command());
        assertEquals(Command.UNKNOWN, Parser.parse("dance now").command());
    }

    @Test
    void parseTaskIndex_validTaskNumber_zeroBasedIndexReturned()
            throws LittleDaisyException {
        assertEquals(1, Parser.parseTaskIndex("2", 3));
    }

    @Test
    void parseTaskIndex_missingNonNumericOrOutOfRange_exceptionThrown() {
        assertThrows(LittleDaisyException.class, () -> Parser.parseTaskIndex("", 3));
        assertThrows(LittleDaisyException.class, () -> Parser.parseTaskIndex("two", 3));
        assertThrows(LittleDaisyException.class, () -> Parser.parseTaskIndex("2 extra", 3));
        assertThrows(LittleDaisyException.class, () -> Parser.parseTaskIndex("0", 3));
        assertThrows(LittleDaisyException.class, () -> Parser.parseTaskIndex("4", 3));
    }

    @Test
    void parseFindKeyword_presentAndMissingKeyword_keywordOrExceptionReturned()
            throws LittleDaisyException {
        assertEquals("report", Parser.parseFindKeyword("report"));
        assertThrows(LittleDaisyException.class, () -> Parser.parseFindKeyword(""));
    }

    @Test
    void parseDeadline_validInput_structuredDeadlineReturned()
            throws LittleDaisyException {
        Deadline deadline = Parser.parseDeadline("submit report /by 2026-09-01");

        assertEquals("submit report", deadline.getDescription());
        assertEquals("2026-09-01", deadline.getBy().toString());
        assertEquals("[D][ ] submit report (by: Sep 1 2026)", deadline.toString());
    }

    @Test
    void parseDeadline_missingOrInvalidDate_exceptionThrown() {
        assertThrows(LittleDaisyException.class,
                () -> Parser.parseDeadline("submit report"));
        assertThrows(LittleDaisyException.class,
                () -> Parser.parseDeadline("submit report /by 2026-02-30"));
        assertThrows(LittleDaisyException.class,
                () -> Parser.parseDeadline("submit /by 2026-09-01 /by 2026-09-02"));
    }

    @Test
    void parseDeadline_multipleSpaces_validDeadlineReturned() throws LittleDaisyException {
        Deadline deadline = Parser.parseDeadline("submit report   /by   2026-09-01");

        assertEquals("submit report", deadline.getDescription());
        assertEquals("2026-09-01", deadline.getBy().toString());
    }

    @Test
    void parseEvent_validInput_eventFieldsReturned() throws LittleDaisyException {
        Event event = Parser.parseEvent("meeting /from 2pm /to 4pm");

        assertEquals("meeting", event.getDescription());
        assertEquals("2pm", event.getFrom());
        assertEquals("4pm", event.getTo());
    }

    @Test
    void parseEvent_missingStartOrEnd_exceptionThrown() {
        assertThrows(LittleDaisyException.class,
                () -> Parser.parseEvent("meeting /to 4pm"));
        assertThrows(LittleDaisyException.class,
                () -> Parser.parseEvent("meeting /from 2pm"));
        assertThrows(LittleDaisyException.class,
                () -> Parser.parseEvent("meeting /from 2pm /to 4pm /to 5pm"));
    }
}
