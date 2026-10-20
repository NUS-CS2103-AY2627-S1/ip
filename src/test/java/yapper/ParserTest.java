package yapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Tests command recognition and validation at the user-input boundary.
 */
public class ParserTest {
    private final Parser parser = new Parser();

    @Test
    public void parseCommandType_completeWords_recognizesCommands() {
        String[] inputs = {"list", "help", "mark 1", "unmark 1", "delete 1", "todo book",
            "deadline book", "event meeting", "on 2024-02-29", "bye"};
        CommandType[] expected = {CommandType.LIST, CommandType.HELP, CommandType.MARK,
            CommandType.UNMARK, CommandType.DELETE, CommandType.TODO, CommandType.DEADLINE,
            CommandType.EVENT, CommandType.ON, CommandType.BYE};
        for (int i = 0; i < inputs.length; i++) {
            assertEquals(expected[i], parser.parseCommandType(inputs[i]), inputs[i]);
        }
    }

    @Test
    public void parseCommandType_unknownWords_returnsUnknown() {
        for (String input : new String[] {"", "TODO book", "todos book", "list extra", "bye now"}) {
            assertEquals(CommandType.UNKNOWN, parser.parseCommandType(input), input);
        }
    }

    @Test
    public void parseTodo_paddedDescription_trimsOnlyOuterWhitespace() throws YapperException {
        assertEquals("T | 0 | read  book", parser.parseTodo("todo   read  book  ").toDataString());
    }

    @Test
    public void parseTodo_missingDescription_throwsException() {
        assertThrows(YapperException.class, () -> parser.parseTodo("todo   "));
    }

    @Test
    public void parseTaskIndex_firstAndLast_convertsToZeroBased() throws YapperException {
        assertEquals(0, parser.parseTaskIndex("mark 1", "mark", 3));
        assertEquals(2, parser.parseTaskIndex("delete   3  ", "delete", 3));
    }

    @Test
    public void parseTaskIndex_invalidNumber_throwsException() {
        for (String value : new String[] {"", "0", "-1", "4", "one", "1.5", "1 2", "2147483648"}) {
            assertThrows(YapperException.class, () -> parser.parseTaskIndex("mark " + value, "mark", 3), value);
        }
        assertThrows(YapperException.class, () -> parser.parseTaskIndex("mark 1", "mark", 0));
    }

    @Test
    public void parseDeadline_validDate_preservesDescriptionAndTime() throws YapperException {
        assertEquals("D | 0 | return book | 2024-02-29T18:00:00",
                parser.parseDeadline("deadline return book /by 29/2/2024 1800").toDataString());
    }

    @Test
    public void parseDeadline_incompleteOrInvalid_throwsException() {
        for (String input : new String[] {"deadline", "deadline book", "deadline /by 2024-02-29",
            "deadline book /by", "deadline book /by tomorrow", "deadline book /by 2023-02-29"}) {
            assertThrows(YapperException.class, () -> parser.parseDeadline(input), input);
        }
    }

    @Test
    public void parseEvent_validAndEqualEndpoints_acceptsBoth() throws YapperException {
        assertEquals("E | 0 | trip | 2024-02-29T18:00:00 | 2024-03-01T09:00:00",
                parser.parseEvent("event trip /from 2024-02-29 1800 /to 1/3/2024 0900").toDataString());
        assertEquals("E | 0 | meeting | 2024-02-29T00:00:00 | 2024-02-29T00:00:00",
                parser.parseEvent("event meeting /from 2024-02-29 /to 2024-02-29").toDataString());
    }

    @Test
    public void parseEvent_incompleteOrInvalid_throwsException() {
        for (String input : new String[] {"event", "event trip /from 2024-02-29",
            "event trip /to 2024-03-01", "event /from 2024-02-29 /to 2024-03-01",
            "event trip /from /to 2024-03-01", "event trip /from 2024-02-29 /to",
            "event trip /from tomorrow /to 2024-03-01",
            "event trip /from 2024-02-29 /to yesterday",
            "event trip /from 2024-02-29 1800 /to 2024-02-29 1759"}) {
            assertThrows(YapperException.class, () -> parser.parseEvent(input), input);
        }
    }

    @Test
    public void parseDate_supportedFormats_returnsDate() throws YapperException {
        assertEquals(LocalDate.of(2024, 2, 29), parser.parseDate("on 2024-02-29"));
        assertEquals(LocalDate.of(2024, 2, 29), parser.parseDate("on   29/2/2024  "));
    }

    @Test
    public void parseDate_missingOrInvalid_throwsException() {
        for (String input : new String[] {"on", "on tomorrow", "on 2023-02-29", "on 2024-02-29 1800"}) {
            assertThrows(YapperException.class, () -> parser.parseDate(input), input);
        }
    }
}
