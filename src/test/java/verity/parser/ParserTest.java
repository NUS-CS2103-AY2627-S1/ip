package verity.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import verity.client.ClientList;
import verity.command.AddCommand;
import verity.command.ClientListCommand;
import verity.command.Command;
import verity.command.CommandContext;
import verity.command.DeleteCommand;
import verity.command.ExitCommand;
import verity.command.FindCommand;
import verity.command.FindDateCommand;
import verity.command.HelpCommand;
import verity.command.ListCommand;
import verity.command.MarkCommand;
import verity.command.UnmarkCommand;
import verity.exception.VerityException;
import verity.storage.ClientStorage;
import verity.storage.Storage;
import verity.task.Deadline;
import verity.task.Event;
import verity.task.Task;
import verity.task.TaskList;
import verity.task.Todo;
import verity.ui.Ui;

/**
 * Tests user-command parsing and saved-task reconstruction.
 */
class ParserTest {
    private final Parser parser = new Parser();

    @TempDir
    Path temporaryDirectory;

    @Test
    void parse_exitCommand_returnsExitCommand() throws VerityException {
        Command command = parser.parse("bye", 0);

        assertInstanceOf(ExitCommand.class, command);
        assertTrue(command.isExit());
    }

    @Test
    void parse_helpCommand_returnsHelpCommand() throws VerityException {
        Command command = parser.parse("help", 0);

        assertInstanceOf(HelpCommand.class, command);
    }

    @Test
    void parse_helpWithArgument_throwsVerityException() {
        VerityException exception = assertThrows(
                VerityException.class,
                () -> parser.parse("help tasks", 0));

        assertEquals(
                "The help command does not accept arguments.",
                exception.getMessage());
    }

    @Test
    void parse_nonMutatingCommands_returnsCorrectCommandTypes()
            throws VerityException {
        assertInstanceOf(
                ListCommand.class,
                parser.parse("list", 0));
        assertInstanceOf(
                FindCommand.class,
                parser.parse("find book", 0));
        assertInstanceOf(
                FindDateCommand.class,
                parser.parse("finddate 2026-08-10", 0));
        assertInstanceOf(
                ClientListCommand.class,
                parser.parse("client list", 0));
        assertInstanceOf(
                MarkCommand.class,
                parser.parse("mark 1", 1));
        assertInstanceOf(
                UnmarkCommand.class,
                parser.parse("unmark 1", 1));
        assertInstanceOf(
                DeleteCommand.class,
                parser.parse("delete 1", 1));
    }

    @Test
    void parse_todoWithClients_addsAssociatedTask()
            throws IOException, VerityException {
        Command command = parser.parse(
                "todo prepare invoice /client C001 /client C002", 0);
        TaskList tasks = new TaskList();
        ClientList clients = new ClientList();
        clients.addNewClient("Alice Tan", "", "", "", "", "", null);
        clients.addNewClient("Bob Lee", "", "", "", "", "", null);
        CommandContext context = new CommandContext(
                tasks,
                clients,
                new Ui(),
                new Storage(temporaryDirectory.resolve("associated-task.txt")),
                new ClientStorage(temporaryDirectory.resolve("clients.txt")));

        command.execute(context);

        assertEquals(List.of("C001", "C002"), tasks.get(0).getClientIds());
    }

    @Test
    void parse_todoCommand_createsTodoAddCommand()
            throws IOException, VerityException {
        Command command = parser.parse("todo read book", 0);
        TaskList tasks = new TaskList();

        CommandContext context = new CommandContext(
                tasks,
                new Ui(),
                new Storage(
                        temporaryDirectory.resolve("todo.txt")));

        command.execute(context);

        assertInstanceOf(AddCommand.class, command);
        assertEquals("[T][ ] read book", tasks.get(0).getStatus());
    }

    @Test
    void parse_deadlineCommand_createsDeadlineAddCommand()
            throws IOException, VerityException {
        Command command = parser.parse(
                "deadline submit report /by 2026-08-10", 0);
        TaskList tasks = new TaskList();

        CommandContext context = new CommandContext(
                tasks,
                new Ui(),
                new Storage(
                        temporaryDirectory.resolve("deadline.txt")));

        command.execute(context);

        assertInstanceOf(AddCommand.class, command);
        assertEquals(
                "[D][ ] submit report (by: Aug 10 2026)",
                tasks.get(0).getStatus());
    }

