package avon.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import avon.command.AddCommand;
import avon.command.Command;
import avon.command.DeleteCommand;
import avon.command.ExitCommand;
import avon.command.FindCommand;
import avon.command.HelpCommand;
import avon.command.ListCommand;
import avon.command.MarkCommand;
import avon.command.UnmarkCommand;
import avon.exception.AvonException;
import avon.exception.InvalidTaskFormatException;
import avon.storage.Storage;
import avon.task.Deadline;
import avon.task.Event;
import avon.task.Task;
import avon.task.TaskList;
import avon.ui.Ui;

class ParserTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void parse_validTaskCommand_returnsAddCommand() throws AvonException {
        Command command = Parser.parse("todo read Hamlet");

        assertInstanceOf(AddCommand.class, command);
    }

    @Test
    void parse_supportedNonTaskCommands_returnSpecializedCommands() throws AvonException {
        assertInstanceOf(ListCommand.class, Parser.parse("list"));
        assertInstanceOf(FindCommand.class, Parser.parse("find Hamlet"));
        assertInstanceOf(MarkCommand.class, Parser.parse("mark 1"));
        assertInstanceOf(UnmarkCommand.class, Parser.parse("unmark 1"));
        assertInstanceOf(DeleteCommand.class, Parser.parse("delete 1"));
        assertInstanceOf(HelpCommand.class, Parser.parse("help"));
    }

    @Test
    void parse_byeCommand_returnsExitCommand() throws AvonException {
        Command command = Parser.parse("bye");

        assertInstanceOf(ExitCommand.class, command);
        assertTrue(command.isExit());
    }

    @Test
    void parse_commandsWithSurroundingOrSeparatorWhitespace_returnsCommands()
            throws AvonException {
        assertInstanceOf(AddCommand.class, Parser.parse("  todo read Hamlet  "));
        assertInstanceOf(MarkCommand.class, Parser.parse("mark\t1"));
        assertInstanceOf(ListCommand.class, Parser.parse("list   "));
    }

    @Test
    void parseTask_validDeadline_returnsDeadline() throws AvonException {
        Task task = parseAddedTask("deadline return book /by 2026-08-20 1800");

        assertInstanceOf(Deadline.class, task);
        assertEquals("[D][ ] return book (by: Aug 20 2026, 6:00PM)", task.toString());
    }

    @Test
    void parseTask_dateOnlyDeadline_returnsDeadlineWithoutTime() throws AvonException {
        Task task = parseAddedTask("deadline return book /by 2026-08-20");

        assertEquals("[D][ ] return book (by: Aug 20 2026)", task.toString());
    }

    @Test
    void parseTask_invalidDeadline_throwsInvalidTaskFormatException() {
        assertThrows(InvalidTaskFormatException.class, () ->
                Parser.parse("deadline return book /by tomorrow"));
    }

    @Test
    void parseTask_validEvent_returnsEvent() throws AvonException {
        Task task = parseAddedTask("event lecture /from 2026-08-20 1400 /to 2026-08-20 1600");

        assertInstanceOf(Event.class, task);
        assertEquals("[E][ ] lecture (from: Aug 20 2026, 2:00PM to: Aug 20 2026, 4:00PM)",
                task.toString());
    }

    @Test
    void parseTask_dateOnlyEvent_returnsEventWithoutTimes() throws AvonException {
        Task task = parseAddedTask("event conference /from 2026-08-20 /to 2026-08-21");

        assertEquals("[E][ ] conference (from: Aug 20 2026 to: Aug 21 2026)", task.toString());
    }

    @Test
    void parseTask_eventEndingBeforeStart_throwsInvalidTaskFormatException() {
        assertThrows(InvalidTaskFormatException.class, () -> Parser.parse(
                "event lecture /from 2026-08-20 1600 /to 2026-08-20 1400"));
    }

    @Test
    void parseTask_eventWithNoDuration_throwsInvalidTaskFormatException() {
        assertThrows(InvalidTaskFormatException.class, () -> Parser.parse(
                "event lecture /from 2026-08-20 1600 /to 2026-08-20 1600"));
    }

    @Test
    void parseTask_invalidEventDate_throwsInvalidTaskFormatException() {
        assertThrows(InvalidTaskFormatException.class, () -> Parser.parse(
                "event lecture /from 2026-02-30 1400 /to 2026-02-30 1600"));
    }

    @Test
    void parseTask_delimiterPrefixesInDescriptions_preservesDescriptions()
            throws AvonException {
        Task deadline = parseAddedTask("deadline study /byte encoding /by 2026-08-20");
        Task event = parseAddedTask("event prepare /today notes /from 2026-08-20 1400 /to 2026-08-20 1600");

        assertEquals("study /byte encoding", deadline.getDescription());
        assertEquals("prepare /today notes", event.getDescription());
    }

    private Task parseAddedTask(String commandText) throws AvonException {
        Command command = Parser.parse(commandText);
        TaskList taskList = new TaskList();
        Ui ui = new Ui(new ByteArrayInputStream(new byte[0]),
                new PrintStream(new ByteArrayOutputStream()));
        Storage storage = new Storage(temporaryDirectory.resolve("avon.txt"));

        command.execute(taskList, ui, storage);

        return taskList.getTask(0);
    }
}
