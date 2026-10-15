package dingleberry.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import dingleberry.command.AddCommand;
import dingleberry.command.Command;
import dingleberry.command.DeleteCommand;
import dingleberry.command.ExitCommand;
import dingleberry.command.FindCommand;
import dingleberry.command.ListCommand;
import dingleberry.command.MarkCommand;
import dingleberry.command.UnmarkCommand;
import dingleberry.exception.DingleberryException;
import dingleberry.model.Task;
import dingleberry.model.TaskList;
import dingleberry.persistence.Storage;
import dingleberry.ui.Ui;

/** Tests command recognition and validation performed by {@link Parser}. */
class ParserTest {
    @Test
    void parse_taskArguments_preservesTaskContent(@TempDir final Path tempDir)
            throws DingleberryException {
        Path taskFile = tempDir.resolve("tasks.txt");
        assertEquals("T | 0 | read notes",
                parseAndExecute("  TODO   read   notes  ", taskFile)
                        .toSaveFormat());
        assertEquals("D | 0 | report | 2026-08-25T18:00:00",
                parseAndExecute("DEADLINE  report /BY 2026-08-25 1800",
                        taskFile)
                        .toSaveFormat());
        assertEquals("E | 0 | meeting | 2026-08-25T14:00:00"
                        + " | 2026-08-25T16:00:00",
                parseAndExecute("EVENT  meeting /FROM 2026-08-25 1400 /TO 4pm",
                        taskFile).toSaveFormat());
    }

    @Test
    void parseByeReturnsExitCommand() throws DingleberryException {
        Command command = Parser.parse("bye");

        assertInstanceOf(ExitCommand.class, command);
        assertTrue(command.isExit());
    }

    @Test
    void parseListReturnsListCommand() throws DingleberryException {
        Command command = Parser.parse("list");

        assertInstanceOf(ListCommand.class, command);
        assertFalse(command.isExit());
    }

        @Test
        void parseCommandKeywordIgnoresCase() throws DingleberryException {
                Command command = Parser.parse("LIST");

                assertInstanceOf(ListCommand.class, command);
        }

    @Test
    void parseTodoWithDescriptionReturnsAddCommand()
            throws DingleberryException {
        Command command = Parser.parse("todo read lecture notes");

        assertInstanceOf(AddCommand.class, command);
    }

        @Test
        void parseCommandWithHarmlessWhitespaceReturnsAddCommand()
            throws DingleberryException {
        Command command = Parser.parse(
                "  todo   read   lecture notes  ");

        assertInstanceOf(AddCommand.class, command);
    }

    @Test
    void parseDeadlineWithDateReturnsAddCommand() throws DingleberryException {
        Command command = Parser.parse(
                "deadline submit report /by 2026-08-25 1800");

        assertInstanceOf(AddCommand.class, command);
    }

    @Test
    void parseDeadlineWithNaturalWeekdayReturnsAddCommand()
            throws DingleberryException {
        Command command = Parser.parse(
                "deadline submit report /by Mon");

        assertInstanceOf(AddCommand.class, command);
    }

    @Test
    void parseEventWithTimesReturnsAddCommand() throws DingleberryException {
        Command command = Parser.parse(
                "event team meeting /from 2026-08-25 1400 /to"
                        + " 2026-08-25 1500");

        assertInstanceOf(AddCommand.class, command);
    }

    @Test
    void parseEventWithNaturalTimesReturnsAddCommand()
            throws DingleberryException {
        Command command = Parser.parse(
                "event team meeting /from next Mon 2pm /to 4pm");

        assertInstanceOf(AddCommand.class, command);
    }

    @Test
    void parseDeleteWithTaskNumberReturnsDeleteCommand()
            throws DingleberryException {
        Command command = Parser.parse("delete 2");

        assertInstanceOf(DeleteCommand.class, command);
    }

    @Test
    void parseMarkWithTaskNumberReturnsMarkCommand()
            throws DingleberryException {
        Command command = Parser.parse("mark 1");

        assertInstanceOf(MarkCommand.class, command);
        assertFalse(command.isExit());
    }

    @Test
    void parseMarkWithoutNumberThrowsIncorrectParameters() {
        assertThrows(DingleberryException.class, () -> Parser.parse("mark"));
    }