    @Test
    void parse_eventCommand_createsEventAddCommand()
            throws IOException, VerityException {
        Command command = parser.parse(
                "event project meeting /from 2026-08-10 /to 2026-08-12",
                0);
        TaskList tasks = new TaskList();

        CommandContext context = new CommandContext(
                tasks,
                new Ui(),
                new Storage(
                        temporaryDirectory.resolve("event.txt")));

        command.execute(context);

        assertInstanceOf(AddCommand.class, command);
        assertEquals(
                "[E][ ] project meeting (from: Aug 10 2026 to: Aug 12 2026)",
                tasks.get(0).getStatus());
    }

    @Test
    void parse_unknownCommand_throwsVerityException() {
        VerityException exception = assertThrows(
                VerityException.class,
                () -> parser.parse("unknown", 0));

        assertEquals("I don't know that command.", exception.getMessage());
    }

    @Test
    void parse_blankCommand_throwsVerityException() {
        VerityException exception = assertThrows(
                VerityException.class,
                () -> parser.parse("   ", 0));

        assertEquals("Please enter a command.", exception.getMessage());
    }

    @Test
    void parse_taskCommandWithFlexibleSpacing_returnsCommand()
            throws VerityException {
        Command command = parser.parse("  todo   read   book  ", 0);

        assertInstanceOf(AddCommand.class, command);
    }

    @Test
    void parse_taskCommandWithUnknownMarker_throwsVerityException() {
        VerityException exception = assertThrows(
                VerityException.class,
                () -> parser.parse("todo read book /due 2026-08-10", 0));

        assertEquals("Unknown task marker '/due'.", exception.getMessage());
    }

    @Test
    void parse_todoWithDateMarker_throwsVerityException() {
        VerityException exception = assertThrows(
                VerityException.class,
                () -> parser.parse("todo read book /by 2026-08-10", 0));

        assertEquals(
                "The /by marker is not valid for a todo command.",
                exception.getMessage());
    }

    @Test
    void parse_deadlineWithRepeatedDateMarker_throwsVerityException() {
        VerityException exception = assertThrows(
                VerityException.class,
                () -> parser.parse(
                        "deadline report /by 2026-08-10 /by 2026-08-11", 0));

        assertEquals("The /by marker cannot be repeated.", exception.getMessage());
    }

    @Test
    void parse_eventWithRepeatedDateMarker_throwsVerityException() {
        VerityException exception = assertThrows(
                VerityException.class,
                () -> parser.parse(
                        "event meeting /from 2026-08-10 /to 2026-08-11"
                                + " /to 2026-08-12",
                        0));

        assertEquals("The /to marker cannot be repeated.", exception.getMessage());
    }

    @Test
    void parse_eventWithReversedMarkers_throwsVerityException() {
        VerityException exception = assertThrows(
                VerityException.class,
                () -> parser.parse(
                        "event meeting /to 2026-08-11 /from 2026-08-10", 0));

        assertEquals(
                "The /from marker must come before the /to marker.",
                exception.getMessage());
    }

    @Test
    void parse_repeatedClientMarkerWithoutFirstId_throwsVerityException() {
        VerityException exception = assertThrows(
                VerityException.class,
                () -> parser.parse(
                        "todo prepare invoice /client /client C001", 0));

        assertEquals("The /client value cannot be empty.", exception.getMessage());
    }

    @Test
    void parse_invalidTaskNumber_throwsVerityException() {
        VerityException exception = assertThrows(
                VerityException.class,
                () -> parser.parse("delete 0", 1));

        assertEquals(
                "That task number does not exist.", exception.getMessage());
    }

    @Test
    void parse_missingTaskNumber_throwsVerityException() {
        VerityException exception = assertThrows(
                VerityException.class,
                () -> parser.parse("mark", 1));

        assertEquals("Please provide a task number.", exception.getMessage());
    }

