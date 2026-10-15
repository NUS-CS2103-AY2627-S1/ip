package walnut;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import org.junit.jupiter.api.Test;

class ParserValidationTest {
    @Test
    void parseCommand_recognisesEverySupportedCommand() {
        assertEquals(Command.TODO, Parser.parseCommand("todo"));
        assertEquals(Command.DEADLINE, Parser.parseCommand("deadline"));
        assertEquals(Command.EVENT, Parser.parseCommand("event"));
        assertEquals(Command.MARK, Parser.parseCommand("mark"));
        assertEquals(Command.UNMARK, Parser.parseCommand("unmark"));
        assertEquals(Command.LIST, Parser.parseCommand("list"));
        assertEquals(Command.BYE, Parser.parseCommand("bye"));
        assertEquals(Command.REMOVE, Parser.parseCommand("remove"));
        assertEquals(Command.FIND, Parser.parseCommand("find"));
    }

    @Test
    void parseCommand_isCaseInsensitive() {
        assertEquals(Command.TODO, Parser.parseCommand("ToDo"));
    }

    @Test
    void parseCommand_returnsNullForUnknownCommand() {
        assertNull(Parser.parseCommand("archive"));
    }

    @Test
    void parseTask_returnsNullForMalformedRecords() {
        assertNull(Parser.parseTask(""));
        assertNull(Parser.parseTask("not a task record"));
        assertNull(Parser.parseTask("X | 0 | unknown"));
        assertNull(Parser.parseTask("T | 2 | invalid status"));
        assertNull(Parser.parseTask("D | 0 | missing date"));
        assertNull(Parser.parseTask("E | 0 | missing date"));
    }

    @Test
    void parseDateTime_parsesStorageFormat() {
        LocalDateTime result = Parser.parseDateTime("2026/09/01 1700");

        assertEquals(LocalDateTime.of(2026, 9, 1, 17, 0), result);
    }

    @Test
    void parseDateTime_rejectsUserFormat() {
        String input = "2026-09-01 1700";
        assertThrows(DateTimeParseException.class,
                () -> Parser.parseDateTime(input));
    }

    @Test
    void parseUserDateTime_parsesUserFormat() {
        LocalDateTime result = Parser.parseUserDateTime("2026-09-01 1700");

        assertEquals(LocalDateTime.of(2026, 9, 1, 17, 0), result);
    }

    @Test
    void parseUserDateTime_rejectsImpossibleDate() {
        String input = "2026-02-30 1700";
        assertThrows(DateTimeParseException.class,
                () -> Parser.parseUserDateTime(input));
    }
}
