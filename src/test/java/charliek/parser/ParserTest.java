package charliek.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import charliek.command.AddCommand;
import charliek.command.DeleteCommand;
import charliek.command.ExitCommand;
import charliek.command.FindCommand;
import charliek.command.HelpCommand;
import charliek.command.ListCommand;
import charliek.command.MarkCommand;
import charliek.command.UnmarkCommand;
import charliek.exception.DuplicateParameterException;
import charliek.exception.DuplicateTaskException;
import charliek.exception.EmptyParameterException;
import charliek.exception.EmptyTaskDescriptionException;
import charliek.exception.InvalidCommandFormatException;
import charliek.exception.InvalidDateTimeException;
import charliek.exception.InvalidEventRangeException;
import charliek.exception.UnknownCommandException;
import charliek.model.Deadline;
import charliek.model.Event;
import charliek.model.TaskList;
import charliek.model.ToDo;
import charliek.storage.Storage;
import charliek.ui.Ui;

/**
 * Tests command parsing and validation before commands are executed.
 */
class ParserTest {
    @TempDir
    Path tempDirectory;

    private InputStream originalInput;
    private Parser parser;

    /**
     * Creates a parser with isolated task storage for each test.
     */
    @BeforeEach
    void setUp() {
        originalInput = System.in;
        System.setIn(new ByteArrayInputStream(new byte[0]));
        parser = new Parser(new TaskList(), new Ui(),
                new Storage(tempDirectory.resolve("tasks.csv")));
    }

    /**
     * Restores the process-wide input stream after constructing the UI dependency.
     */
    @AfterEach
    void restoreInput() {
        System.setIn(originalInput);
    }

    /**
     * Verifies to-do parsing trims descriptions and rejects blank descriptions.
     */
    @Test
    void parseToDo_validAndBlankDescriptions_returnsTaskOrThrows() throws Exception {
        ToDo task = parser.parseToDo("  read book  ");

        assertEquals("read book", task.getStorageFields().get(0));
        assertThrows(EmptyTaskDescriptionException.class, () -> parser.parseToDo("   "));
    }

    /**
     * Verifies valid deadline parsing and its required-parameter validation.
     */
    @Test
    void parseDeadline_validAndMissingParameter_returnsTaskOrThrows() throws Exception {
        Deadline deadline = parser.parseDeadline(" return book /by 2019-12-02 ");

        assertEquals("return book", deadline.getStorageFields().get(0));
        assertEquals("2019-12-02", deadline.getStorageFields().get(1));
        assertThrows(EmptyTaskDescriptionException.class, () -> parser.parseDeadline("   "));
        assertThrows(EmptyTaskDescriptionException.class, () -> parser.parseDeadline(" /by 2019-12-02"));
        assertThrows(EmptyParameterException.class, () -> parser.parseDeadline("return book"));
        assertThrows(EmptyParameterException.class, () -> parser.parseDeadline("return book /by"));
        assertThrows(InvalidDateTimeException.class, () -> parser.parseDeadline("return book /by not-a-date"));
    }

    /**
     * Verifies valid event parsing and validation of its two date parameters.
     */
    @Test
    void parseEvent_validAndMissingParameter_returnsTaskOrThrows() throws Exception {
        Event event = parser.parseEvent(
                " project meeting /from 2019-12-02 14:00 /to 2019-12-02 15:00 ");

        assertEquals("project meeting", event.getStorageFields().get(0));
        assertEquals("2019-12-02T14:00:00", event.getStorageFields().get(1));
        assertEquals("2019-12-02T15:00:00", event.getStorageFields().get(2));
        assertThrows(EmptyTaskDescriptionException.class, () -> parser.parseEvent("   "));
        assertThrows(EmptyTaskDescriptionException.class, () -> parser.parseEvent(
                " /from 2019-12-02 /to 2019-12-03"));
        assertThrows(EmptyParameterException.class, () -> parser.parseEvent("project meeting"));
        assertThrows(EmptyParameterException.class, () -> parser.parseEvent(
                "project meeting /to 2019-12-03"));
        assertThrows(EmptyParameterException.class, () -> parser.parseEvent(
                "project meeting /from /to 2019-12-03"));
        assertThrows(EmptyParameterException.class, () -> parser.parseEvent(
                "project meeting /from 2019-12-02 /to"));
        assertThrows(InvalidDateTimeException.class, () -> parser.parseEvent(
                "project meeting /from invalid /to 2019-12-03"));
    }