    @Test
    void parseMarkWithNonNumericNumberThrowsIncorrectParameters() {
        assertThrows(DingleberryException.class,
                () -> Parser.parse("mark one"));
    }

    @Test
    void parseUnmarkWithTaskNumberReturnsUnmarkCommand()
            throws DingleberryException {
        Command command = Parser.parse("unmark 1");

        assertInstanceOf(UnmarkCommand.class, command);
        assertFalse(command.isExit());
    }

    @Test
    void parseUnmarkWithoutNumberThrowsIncorrectParameters() {
        assertThrows(DingleberryException.class, () -> Parser.parse("unmark"));
    }

    @Test
    void parseUnmarkWithNonNumericNumberThrowsIncorrectParameters() {
        assertThrows(DingleberryException.class,
                () -> Parser.parse("unmark one"));
    }

    @Test
    void parseFindWithKeywordReturnsFindCommand() throws DingleberryException {
        Command command = Parser.parse("find lecture");

        assertInstanceOf(FindCommand.class, command);
    }

    @Test
    void parseBlankInputThrowsIncorrectParameters() {
        DingleberryException exception = assertThrows(
                DingleberryException.class, () -> Parser.parse("   "));

        assertEquals("Please give me a command or a task description.",
                exception.getMessage());
        assertFalse(exception.isWrongCommand());
    }

        @Test
        void parseNullInputThrowsIncorrectParameters() {
                DingleberryException exception = assertThrows(
                                DingleberryException.class,
                                () -> Parser.parse(null));

                assertEquals("Please give me a command or a task description.",
                                exception.getMessage());
        }

    @Test
    void parseUnknownCommandThrowsWrongCommand() {
        DingleberryException exception = assertThrows(
                DingleberryException.class,
                () -> Parser.parse("archive notes"));

        assertTrue(exception.isWrongCommand());
    }

    @Test
    void parseListWithParametersThrowsIncorrectParameters() {
        DingleberryException exception = assertThrows(
                DingleberryException.class, () -> Parser.parse("list now"));

        assertEquals("'list' does not accept parameters.",
                exception.getMessage());
    }

    @Test
    void parseTodoWithoutDescriptionThrowsIncorrectParameters() {
        DingleberryException exception = assertThrows(
                DingleberryException.class,
                () -> Parser.parse("todo   "));

        assertEquals("The todo needs a non-empty description and details.",
                exception.getMessage());
    }

    @Test
    void parseDeadlineWithoutDescriptionThrowsIncorrectParameters() {
        DingleberryException exception = assertThrows(
                DingleberryException.class,
                () -> Parser.parse("deadline /by 2026-08-25 1800"));

        assertEquals(
                "The deadline needs a non-empty description and details.",
                exception.getMessage());
    }

    @Test
    void parseEventWithoutDescriptionThrowsIncorrectParameters() {
        DingleberryException exception = assertThrows(
                DingleberryException.class,
                () -> Parser.parse("event /from 2026-08-25 1400"
                        + " /to 2026-08-25 1500"));

        assertEquals("The event needs a non-empty description and details.",
                exception.getMessage());
    }

    @Test
    void parseDeleteWithoutNumberThrowsIncorrectParameters() {
        assertThrows(DingleberryException.class, () -> Parser.parse("delete"));
    }

    @Test
    void parseDeleteWithNonNumericNumberThrowsIncorrectParameters() {
        assertThrows(DingleberryException.class,
                () -> Parser.parse("delete two"));
    }

    @Test
    void parseFindWithoutKeywordThrowsIncorrectParameters() {
        DingleberryException exception = assertThrows(
                DingleberryException.class, () -> Parser.parse("find   "));

        assertEquals("'find' needs a keyword.", exception.getMessage());
    }

    @Test
    void parseDeadlineWithoutDateThrowsIncorrectParameters() {
        DingleberryException exception = assertThrows(
                DingleberryException.class,
                () -> Parser.parse("deadline submit report"));

        assertEquals(
                "A deadline needs a description and '/by <date>' in the"
                        + " format yyyy-MM-dd HHmm, e.g. 2019-12-02 1800,"
                        + " or a weekday such as next Tue.",
                exception.getMessage());
    }

