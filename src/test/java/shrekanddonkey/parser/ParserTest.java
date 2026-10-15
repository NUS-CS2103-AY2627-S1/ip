package shrekanddonkey.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Tests parsing user inputs into command types and arguments in {@link Parser}.
 */
public class ParserTest {
    @Test
    public void parseCommandType_findCommand_returnsFind() {
        assertEquals(Parser.CommandType.FIND, Parser.parseCommandType("find book"));
        assertEquals(Parser.CommandType.FIND, Parser.parseCommandType("find"));
    }

    @Test
    public void getArguments_findCommand_returnsKeyword() {
        assertEquals("book", Parser.getArguments("find book", "find"));
        assertEquals("", Parser.getArguments("find", "find"));
    }

    @Test
    public void parseCommandType_allCommands_parsedCorrectly() {
        assertEquals(Parser.CommandType.LIST, Parser.parseCommandType("list"));
        assertEquals(Parser.CommandType.MARK, Parser.parseCommandType("mark 1"));
        assertEquals(Parser.CommandType.UNMARK, Parser.parseCommandType("unmark 1"));
        assertEquals(Parser.CommandType.TODO, Parser.parseCommandType("todo read"));
        assertEquals(Parser.CommandType.DEADLINE, Parser.parseCommandType("deadline task /by 2026-10-10"));
        assertEquals(Parser.CommandType.EVENT, Parser.parseCommandType("event e /from 2026-10-10 /to 2026-10-11"));
        assertEquals(Parser.CommandType.DELETE, Parser.parseCommandType("delete 1"));
        assertEquals(Parser.CommandType.SORT, Parser.parseCommandType("sort"));
        assertEquals(Parser.CommandType.EXIT, Parser.parseCommandType("bye"));
        assertEquals(Parser.CommandType.UNKNOWN, Parser.parseCommandType("unknownCommand"));
        assertEquals(Parser.CommandType.UNKNOWN, Parser.parseCommandType("read happyFile.txt"));
        assertEquals(Parser.CommandType.UNKNOWN, Parser.parseCommandType("write"));
    }

    @Test
    public void parseDateTime_supportedFormats_parsedSuccessfully() {
        assertEquals(java.time.LocalDateTime.of(2026, 12, 1, 14, 30),
                Parser.parseDateTime("2026-12-01T14:30"));
        assertEquals(java.time.LocalDateTime.of(2026, 12, 1, 14, 30),
                Parser.parseDateTime("2026-12-01 14:30"));
        assertEquals(java.time.LocalDateTime.of(2026, 12, 1, 0, 0),
                Parser.parseDateTime("2026-12-01"));
        assertEquals(java.time.LocalDateTime.of(2026, 12, 1, 14, 30),
                Parser.parseDateTime("1/12/2026 1430"));
    }
}
