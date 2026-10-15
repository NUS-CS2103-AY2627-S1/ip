package serina.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import serina.exception.SerinaError;
import serina.exception.SerinaException;

/** Tests command grammar and normalization. */
public class CommandParserTest {
    @Test
    public void parse_repeatedWhitespace_returnsSemanticArguments() throws SerinaException {
        ParsedCommand command = CommandParser.parse("  event   briefing /from 2026-09-20   /to 2026-09-21 ");

        assertEquals(CommandType.EVENT, command.type());
        assertEquals(List.of("briefing", "2026-09-20", "2026-09-21"), command.arguments());
        assertEquals(List.of("two\twords"), CommandParser.parse("find\ttwo\twords").arguments());
    }

    @Test
    public void parse_extraOrDuplicateArguments_throwsSerinaException() {
        assertThrows(SerinaException.class, () -> CommandParser.parse("help me"));
        assertThrows(SerinaException.class, () -> CommandParser.parse("mark +1"));
        assertThrows(SerinaException.class, () ->
                CommandParser.parse("deadline report /by 2026-09-20 /by 2026-09-21"));
        assertThrows(SerinaException.class, () ->
                CommandParser.parse("event briefing /to 2026-09-21 /from 2026-09-20"));
    }

    @Test
    public void parse_controlCharacter_throwsSerinaException() {
        assertThrows(SerinaException.class, () -> CommandParser.parse("todo unsafe\u0000text"));
    }

    @ParameterizedTest
    @CsvSource({
        "HELP, help",
        "LIST, LiSt",
        "BYE, BYE",
        "TODO, 'todo 检查引擎 🚀'",
        "FIND, 'find report draft'",
        "MARK, 'mark 1'",
        "UNMARK, 'unmark 12'",
        "DELETE, 'delete 999'"
    })
    public void parse_supportedCommands_returnsExpectedType(CommandType expectedType, String input)
            throws SerinaException {
        assertEquals(expectedType, CommandParser.parse(input).type());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "\t", "unknown", "help now", "list all", "bye later"})
    public void parse_emptyUnknownOrUnexpectedArguments_throwsSerinaException(String input) {
        assertThrows(SerinaException.class, () -> CommandParser.parse(input));
    }

    @Test
    public void parse_nullInput_reportsEmptyCommand() {
        SerinaException exception = assertThrows(SerinaException.class, () -> CommandParser.parse(null));

        assertEquals(SerinaError.EMPTY_COMMAND.getMessage(), exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"mark", "mark 0", "mark -1", "mark +1", "mark 1.0", "mark one", "mark 1 2"})
    public void parse_invalidTaskNumber_reportsTaskNumberError(String input) {
        SerinaException exception = assertThrows(SerinaException.class, () -> CommandParser.parse(input));

        assertEquals(SerinaError.INVALID_TASK_NUMBER.getMessage(), exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "deadline", "deadline /by 2026-09-14", "deadline report /by",
        "deadline report /from 2026-09-14", "deadline report /by 2026-09-14 extra",
        "event", "event /from 2026-09-14 /to 2026-09-15", "event meeting /from /to 2026-09-15",
        "event meeting /from 2026-09-14 /to", "event meeting /to 2026-09-15 /from 2026-09-14",
        "event meeting /by 2026-09-14"
    })
    public void parse_missingOrMisplacedDateParameters_throwsSerinaException(String input) {
        assertThrows(SerinaException.class, () -> CommandParser.parse(input));
    }

    @Test
    public void parsedCommand_argumentsAreImmutableAndCopied() throws SerinaException {
        List<String> mutableArguments = new java.util.ArrayList<>(List.of("text"));
        ParsedCommand command = new ParsedCommand(CommandType.TODO, mutableArguments);
        mutableArguments.add("changed");

        assertEquals(List.of("text"), command.arguments());
        assertThrows(UnsupportedOperationException.class, () -> command.arguments().add("extra"));
        assertTrue(CommandParser.parse("todo pipe | slash \\ punctuation!").arguments().get(0).contains("|"));
    }
}