    @Test
    void parse_missingTodoDescription_throwsVerityException() {
        VerityException exception = assertThrows(
                VerityException.class,
                () -> parser.parse("todo", 0));

        assertEquals(
                "The description of a todo cannot be empty.",
                exception.getMessage());
    }

    @Test
    void parse_invalidDeadlineDate_throwsVerityException() {
        VerityException exception = assertThrows(
                VerityException.class,
                () -> parser.parse(
                        "deadline submit report /by 10-08-2026", 0));

        assertEquals(
                "Dates must use the format yyyy-MM-dd.",
                exception.getMessage());
    }

    @Test
    void parse_nonexistentDeadlineDate_throwsVerityException() {
        VerityException exception = assertThrows(
                VerityException.class,
                () -> parser.parse(
                        "deadline submit report /by 2026-02-30", 0));

        assertEquals(
                "Dates must use the format yyyy-MM-dd.",
                exception.getMessage());
    }

    @Test
    void parse_reversedEventDates_throwsVerityException() {
        VerityException exception = assertThrows(
                VerityException.class,
                () -> parser.parse(
                        "event meeting /from 2026-08-12 /to 2026-08-10",
                        0));

        assertEquals(
                "The event end date cannot be before the start date.",
                exception.getMessage());
    }

    @Test
    void parse_findWithoutKeyword_throwsVerityException() {
        VerityException exception = assertThrows(
                VerityException.class,
                () -> parser.parse("find", 0));

        assertEquals(
                "Please provide a keyword to search for.",
                exception.getMessage());
    }

    @Test
    void parse_invalidFindDateCommand_throwsVerityException() {
        VerityException exception = assertThrows(
                VerityException.class,
                () -> parser.parse("finddate book", 0));

        assertEquals(
                "Dates must use the format yyyy-MM-dd.",
                exception.getMessage());
    }

    @Test
    void parseSavedTasks_validLines_reconstructAllTaskTypes()
            throws VerityException {
        List<Task> tasks = parser.parseSavedTasks(List.of(
                "T\t0\tread book",
                "D\t1\tsubmit report\t2026-08-10",
                "E\t0\tproject meeting\t2026-08-10\t2026-08-12"
        ));

        assertInstanceOf(Todo.class, tasks.get(0));
        assertInstanceOf(Deadline.class, tasks.get(1));
        assertInstanceOf(Event.class, tasks.get(2));
        assertEquals("[T][ ] read book", tasks.get(0).getStatus());
        assertEquals(
                "[D][X] submit report (by: Aug 10 2026)",
                tasks.get(1).getStatus());
        assertEquals(
                "[E][ ] project meeting (from: Aug 10 2026 to: Aug 12 2026)",
                tasks.get(2).getStatus());
    }

    @Test
    void parseSavedTasks_extendedLine_restoresClientMetadata()
            throws VerityException {
        List<Task> tasks = parser.parseSavedTasks(List.of(
                "T\t0\tprepare invoice\tC002,C001"
                        + "\tFormer client Carol Lim (C003) was deleted."));

        assertEquals(List.of("C001", "C002"), tasks.get(0).getClientIds());
        assertEquals(
                List.of("Former client Carol Lim (C003) was deleted."),
                tasks.get(0).getFormerClientNotes());
    }

    @Test
    void parseSavedTasks_emptyLines_returnsEmptyList()
            throws VerityException {
        assertEquals(List.of(), parser.parseSavedTasks(List.of()));
    }

    @Test
    void parseSavedTasks_corruptedLineIncludesLineNumber() {
        VerityException exception = assertThrows(
                VerityException.class,
                () -> parser.parseSavedTasks(List.of(
                        "T\t0\tread book",
                        "X\t0\tunknown task"
                )));

        assertEquals(
                "Line 2: unknown task type 'X'.",
                exception.getMessage());
    }

    @Test
    void parseSavedTasks_invalidStatus_throwsVerityException() {
        VerityException exception = assertThrows(
                VerityException.class,
                () -> parser.parseSavedTasks(List.of("T\t2\tread book")));

        assertEquals(
                "Line 1: completion status must be 0 or 1.",
                exception.getMessage());
    }

