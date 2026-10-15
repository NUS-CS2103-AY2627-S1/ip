package zeus.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import zeus.command.AddCommand;
import zeus.command.CommandContext;
import zeus.command.DeleteCommand;
import zeus.command.ExitCommand;
import zeus.command.FindCommand;
import zeus.command.HelpCommand;
import zeus.command.ListCommand;
import zeus.command.MarkCommand;
import zeus.command.UnmarkCommand;
import zeus.exception.ZeusException;
import zeus.storage.Storage;
import zeus.task.Task;
import zeus.task.TaskList;
import zeus.ui.Ui;

/** Tests command recognition and validation performed by {@link Parser}. */
public class ParserTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void parse_supportedCommands_returnsCorrectCommandTypes() throws ZeusException {
        assertInstanceOf(ExitCommand.class, Parser.parse("bye"));
        assertInstanceOf(ListCommand.class, Parser.parse("list"));
        assertInstanceOf(HelpCommand.class, Parser.parse("help"));
        assertInstanceOf(FindCommand.class, Parser.parse("find book"));
        assertInstanceOf(MarkCommand.class, Parser.parse("mark 1"));
        assertInstanceOf(UnmarkCommand.class, Parser.parse("unmark 1"));
        assertInstanceOf(DeleteCommand.class, Parser.parse("delete 1"));
        assertInstanceOf(AddCommand.class, Parser.parse("todo read book"));
        assertInstanceOf(AddCommand.class,
                Parser.parse("deadline return book /by 2026-09-06"));
        assertInstanceOf(AddCommand.class,
                Parser.parse("event meeting /from 2026-09-07 /to 2026-09-08"));
    }

    @Test
    public void parse_taskCommands_executeToCreateCorrectTasks() throws ZeusException {
        TaskList tasks = new TaskList();
        Storage storage = new Storage(temporaryDirectory.resolve("data/zeus.txt").toString());
        Ui ui = new Ui();
        CommandContext context = new CommandContext(tasks, ui, storage);

        Parser.parse("todo read book").execute(context);
        Parser.parse("deadline return book /by 2026-09-06").execute(context);
        Parser.parse("event meeting /from 2026-09-07 /to 2026-09-08")
                .execute(context);

        List<String> savedForms = tasks.getTasks().stream()
                .map(Task::toDataString)
                .toList();
        assertEquals(List.of(
                "T | 0 | read book",
                "D | 0 | return book | 2026-09-06",
                "E | 0 | meeting | 2026-09-07 | 2026-09-08"), savedForms);
    }

    @Test
    public void parse_unknownOrPartialCommand_exceptionThrown() {
        String expectedMessage =
                "Even Zeus cannot understand that command. Type 'help' to see available commands.";
        assertParseError("", expectedMessage);
        assertParseError("blah", expectedMessage);
        assertParseError("marking 1", expectedMessage);
        assertParseError("HELP", expectedMessage);
        assertParseError("helpful", expectedMessage);
    }

    @Test
    public void parse_helpWithArguments_exceptionThrown() {
        assertParseError("help todo",
                "The help command does not take any arguments. Type 'help' on its own.");
    }

    @Test
    public void parse_findWithEmptyKeyword_exceptionThrown() {
        assertParseError("find", "Tell me what to find, for example 'find book'.");
        assertParseError("find   ", "Tell me what to find, for example 'find book'.");
    }

    @Test
    public void parse_numberedCommandWithMissingOrInvalidNumber_exceptionThrown() {
        assertParseError("mark", "Tell me which task to mark, for example 'mark 1'.");
        assertParseError("unmark   ", "Tell me which task to unmark, for example 'unmark 1'.");
        assertParseError("delete two", "The task number must be a whole number.");
        assertParseError("mark 1 2", "The task number must be a whole number.");
    }

    @Test
    public void parse_todoWithEmptyDescription_exceptionThrown() {
        assertParseError("todo", "A todo needs a description after 'todo'.");
        assertParseError("todo   ", "A todo needs a description after 'todo'.");
    }

    @Test
    public void parse_deadlineWithInvalidDetails_exceptionThrown() {
        assertParseError("deadline", "A deadline needs a description and '/by' date.");
        assertParseError("deadline return book", "A deadline needs a '/by' date.");
        assertParseError("deadline /by 2026-09-06",
                "A deadline needs a description before '/by'.");
        assertParseError("deadline return book /by", "A deadline needs a date after '/by'.");
        assertParseError("deadline return book /by 2025-02-29",
                "The deadline date must use yyyy-MM-dd, for example 2019-10-15.");
    }

    @Test
    public void parse_eventWithInvalidDetails_exceptionThrown() {
        assertParseError("event",
                "An event needs a description, '/from' start date, and '/to' end date.");
        assertParseError("event meeting", "An event needs a start date after '/from'.");
        assertParseError("event /from 2026-09-07 /to 2026-09-08",
                "An event needs a description before '/from'.");
        assertParseError("event meeting /from /to 2026-09-08",
                "An event needs a start date after '/from'.");
        assertParseError("event meeting /from 2026-09-07",
                "An event needs an end date after '/to'.");
        assertParseError("event meeting /from 2026-09-07 /to 2026-09-06",
                "The event end date cannot be before its start date.");
    }

    private static void assertParseError(String input, String expectedMessage) {
        ZeusException exception = assertThrows(ZeusException.class, () -> Parser.parse(input));
        assertEquals(expectedMessage, exception.getMessage());
    }
}