    /**
     * Verifies that complete command lines produce the expected command types.
     */
    @Test
    void parse_supportedCommands_returnsMatchingCommandObjects() throws Exception {
        assertInstanceOf(ExitCommand.class, parser.parse("bye"));
        assertInstanceOf(ListCommand.class, parser.parse("list time"));
        assertInstanceOf(MarkCommand.class, parser.parse("mark 1"));
        assertInstanceOf(UnmarkCommand.class, parser.parse("unmark 1"));
        assertInstanceOf(DeleteCommand.class, parser.parse("delete 1"));
        assertInstanceOf(FindCommand.class, parser.parse("find book"));
        assertInstanceOf(HelpCommand.class, parser.parse("help"));
        assertInstanceOf(HelpCommand.class, parser.parse("help list"));
        assertInstanceOf(AddCommand.class, parser.parse("todo read book"));
        assertInstanceOf(AddCommand.class,
                parser.parse("deadline return book /by 2019-12-02"));
        assertInstanceOf(AddCommand.class,
                parser.parse("event meeting /from 2019-12-02 /to 2019-12-03"));
    }

    /**
     * Verifies that unknown commands are reported before command creation.
     */
    @Test
    void parse_unknownCommand_throwsUnknownCommandException() {
        assertThrows(UnknownCommandException.class, () -> parser.parse("unknown"));
        assertThrows(InvalidCommandFormatException.class, () -> parser.parse(null));
    }

    /**
     * Verifies malformed command spacing and argument shapes are rejected early.
     */
    @Test
    void parse_malformedSpacingAndArguments_throwsFormatException() {
        assertThrows(InvalidCommandFormatException.class, () -> parser.parse(" todo read book"));
        assertThrows(InvalidCommandFormatException.class, () -> parser.parse("todo read book "));
        assertThrows(InvalidCommandFormatException.class, () -> parser.parse("todo  read book"));
        assertThrows(InvalidCommandFormatException.class, () -> parser.parse("todo read\tbook"));
        assertThrows(InvalidCommandFormatException.class, () -> parser.parse("todo read\u00A0book"));
        assertThrows(InvalidCommandFormatException.class, () -> parser.parse("mark +1"));
        assertThrows(InvalidCommandFormatException.class, () -> parser.parse("find book now"));
        assertThrows(InvalidCommandFormatException.class, () -> parser.parse("list unsupported"));
        assertThrows(InvalidCommandFormatException.class, () -> parser.parse("todo buy /by milk"));
    }

    /**
     * Verifies duplicate parameters and reversed event parameters are rejected.
     */
    @Test
    void parse_duplicateOrMisorderedParameters_throwsSpecificException() {
        assertThrows(DuplicateParameterException.class, () -> parser.parse(
                "deadline submit report /by 2019-12-02 /by 2019-12-03"));
        assertThrows(DuplicateParameterException.class, () -> parser.parse(
                "event meeting /from 2019-12-02 /from 2019-12-03 /to 2019-12-04"));
        assertThrows(InvalidCommandFormatException.class, () -> parser.parse(
                "event meeting /to 2019-12-03 /from 2019-12-02"));
    }

    /**
     * Verifies events reject endpoints that do not form a forward time range.
     */
    @Test
    void parse_eventWithInvalidRange_throwsRangeException() {
        assertThrows(InvalidEventRangeException.class, () -> parser.parse(
                "event meeting /from 2019-12-03 /to 2019-12-02"));
        assertThrows(InvalidEventRangeException.class, () -> parser.parse(
                "event meeting /from 2019-12-02 14:00 /to 2019-12-02 14:00"));
    }

    /**
     * Verifies that adding a task with identical details is rejected.
     */
    @Test
    void parse_duplicateTask_throwsDuplicateTaskException() throws Exception {
        parser.parse("todo read book").execute();

        assertThrows(DuplicateTaskException.class, () -> parser.parse("todo read book"));
    }

