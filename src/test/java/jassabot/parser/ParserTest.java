package jassabot.parser;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Test;

import jassabot.exception.JassaBotException;

/**
 * Tests command recognition and date-time parsing performed by {@link Parser}.
 */
public class ParserTest {
    private static final String INVALID_DATE_TIME_MESSAGE =
            "Please enter a valid date as yyyy-MM-dd or d/M/yyyy, "
                    + "optionally followed by a time in HHmm format.";

    @Test
    public void parseCommandType_bareSupportedCommand_returnsCorrespondingType() {
        assertAll(() ->
                assertEquals(CommandType.HELP, Parser.parseCommandType("help")), () ->
                assertEquals(CommandType.BYE, Parser.parseCommandType("bye")), () ->
                assertEquals(CommandType.LIST, Parser.parseCommandType("list")), () ->
                assertEquals(CommandType.MARK, Parser.parseCommandType("mark")), () ->
                assertEquals(CommandType.UNMARK, Parser.parseCommandType("unmark")), () ->
                assertEquals(CommandType.DELETE, Parser.parseCommandType("delete")), () ->
                assertEquals(CommandType.FIND, Parser.parseCommandType("find")), () ->
                assertEquals(CommandType.DEADLINE, Parser.parseCommandType("deadline")), () ->
                assertEquals(CommandType.EVENT, Parser.parseCommandType("event")), () ->
                assertEquals(CommandType.TODO, Parser.parseCommandType("todo"))
        );
    }

    @Test
    public void parseCommandType_commandWithArguments_returnsCorrespondingType() {
        assertAll(() ->
                assertEquals(CommandType.MARK, Parser.parseCommandType("mark 1")), () ->
                assertEquals(CommandType.UNMARK, Parser.parseCommandType("unmark 1")), () ->
                assertEquals(CommandType.DELETE, Parser.parseCommandType("delete 1")), () ->
                assertEquals(CommandType.FIND, Parser.parseCommandType("find book")), () ->
                assertEquals(CommandType.DEADLINE,
                        Parser.parseCommandType("deadline return book /by 2019-12-02")), () ->
                assertEquals(CommandType.EVENT,
                        Parser.parseCommandType("event meeting /from 2019-12-02 /to 2019-12-03")), () ->
                assertEquals(CommandType.TODO, Parser.parseCommandType("todo read book"))
        );
    }

    @Test
    public void parseCommandType_unrecognisedCommand_returnsUnknown() {
        assertAll(() ->
                assertEquals(CommandType.UNKNOWN, Parser.parseCommandType("")), () ->
                assertEquals(CommandType.UNKNOWN, Parser.parseCommandType("unknown")), () ->
                assertEquals(CommandType.UNKNOWN, Parser.parseCommandType("BYE")), () ->
                assertEquals(CommandType.UNKNOWN, Parser.parseCommandType("bye now")), () ->
                assertEquals(CommandType.UNKNOWN, Parser.parseCommandType("list tasks")), () ->
                assertEquals(CommandType.UNKNOWN, Parser.parseCommandType("mark1")), () ->
                assertEquals(CommandType.UNKNOWN, Parser.parseCommandType("findbook")), () ->
                assertEquals(CommandType.UNKNOWN, Parser.parseCommandType("todoist")), () ->
                assertEquals(CommandType.UNKNOWN, Parser.parseCommandType("todo\tread book"))
        );
    }

    @Test
    public void parseCommandType_invalidHelp_returnsUnknown() {
        for (String command : List.of("HELP", "Help", "?", "/help", "--help",
                "helper", "helpful", "help123", "help todo", "help unknown", "help\ttodo", " help ")) {
            assertEquals(CommandType.UNKNOWN, Parser.parseCommandType(command), command);
        }
    }

    @Test
    public void parseDateTime_supportedFormats_returnsLocalDateTime() throws JassaBotException {
        assertAll(() ->
                assertEquals(LocalDateTime.of(2019, 12, 2, 0, 0),
                        Parser.parseDateTime("2019-12-02")), () ->
                assertEquals(LocalDateTime.of(2019, 12, 2, 0, 0),
                        Parser.parseDateTime("2/12/2019")), () ->
                assertEquals(LocalDateTime.of(2019, 12, 2, 18, 30),
                        Parser.parseDateTime("2019-12-02 1830")), () ->
                assertEquals(LocalDateTime.of(2019, 12, 2, 18, 30),
                        Parser.parseDateTime("2/12/2019 1830"))
        );
    }

    @Test
    public void parseDateTime_validBoundaryValues_returnsLocalDateTime() throws JassaBotException {
        assertAll(() ->
                assertEquals(LocalDateTime.of(2000, 2, 29, 0, 0),
                        Parser.parseDateTime("2000-02-29 0000")), () ->
                assertEquals(LocalDateTime.of(2024, 12, 31, 23, 59),
                        Parser.parseDateTime("31/12/2024 2359"))
        );
    }

    @Test
    public void parseDateTime_invalidValue_throwsJassaBotException() {
        assertAll(() ->
                assertInvalidDateTime(""), () ->
                assertInvalidDateTime("02-12-2019"), () ->
                assertInvalidDateTime("2019-02-29"), () ->
                assertInvalidDateTime("2019-13-01"), () ->
                assertInvalidDateTime("32/12/2019"), () ->
                assertInvalidDateTime("2019-12-02 2400"), () ->
                assertInvalidDateTime("2/12/2019 1260"), () ->
                assertInvalidDateTime("2019-12-02 600")
        );
    }

    /**
     * Verifies that an invalid date-time produces the parser's corrective user message.
     *
     * @param value Invalid date-time text to parse.
     */
    private static void assertInvalidDateTime(String value) {
        JassaBotException exception = assertThrows(
                JassaBotException.class, () -> Parser.parseDateTime(value));
        assertEquals(INVALID_DATE_TIME_MESSAGE, exception.getMessage());
    }

    @Test
    public void parseCommandType_prefixesAndCaseVariants_rejectsNearMatches() {
        for (String command : List.of("help", "bye", "list", "mark", "unmark", "delete",
                "find", "deadline", "event", "todo")) {
            assertEquals(CommandType.UNKNOWN, Parser.parseCommandType(command + "suffix"), command);
            assertEquals(CommandType.UNKNOWN,
                    Parser.parseCommandType(command.toUpperCase(Locale.ROOT)), command);
            assertEquals(CommandType.UNKNOWN, Parser.parseCommandType(command + "\targument"), command);
        }
    }
}
