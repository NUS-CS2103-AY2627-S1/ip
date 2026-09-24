package sage;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import sage.command.AddDeadlineCommand;
import sage.command.AddEventCommand;
import sage.command.AddTodoCommand;
import sage.command.ExitCommand;
import sage.command.FindCommand;
import sage.exception.SageException;
import sage.parser.Parser;

class ParserCoverageTest {
    private final Parser parser = new Parser();

    @Test
    void extractsCommandAndArguments() {
        assertEquals("todo", parser.getCommandName("  todo read book  "));
        assertEquals("read book", parser.getArguments("  todo read book  "));
        assertEquals("", parser.getArguments("list"));
    }

    @Test
    void parsesSupportedCommandsAndNullInput() throws Exception {
        assertNull(parser.parseCommand(null));
        assertEquals(ExitCommand.class, parser.parseCommand("bye").getClass());
        assertEquals(FindCommand.class, parser.parseCommand("find book").getClass());
        assertEquals(AddTodoCommand.class, parser.parseCommand("todo book").getClass());
        assertEquals(AddDeadlineCommand.class, parser.parseCommand("deadline book /by 1/1/2026 1200").getClass());
        assertEquals(AddEventCommand.class,
                parser.parseCommand("event book /from 1/1/2026 1200 /to 1/1/2026 1300").getClass());
    }

    @Test
    void parsesDatesInBothSupportedFormats() throws Exception {
        assertEquals(LocalDateTime.of(2026, 1, 2, 13, 5), parser.parseDateTime("2/1/2026 1305"));
        assertEquals(LocalDateTime.of(2026, 1, 2, 13, 5), parser.parseDateTime("2026-01-02 1305"));
        assertEquals(LocalDate.of(2026, 1, 2), parser.parseDate("2/1/2026"));
        assertEquals(LocalDate.of(2026, 1, 2), parser.parseDate("2026-01-02"));
        assertEquals(LocalDateTime.of(2026, 1, 2, 0, 0),
                parser.parseDateTimeOrDate("2026-01-02", true));
        assertEquals(LocalDateTime.of(2026, 1, 2, 23, 59),
                parser.parseDateTimeOrDate("2026-01-02", false));
        assertEquals(LocalDateTime.of(2026, 1, 2, 23, 59),
                parser.parseDateTimeOrDate("2/1/2026", false));
    }

    @Test
    void parsesAndValidatesTaskDetails() throws Exception {
        assertArrayEquals(new String[] {"submit report", "2/1/2026 1305"},
                parser.parseDeadlineDetails("submit report /by 2/1/2026 1305"));
        assertArrayEquals(new String[] {"meeting", "2/1/2026 1305", "2/1/2026 1405"},
                parser.parseEventDetails("meeting /from 2/1/2026 1305 /to 2/1/2026 1405"));
        assertThrows(SageException.class, () -> parser.validateDescription("todo   book"));
        assertThrows(SageException.class, () -> parser.parseDate("31/2/2026"));
        assertThrows(SageException.class, () -> parser.parseDeadlineDetails("missing by"));
        assertThrows(SageException.class, () -> parser.parseEventDetails("missing to /from x"));
    }

    @Test
    void parsesTaskNumberOnlyWhenArgumentIsNumeric() {
        assertEquals(12, parser.parseTaskNumber("mark 12"));
        assertThrows(NumberFormatException.class, () -> parser.parseTaskNumber("mark one"));
        assertThrows(NumberFormatException.class, () -> parser.parseTaskNumber("mark 1 extra"));
    }
}
