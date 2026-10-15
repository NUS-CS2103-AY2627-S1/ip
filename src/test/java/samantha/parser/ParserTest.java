package samantha.parser;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import samantha.command.AddDeadlineCommand;
import samantha.command.AddEventCommand;
import samantha.command.AddNoteCommand;
import samantha.command.AddTodoCommand;
import samantha.command.DeleteCommand;
import samantha.command.DeleteNoteCommand;
import samantha.command.EditNoteCommand;
import samantha.command.ExitCommand;
import samantha.command.FindCommand;
import samantha.command.HelpCommand;
import samantha.command.ListCommand;
import samantha.command.ListNotesCommand;
import samantha.command.MarkCommand;
import samantha.command.UndoCommand;
import samantha.command.UnmarkCommand;
import samantha.exception.InputException;
import samantha.exception.UnknownCommandException;

class ParserTest {
    @ParameterizedTest
    @MethodSource("validCommands")
    void parse_validCommand_returnsExpectedCommandType(String input, Class<?> commandType)
            throws InputException {
        assertInstanceOf(commandType, Parser.parse(input));
    }

    private static Stream<Arguments> validCommands() {
        return Stream.of(
                Arguments.of("bye", ExitCommand.class),
                Arguments.of("help", HelpCommand.class),
                Arguments.of("LIST", ListCommand.class),
                Arguments.of("find book", FindCommand.class),
                Arguments.of("todo read book", AddTodoCommand.class),
                Arguments.of("deadline return book /by 2/12/2019", AddDeadlineCommand.class),
                Arguments.of("event meeting /from 2/12/2019 1400 /to 2/12/2019 1600", AddEventCommand.class),
                Arguments.of("mark 1", MarkCommand.class),
                Arguments.of("unmark 1", UnmarkCommand.class),
                Arguments.of("delete 1", DeleteCommand.class),
                Arguments.of("note remember this", AddNoteCommand.class),
                Arguments.of("notes", ListNotesCommand.class),
                Arguments.of("edit-note 1 replacement text", EditNoteCommand.class),
                Arguments.of("delete-note 1", DeleteNoteCommand.class),
                Arguments.of("undo", UndoCommand.class));
    }

    @Test
    void parse_unknownCommand_unknownCommandExceptionThrown() {
        assertThrows(UnknownCommandException.class, () -> Parser.parse("archive 1"));
    }

    @Test
    void parse_undoWithExtraArgument_inputExceptionThrown() {
        assertThrows(InputException.class, () -> Parser.parse("undo extra"));
    }

    @Test
    void splitCommandAndParseDescription_preserveCommandArguments() {
        String[] parts = Parser.splitCommand("todo buy groceries today");

        assertArrayEquals(new String[] {"todo", "buy", "groceries", "today"}, parts);
        assertEquals("buy groceries today", Parser.parseDescription(parts));
        assertEquals("buy groceries today", Parser.parseDescription("todo", "buy", "groceries", "today"));
    }

    @Test
    void parseFindKeyword_validKeyword_returnsTrimmedKeyword() throws InputException {
        assertEquals("book", Parser.parseFindKeyword(Parser.splitCommand("find book ")));
        assertEquals("book", Parser.parseFindKeyword("find", "book"));
    }

    @Test
    void parseFindKeyword_missingKeyword_inputExceptionThrown() {
        assertThrows(InputException.class, () ->
                Parser.parseFindKeyword(Parser.splitCommand("find")));
    }

    @Test
    void parseDeadlineDetails_validDetails_returnsDescriptionAndDeadline() throws InputException {
        assertArrayEquals(new String[] {"return book", "2/12/2019 1800"},
                Parser.parseDeadlineDetails(Parser.splitCommand("deadline return book /by 2/12/2019 1800")));
        assertArrayEquals(new String[] {"return book", "2/12/2019 1800"},
                Parser.parseDeadlineDetails("deadline", "return", "book", "/by", "2/12/2019", "1800"));
    }