    @Test
    void parseDeadlineWithInvalidDateThrowsIncorrectParameters() {
        DingleberryException exception = assertThrows(
                DingleberryException.class,
                () -> Parser.parse("deadline submit report /by tomorrow"));

        assertEquals(
                "I couldn't understand that date/time. Use yyyy-MM-dd HHmm,"
                        + " a weekday such as Mon or next Tue, or a time"
                        + " such as 2:30pm.", exception.getMessage());
    }

    @Test
    void parseDeadlineWithNonexistentDateThrowsIncorrectParameters() {
        assertThrows(DingleberryException.class,
                () -> Parser.parse(
                        "deadline submit report /by 2026-02-30 1800"));
    }

    @Test
    void parseDeadlineWithDuplicateParameterThrowsIncorrectParameters() {
        assertThrows(DingleberryException.class,
                () -> Parser.parse(
                        "deadline submit report /by 2026-08-25 1800"
                                + " /by 2026-08-26 1800"));
    }

    @Test
    void parseDeadlineWithWrongParameterThrowsIncorrectParameters() {
        assertThrows(DingleberryException.class,
                () -> Parser.parse(
                        "deadline submit report /to 2026-08-25 1800"));
    }

    @Test
    void parseEventWithoutEndTimeThrowsIncorrectParameters() {
        DingleberryException exception = assertThrows(
                DingleberryException.class,
                () -> Parser.parse(
                        "event team meeting /from 2026-08-25 1400"));

        assertEquals(
                "An event needs a description, '/from <time>',"
                        + " and '/to <time>' in the format"
                        + " yyyy-MM-dd HHmm, e.g. 2019-12-02 1800,"
                        + " or a weekday such as next Tue.",
                exception.getMessage());
    }

    @Test
    void parseEventWithInvalidDateThrowsIncorrectParameters() {
        DingleberryException exception = assertThrows(
                DingleberryException.class,
                () -> Parser.parse(
                        "event team meeting /from tomorrow"
                                + " /to 2026-08-25 1500"));

        assertEquals(
                "I couldn't understand that date/time. Use yyyy-MM-dd HHmm,"
                        + " a weekday such as Mon or next Tue, or a time"
                        + " such as 2:30pm.", exception.getMessage());
    }

    @Test
    void parseEventWithReorderedParametersThrowsIncorrectParameters() {
        DingleberryException exception = assertThrows(
                DingleberryException.class,
                () -> Parser.parse(
                        "event team meeting /to 2026-08-25 1500"
                                + " /from 2026-08-25 1400"));

        assertEquals("An event needs '/from <time>' before '/to <time>'.",
                exception.getMessage());
    }

    @Test
    void parseEventWithEndBeforeStartThrowsIncorrectParameters() {
        DingleberryException exception = assertThrows(
                DingleberryException.class,
                () -> Parser.parse(
                        "event team meeting /from 2026-08-25 1500"
                                + " /to 2026-08-25 1400"));

        assertEquals("An event's end time must be after its start time.",
                exception.getMessage());
    }

    @Test
    void parseTaskWithNonPositiveNumberThrowsSpecificError() {
        DingleberryException exception = assertThrows(
                DingleberryException.class, () -> Parser.parse("delete 0"));

        assertEquals("'delete' needs a positive task number.",
                exception.getMessage());
    }

    @Test
    void parseTaskWithNonNumericNumberThrowsSpecificError() {
        DingleberryException exception = assertThrows(
                DingleberryException.class,
                () -> Parser.parse("delete one"));

        assertEquals("'delete' needs a numeric task number.",
                exception.getMessage());
    }

    @Test
    void parseTaskWithOverflowNumberThrowsSpecificError() {
        DingleberryException exception = assertThrows(
                DingleberryException.class,
                () -> Parser.parse("delete 999999999999999999999"));

        assertEquals("'delete' needs a numeric task number.",
                exception.getMessage());
    }

    /**
     * Executes the public parser's result against isolated task storage.
     *
     * @param input the raw command input.
     * @param taskFile the isolated persistence file.
     * @return the constructed task after execution.
     * @throws DingleberryException if parsing or execution fails validation.
     */
    private static Task parseAndExecute(final String input, final Path taskFile)
            throws DingleberryException {
        TaskList tasks = new TaskList();
        Parser.parse(input).execute(tasks, new Ui(),
                new Storage(taskFile.toString()));
        return tasks.get(0);
    }
}
