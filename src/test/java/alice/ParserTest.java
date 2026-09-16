package alice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class ParserTest {

    @Test
    void parseCommand_returnsCorrectCommand() {
        // Test parsing different commands
        assertEquals(Command.BYE, Parser.parseCommand("bye"));
        assertEquals(Command.LIST, Parser.parseCommand("list"));
        assertEquals(Command.MARK, Parser.parseCommand("mark 2"));
        assertEquals(Command.UNMARK, Parser.parseCommand("unmark 3"));
        assertEquals(Command.DELETE, Parser.parseCommand("delete 5"));
        assertEquals(Command.TODO, Parser.parseCommand("todo read book"));
        assertEquals(Command.DEADLINE, Parser.parseCommand("deadline return /by 2024-12-25"));
        assertEquals(Command.EVENT, Parser.parseCommand("event meeting /from 2024-12-20 /to 2024-12-22"));
        assertEquals(Command.UNKNOWN, Parser.parseCommand("blahblah"));
        assertEquals(Command.UNKNOWN, Parser.parseCommand(""));
    }

    @Test
    void parseCommand_handlesExtraSpaces() {
        assertEquals(Command.BYE, Parser.parseCommand("  bye  "));
        assertEquals(Command.LIST, Parser.parseCommand("   list   "));
        assertEquals(Command.TODO, Parser.parseCommand("  todo   read book  "));
    }

    @Test
    void parseIndex_returnsCorrectIndex() {
        String[] parts1 = {"mark", "2"};
        assertEquals(1, Parser.parseIndex(parts1));

        String[] parts2 = {"delete", "5"};
        assertEquals(4, Parser.parseIndex(parts2));

        // Invalid cases should return -1
        String[] parts3 = {"mark"};
        assertEquals(-1, Parser.parseIndex(parts3));

        String[] parts4 = {"mark", "abc"};
        assertEquals(-1, Parser.parseIndex(parts4));

        String[] parts5 = {"mark", "-1"};
        assertEquals(-1, Parser.parseIndex(parts5));

        String[] parts6 = {"mark", "1", "extra"};
        assertEquals(-1, Parser.parseIndex(parts6));
    }

    @Test
    void parseDeadline_returnsCorrectParts() {
        String input = "deadline return book /by 2024-12-25";
        String[] result = Parser.parseDeadline(input);
        assertNotNull(result);
        assertEquals("return book", result[0]);
        assertEquals("2024-12-25", result[1]);

        // Invalid cases should return null
        assertNull(Parser.parseDeadline("deadline return book")); // missing /by
        assertNull(Parser.parseDeadline("deadline /by 2024-12-25")); // empty description
        assertNull(Parser.parseDeadline("return book /by 2024-12-25")); // missing "deadline"
        assertNull(Parser.parseDeadline("deadline  /by ")); // empty both
        assertNull(Parser.parseDeadline("deadline book /by 2024-12-25 /by 2024-12-26"));
    }

    @Test
    void parseEvent_returnsCorrectParts() {
        String input = "event conference /from 2024-12-20 /to 2024-12-22";
        String[] result = Parser.parseEvent(input);
        assertNotNull(result);
        assertEquals("conference", result[0]);
        assertEquals("2024-12-20", result[1]);
        assertEquals("2024-12-22", result[2]);

        // Invalid cases
        assertNull(Parser.parseEvent("event conference /from 2024-12-20")); // missing /to
        assertNull(Parser.parseEvent("event /from 2024-12-20 /to 2024-12-22")); // empty description
        assertNull(Parser.parseEvent("conference /from 2024-12-20 /to 2024-12-22")); // missing "event"
        assertNull(Parser.parseEvent("event conference /to 2024-12-22 /from 2024-12-20"));
        assertNull(Parser.parseEvent("event conference /from 2024-12-20 /to 2024-12-22 /to 2024-12-23"));
    }

    @Test
    void parseDate_parsesValidDate() {
        LocalDate date = Parser.parseDate("2024-12-25");
        assertNotNull(date);
        assertEquals(2024, date.getYear());
        assertEquals(12, date.getMonthValue());
        assertEquals(25, date.getDayOfMonth());

        // Invalid date returns null
        assertNull(Parser.parseDate("25-12-2024"));
        assertNull(Parser.parseDate("2024-13-01"));
        assertNull(Parser.parseDate("blah"));
        assertNull(Parser.parseDate(""));
    }

    @Test
    void extractDescription_returnsCorrectDescription() {
        String result1 = Parser.extractDescription("todo read book", "todo");
        assertEquals("read book", result1);

        String result2 = Parser.extractDescription("todo    read   book  ", "todo");
        assertEquals("read   book", result2); // extra spaces inside description are preserved

        // Invalid cases
        assertEquals("", Parser.extractDescription("todo", "todo"));
        assertEquals("", Parser.extractDescription("", "todo"));
        assertEquals("", Parser.extractDescription("list", "todo")); // wrong prefix
    }
}
