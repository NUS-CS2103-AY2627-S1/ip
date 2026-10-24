package shannon;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import shannon.exception.EmptyDescriptionException;
import shannon.exception.EmptyKeywordException;
import shannon.exception.InvalidMarkerException;
import shannon.exception.InvalidTaskNumberException;
import shannon.exception.MissingDeadlineByException;
import shannon.exception.MissingEventTimeException;
import shannon.exception.UnexpectedArgumentException;

/** Tests command normalization and actionable errors for malformed arguments. */
class ParserTest {
    @Test
    void parseTodo_mixedCaseAndWhitespace_normalizesInput() throws Exception {
        Parser parser = new Parser("  TODO   read\t book  ");
        assertEquals("todo", parser.getCommand());
        assertEquals("T | 0 | read book", parser.parseTodo().toFileFormat());
        assertEquals("", new Parser("  ").getCommand());
        assertFalse(new Parser(" list ").hasArgument());
        new Parser("list").checkNoArgument();
        assertThrows(UnexpectedArgumentException.class, () -> new Parser("list done").checkNoArgument());
    }

    @Test
    void parseTaskNumbers_multipleAndInvalidNumbers_checksWholeNumbers() throws Exception {
        assertArrayEquals(new int[] {2, 1, 2}, new Parser("delete 2  1\t2").parseTaskNumbers());
        for (String command : new String[] {"mark", "mark two", "delete 1.5", "delete 2147483648"}) {
            assertThrows(InvalidTaskNumberException.class, () -> new Parser(command).parseTaskNumbers());
        }
    }

    @Test
    void parseKeyword_phraseAndEmptyInput_normalizesOrRejects() throws Exception {
        assertEquals("team meeting", new Parser("find team   meeting").parseKeyword());
        assertThrows(EmptyKeywordException.class, () -> new Parser("find").parseKeyword());
    }

    @Test
    void parseTodo_missingDescriptionAndUnexpectedMarkers_rejected() {
        assertThrows(EmptyDescriptionException.class, () -> new Parser("todo").parseTodo());
        for (String marker : new String[] {"/by", "/from", "/to"}) {
            assertThrows(InvalidMarkerException.class, () -> new Parser("todo book " + marker).parseTodo());
        }
    }

    @Test
    void parseDeadline_validAndMissingParts_checksRequiredFields() throws Exception {
        assertEquals("D | 0 | submit report | 2026-08-09",
                new Parser("deadline submit report /BY 2026-08-09").parseDeadline().toFileFormat());
        for (String command : new String[] {"deadline report", "deadline report /by"}) {
            assertThrows(MissingDeadlineByException.class, () -> new Parser(command).parseDeadline());
        }
        assertThrows(EmptyDescriptionException.class, () ->
                new Parser("deadline /by 2026-08-09").parseDeadline());
    }

    @Test
    void parseDeadline_repeatedOrUnspacedMarker_rejected() {
        for (String command : new String[] {
            "deadline report /by 2026-08-09 /by 2026-08-10",
            "deadline report/by 2026-08-09", "deadline report /by2026-08-09"
        }) {
            assertThrows(InvalidMarkerException.class, () -> new Parser(command).parseDeadline());
        }
    }

    @Test
    void parseEvent_validEvent_preservesTimes() throws Exception {
        assertEquals("E | 0 | meeting | 2026-08-09 9:00 | 2026-08-09 10:00",
                new Parser("event meeting /from 2026-08-09 9:00 /to 2026-08-09 10:00")
                        .parseEvent().toFileFormat());
    }

    @Test
    void parseEvent_missingPartsAndWrongOrder_rejected() {
        for (String command : new String[] {
            "event meeting", "event meeting /from 2026-08-09", "event meeting /to 2026-08-09",
            "event meeting /from /to 2026-08-09"
        }) {
            assertThrows(MissingEventTimeException.class, () -> new Parser(command).parseEvent());
        }
        assertThrows(EmptyDescriptionException.class, () ->
                new Parser("event /from 2026-08-09 /to 2026-08-10").parseEvent());
        assertThrows(InvalidMarkerException.class, () ->
                new Parser("event meeting /to 2026-08-10 /from 2026-08-09").parseEvent());
    }
}