    /**
     * Verifies that parser collaborators are required for safe command construction.
     */
    @Test
    void parser_nullCollaborator_throwsNullPointerException() {
        Storage storage = new Storage(tempDirectory.resolve("tasks.csv"));
        Ui ui = new Ui(ignored -> { });
        TaskList tasks = new TaskList();

        assertThrows(NullPointerException.class, () -> new Parser(null, ui, storage));
        assertThrows(NullPointerException.class, () -> new Parser(tasks, null, storage));
        assertThrows(NullPointerException.class, () -> new Parser(tasks, ui, null));
    }

    /**
     * Verifies missing and malformed arguments for commands with numeric or single-word arguments.
     */
    @Test
    void parse_missingOrMalformedSimpleArguments_throwsExpectedException() {
        assertThrows(EmptyParameterException.class, () -> parser.parse("mark"));
        assertThrows(EmptyParameterException.class, () -> parser.parse("delete"));
        assertThrows(EmptyParameterException.class, () -> parser.parse("find"));
        assertThrows(InvalidCommandFormatException.class, () -> parser.parse("mark 1.0"));
        assertThrows(InvalidCommandFormatException.class, () -> parser.parse("delete -1"));
        assertThrows(InvalidCommandFormatException.class, () -> parser.parse("find two words"));
        assertThrows(InvalidCommandFormatException.class, () -> parser.parse("help two words"));
    }

    /**
     * Verifies that every supported whitespace and control-character failure is rejected.
     */
    @Test
    void parse_unexpectedWhitespaceAndControlCharacters_throwsFormatException() {
        assertThrows(InvalidCommandFormatException.class, () -> parser.parse("\tlist"));
        assertThrows(InvalidCommandFormatException.class, () -> parser.parse("list\u00a0time"));
        assertThrows(InvalidCommandFormatException.class, () -> parser.parse("list\u007ftime"));
        assertThrows(InvalidCommandFormatException.class, () -> parser.parse("list\u0001"));
        assertThrows(InvalidCommandFormatException.class, () -> parser.parse("   "));
    }

    /**
     * Verifies that parameter markers embedded in words are not mistaken for syntax markers.
     */
    @Test
    void parse_parameterTextInsideWords_isHandledAsTaskTextOrMissingParameter() throws Exception {
        assertEquals("buy /bypass milk", parser.parseToDo("buy /bypass milk").getDescription());
        assertThrows(EmptyParameterException.class, () -> parser.parseDeadline("submit /bypass report"));
        assertThrows(EmptyParameterException.class, () -> parser.parseEvent("meeting /fromage /today"));
    }

    /**
     * Verifies event parsing reports each missing marker/value and rejects incompatible markers.
     */
    @Test
    void parseEvent_missingOrIncompatibleParameters_throwsSpecificException() {
        assertThrows(EmptyParameterException.class, () -> parser.parseEvent("meeting /from 2019-12-02"));
        assertThrows(EmptyParameterException.class, () -> parser.parseEvent("meeting /to 2019-12-03"));
        assertThrows(EmptyParameterException.class, () -> parser.parseEvent("meeting /from /to 2019-12-03"));
        assertThrows(InvalidCommandFormatException.class, () -> parser.parseEvent(
                "meeting /from 2019-12-02 /to 2019-12-03 /by 2019-12-04"));
        assertThrows(InvalidCommandFormatException.class, () -> parser.parseEvent(
                "meeting /to 2019-12-03 /from 2019-12-02"));
    }

    /**
     * Verifies deadline parsing distinguishes unsupported event syntax from missing dates.
     */
    @Test
    void parseDeadline_incompatibleOrMissingDate_throwsSpecificException() {
        assertThrows(InvalidCommandFormatException.class, () -> parser.parseDeadline(
                "submit report /by 2019-12-02 /from 2019-12-01"));
        assertThrows(EmptyParameterException.class, () -> parser.parseDeadline("submit report /by   "));
        assertThrows(EmptyTaskDescriptionException.class, () -> parser.parseDeadline("/by 2019-12-02"));
    }
}