    @Test
    void parseSavedTasks_blankField_throwsVerityException() {
        VerityException exception = assertThrows(
                VerityException.class,
                () -> parser.parseSavedTasks(List.of("T\t0\t")));

        assertEquals(
                "Line 1: task fields cannot be empty.",
                exception.getMessage());
    }

    @Test
    void parse_byeWithArgument_throwsVerityException() {
        VerityException exception = assertThrows(
                VerityException.class,
                () -> parser.parse("bye now", 0));

        assertEquals(
                "The bye command does not accept arguments.",
                exception.getMessage());
    }

    @Test
    void parse_listWithArgument_throwsVerityException() {
        VerityException exception = assertThrows(
                VerityException.class,
                () -> parser.parse("list all", 0));

        assertEquals(
                "The list command does not accept arguments.",
                exception.getMessage());
    }

    @Test
    void parse_taskNumberWithExtraArgument_throwsVerityException() {
        VerityException exception = assertThrows(
                VerityException.class,
                () -> parser.parse("mark 1 extra", 1));

        assertEquals(
                "A task command accepts exactly one task number.",
                exception.getMessage());
    }

    @Test
    void parse_nonNumericTaskNumber_throwsVerityException() {
        VerityException exception = assertThrows(
                VerityException.class,
                () -> parser.parse("mark one", 1));

        assertEquals("The task number must be a number.", exception.getMessage());
    }

    @Test
    void parse_deadlineWithMissingArguments_throwsHelpfulException() {
        assertParseFailure(
                "deadline /by 2026-08-10",
                "The description of a deadline cannot be empty.");
        assertParseFailure(
                "deadline submit report",
                "A deadline must include a /by date.");
        assertParseFailure(
                "deadline submit report /by",
                "The deadline date cannot be empty.");
    }

    @Test
    void parse_deadlineWithInvalidClientPlacement_throwsHelpfulException() {
        assertParseFailure(
                "deadline report /client C001 /by 2026-08-10",
                "Client markers must follow the deadline date.");
        assertParseFailure(
                "deadline report /by /client C001",
                "The deadline date cannot be empty.");
    }

    @Test
    void parse_eventWithMissingArguments_throwsHelpfulException() {
        assertParseFailure(
                "event /from 2026-08-10 /to 2026-08-11",
                "The description of an event cannot be empty.");
        assertParseFailure(
                "event meeting",
                "An event must include a /from date and a /to date.");
        assertParseFailure(
                "event meeting /from /to 2026-08-11",
                "The event's from date cannot be empty.");
        assertParseFailure(
                "event meeting /from 2026-08-10",
                "An event must include a /to date.");
        assertParseFailure(
                "event meeting /from 2026-08-10 /to",
                "The event's to date cannot be empty.");
    }

    @Test
    void parse_eventWithInvalidClientPlacement_throwsHelpfulException() {
        assertParseFailure(
                "event meeting /from 2026-08-10 /client C001 /to 2026-08-11",
                "Client markers must follow the event end date.");
        assertParseFailure(
                "event meeting /from 2026-08-10 /to /client C001",
                "The event's to date cannot be empty.");
    }

    @Test
    void parse_findDateWithMissingOrExtraArguments_throwsHelpfulException() {
        String expectedMessage =
                "Use finddate followed by a date in yyyy-MM-dd format.";
        assertParseFailure("finddate", expectedMessage);
        assertParseFailure("finddate 2026-08-10 extra", expectedMessage);
    }

    @Test
    void parse_clientAssociationWithMalformedSequence_throwsHelpfulException() {
        assertParseFailure(
                "todo invoice /client C001 unexpected",
                "Use /client followed by one client ID.");
        assertParseFailure(
                "todo invoice /client",
                "The /client value cannot be empty.");
    }

    private void assertParseFailure(String command, String expectedMessage) {
        VerityException exception = assertThrows(
                VerityException.class,
                () -> parser.parse(command, 2));

        assertEquals(expectedMessage, exception.getMessage());
    }
}
