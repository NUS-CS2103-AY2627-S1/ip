package bob.parser;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import bob.command.Command;
import bob.command.ParsedCommand;
import bob.task.Deadline;
import org.junit.jupiter.api.Test;

/**
 * Tests recognition and argument extraction performed by {@link Parser}.
 */
public class ParserTest {
    private final Parser parser = new Parser();

    @Test
    public void parseDeadline_validArguments_preservesDescriptionAndParsesDate() {
        Deadline deadline = parser.parseDeadline("  return   book  /by  2/12/2019  ");
        assertEquals("return   book", deadline.getDescription());
        assertEquals(LocalDate.of(2019, 12, 2), deadline.getBy());
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0),
                parser.parseDeadline("return book /by 2/12/2019 1800").getBy());
    }

    @Test
    public void parseDeadline_missingArguments_throwsFormatException() {
        for (String input : new String[] {"", "read", " /by 2/12/2019", "  /by 2/12/2019",
                "read /by ", "read /by   ", "read /by", "read/by 2/12/2019"}) {
            assertThrows(IllegalArgumentException.class, () -> parser.parseDeadline(input), input);
        }
    }

    @Test
    public void parseDeadline_invalidDateOrRepeatedSeparator_throwsDateException() {
        for (String input : new String[] {"read /by 31/02/2019", "read /by tomorrow",
                "read /by 2/12/2019 2500", "read /by 2/12/2019 /by 3/12/2019"}) {
            assertThrows(DateTimeParseException.class, () -> parser.parseDeadline(input), input);
        }
    }

    @Test
    public void parse_supportedCommandWords_returnsMatchingCommands() {
        assertAll(
                () -> assertEquals(Command.BYE, parser.parse("bye").getCommand()),
                () -> assertEquals(Command.LIST, parser.parse("list").getCommand()),
                () -> assertEquals(Command.FIND, parser.parse("find book").getCommand()),
                () -> assertEquals(Command.MARK, parser.parse("mark 1").getCommand()),
                () -> assertEquals(Command.UNMARK, parser.parse("unmark 1").getCommand()),
                () -> assertEquals(Command.DELETE, parser.parse("delete 1").getCommand()),
                () -> assertEquals(Command.TODO, parser.parse("todo read").getCommand()),
                () -> assertEquals(Command.DEADLINE, parser.parse("deadline read").getCommand()),
                () -> assertEquals(Command.EVENT, parser.parse("event meeting").getCommand()));
    }

    @Test
    public void parse_shortAliases_returnsMatchingCommandsAndArguments() {
        String[][] commandPairs = {
            {"b", "bye"},
            {"l", "list"},
            {"f BOOK", "find BOOK"},
            {"m 1", "mark 1"},
            {"u 1", "unmark 1"},
            {"del 1", "delete 1"},
            {"  t\t read   a book  ", "todo read   a book"},
            {"d return book /by 2/12/2019", "deadline return book /by 2/12/2019"},
            {"e meeting /from 2/12/2019 /to 3/12/2019", "event meeting /from 2/12/2019 /to 3/12/2019"}
        };
        for (String[] pair : commandPairs) {
            ParsedCommand expected = parser.parse(pair[1]);
            ParsedCommand actual = parser.parse(pair[0]);
            assertEquals(expected.getCommand(), actual.getCommand(), pair[0]);
            assertEquals(expected.getArguments(), actual.getArguments(), pair[0]);
        }
    }

    @Test
    public void parse_bareAliases_preservesMissingArguments() {
        for (String alias : new String[] {"t", "d", "e", "m", "u", "del", "f"}) {
            assertEquals("", parser.parse(alias).getArguments(), alias);
        }
    }

    @Test
    public void parse_unsupportedAliasVariants_returnsUnknownCommand() {
        for (String input : new String[] {"T read", "M 1", "DEL 1", "td read", "de 1", "tm read"}) {
            assertEquals(Command.UNKNOWN, parser.parse(input).getCommand(), input);
        }
    }

    @Test
    public void parse_commandWithArguments_returnsArgumentsWithoutOuterWhitespace() {
        ParsedCommand result = parser.parse("  todo    read   a book  ");

        assertEquals(Command.TODO, result.getCommand());
        assertEquals("read   a book", result.getArguments());
    }

    @Test
    public void parse_commandWithoutArguments_returnsEmptyArguments() {
        assertEquals("", parser.parse("list").getArguments());
    }

    @Test
    public void parse_emptyInput_returnsUnknownCommand() {
        assertEquals(Command.UNKNOWN, parser.parse("   ").getCommand());
    }

    @Test
    public void parse_similarCommandPrefix_returnsUnknownCommand() {
        assertEquals(Command.UNKNOWN, parser.parse("deleteLater 1").getCommand());
    }

    @Test
    public void parse_commandWithDifferentCapitalization_returnsUnknownCommand() {
        assertEquals(Command.UNKNOWN, parser.parse("TODO read").getCommand());
    }
}
