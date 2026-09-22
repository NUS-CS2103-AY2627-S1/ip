package miaow.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
//import java.time.LocalDateTime;

import miaow.task.Deadline;
import miaow.task.Event;
import miaow.task.Task;
import org.junit.jupiter.api.Test;

class ParserTest {

    private final Parser parser = new Parser();

    @Test
    void nullAndBlankCommandsAreUnknownOrInvalid() {
        assertEquals(
                Parser.CommandType.UNKNOWN,
                parser.getCommandType(null)
        );

        assertEquals(
                Parser.CommandType.UNKNOWN,
                parser.getCommandType("   ")
        );

        assertNull(parser.parseTodo(null));
        assertNull(parser.parseDeadline(
                "deadline task /by 2026-02-30"
        ));

        assertNull(parser.parseEvent(
                "event meeting /from not-a-date /to 16:00"
        ));
    }

    @Test
    void todoParserAcceptsSurroundingWhitespace() {
        Task task = parser.parseTodo("  todo   read book  ");

        assertEquals("read book", task.getDescription());
    }

    @Test
    void deadlineParserRejectsMissingOrRepeatedByParameter() {
        assertNull(parser.parseDeadline("deadline task"));

        assertNull(parser.parseDeadline(
                "deadline task /by 2026-01-01 /by 2026-01-02"
        ));
    }

    @Test
    void deadlineParserSupportsBothDocumentedDateFormats() {
        Deadline iso = (Deadline) parser.parseDeadline(
                "deadline task /by 2026-12-02"
        );

        Deadline slash = (Deadline) parser.parseDeadline(
                "deadline task /by 2/12/2026"
        );

        assertEquals(
                LocalDate.of(2026, 12, 2),
                iso.getByDate()
        );

        assertEquals(
                iso.getByDate(),
                slash.getByDate()
        );
    }

    @Test
    void eventParserRejectsNonIncreasingRanges() {
        assertNull(parser.parseEvent(
                "event meeting /from 2026-09-20 /to 2026-09-18"
        ));

        assertNull(parser.parseEvent(
                "event meeting /from 2026-09-18 /to 2026-09-18"
        ));
    }

    @Test
    void eventParserParsesDateOnlyValues() {
        Event event = (Event) parser.parseEvent(
                "event project meeting /from 2026-09-18 /to 2026-09-20"
        );

        assertEquals(
                LocalDate.of(2026, 9, 18),
                event.getFromDateTime()
        );
    }

    @Test
    void dateParserRejectsImpossibleDates() {
        assertNull(parser.parseDate("2026-02-30"));
        assertNull(parser.parseDate("31/2/2026"));

        assertEquals(
                LocalDate.of(2026, 9, 18),
                parser.parseDate("2026-09-18")
        );
    }

    @Test
    void taskNumberParserRejectsMalformedNumbers() {
        assertEquals(
                -1,
                parser.parseTaskNumber("delete abc", "delete ")
        );

        assertEquals(
                -1,
                parser.parseTaskNumber("delete 1 2", "delete ")
        );

        assertEquals(
                0,
                parser.parseTaskNumber("delete 1", "delete ")
        );
    }
}