    @Test
    void parseDeadlineDetails_missingMarker_inputExceptionThrown() {
        assertThrows(InputException.class, () ->
                Parser.parseDeadlineDetails(Parser.splitCommand("deadline return book")));
    }

    @Test
    void parseEventDetails_validDetails_returnsDescriptionAndSchedule() throws InputException {
        assertArrayEquals(new String[] {"meeting", "2/12/2019 1400", "2/12/2019 1600"},
                Parser.parseEventDetails(Parser.splitCommand(
                        "event meeting /from 2/12/2019 1400 /to 2/12/2019 1600")));
        assertArrayEquals(new String[] {"meeting", "2/12/2019 1400", "2/12/2019 1600"},
                Parser.parseEventDetails("event", "meeting", "/from", "2/12/2019", "1400", "/to",
                        "2/12/2019", "1600"));
    }

    @Test
    void parseEventDetails_missingMarker_inputExceptionThrown() {
        assertThrows(InputException.class, () ->
                Parser.parseEventDetails(Parser.splitCommand("event meeting /from 2/12/2019 1400")));
    }

    @Test
    void parseEventDetails_repeatedMarker_inputExceptionThrown() {
        assertThrows(InputException.class, () ->
                Parser.parseEventDetails(Parser.splitCommand(
                        "event meeting /from 2/12/2019 1400 /to 2/12/2019 1600 /from 2/12/2019 1500")));
    }

    @Test
    void parseEventDetails_emptyDescription_returnsBlankDescriptionSegment() throws InputException {
        assertArrayEquals(new String[] {"", "2/12/2019 1400", "2/12/2019 1600"},
                Parser.parseEventDetails(Parser.splitCommand(
                        "event /from 2/12/2019 1400 /to 2/12/2019 1600")));
    }

    @Test
    void parseTaskId_extraSpacesAroundId_returnsId() throws InputException {
        assertEquals(1, Parser.parseTaskId(Parser.splitCommand("mark  1")));
    }

    @Test
    void parseListDate_noArgument_returnsNull() throws InputException {
        assertNull(Parser.parseListDate(Parser.splitCommand("list")));
    }

    @Test
    void parseListDate_validDate_returnsDate() throws InputException {
        assertEquals(LocalDate.of(2019, 12, 2),
                Parser.parseListDate(Parser.splitCommand("list 2-12-2019")));
        assertEquals(LocalDate.of(2019, 12, 2), Parser.parseListDate("list", "2-12-2019"));
    }

    @Test
    void parseListDate_timeOrExtraArgument_inputExceptionThrown() {
        assertThrows(InputException.class, () ->
                Parser.parseListDate(Parser.splitCommand("list 2/12/2019 1800")));
        assertThrows(InputException.class, () ->
                Parser.parseListDate(Parser.splitCommand("list 2/12/2019 extra")));
    }

    @Test
    void parseTaskId_validId_returnsId() throws InputException {
        assertEquals(42, Parser.parseTaskId(Parser.splitCommand("mark 42")));
        assertEquals(42, Parser.parseTaskId("mark", "42"));
    }

    @Test
    void parseTaskId_missingNonNumericOrExtraArgument_inputExceptionThrown() {
        assertThrows(InputException.class, () -> Parser.parseTaskId(Parser.splitCommand("mark")));
        assertThrows(InputException.class, () -> Parser.parseTaskId(Parser.splitCommand("mark one")));
        assertThrows(InputException.class, () -> Parser.parseTaskId(Parser.splitCommand("mark 1 extra")));
    }

    @Test
    void parseNoteId_validId_returnsId() throws InputException {
        assertEquals(42, Parser.parseNoteId(Parser.splitCommand("delete-note 42")));
        assertEquals(42, Parser.parseNoteId("edit-note", "42", "replacement"));
    }

    @Test
    void parseNoteText_missingText_inputExceptionThrown() {
        assertThrows(InputException.class, () ->
                Parser.parseNoteText(Parser.splitCommand("edit-note 1")));
    }
}
