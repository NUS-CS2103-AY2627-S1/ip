package remy.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import remy.command.Command;
import remy.command.ExitCommand;
import remy.command.FindCommand;
import remy.command.ListCommand;
import remy.command.SortCommand;
import remy.exception.RemyException;
import remy.storage.Storage;
import remy.task.Deadline;
import remy.task.Task;
import remy.task.TaskList;
import remy.task.Todo;
import remy.ui.Ui;

/**
 * Tests conversion of user input into commands and resulting task changes.
 *
 * @author LEE-wz
 */
class ParserTest {
    /**
     * A temporary directory for storing test data.
     */
    @TempDir
    Path temporaryDirectory;

    /**
     * Tests that the parse method correctly converts user input into the expected command types.
     */
    @Test
    void parse_exitListAndFindCommands_expectedCommandTypesReturned() {
        Parser parser = new Parser();

        Command exitCommand = parser.parse("bye");
        Command listCommand = parser.parse("list");
        Command findCommand = parser.parse("find book");

        assertInstanceOf(ExitCommand.class, exitCommand);
        assertTrue(exitCommand.isExit());
        assertInstanceOf(ListCommand.class, listCommand);
        assertFalse(listCommand.isExit());
        assertInstanceOf(FindCommand.class, findCommand);
        assertFalse(findCommand.isExit());
    }

    /**
     * Tests that parsing creates the expected tasks for task-creation commands.
     */
    @Test
    void parse_taskCreationCommands_expectedTasksAdded() {
        Parser parser = new Parser();
        TaskList tasks = new TaskList();

        execute(parser.parse("todo Read book"), tasks);
        execute(parser.parse("deadline Submit assignment /by 23/8/2026"), tasks);
        execute(parser.parse("event Project meeting /from 23/8/2026 1430 /to 23/8/2026 1600"), tasks);

        assertEquals(List.of(
                "[T][ ] Read book",
                "[D][ ] Submit assignment (by: Aug 23 2026)",
                "[E][ ] Project meeting (from: Aug 23 2026 14:30 to: Aug 23 2026 16:00)"),
                tasks.getTasks().stream().map(Task::toString).toList());
    }

    @Test
    void parse_taskCreationCommandsWithDateVariants_expectedDatesAdded() {
        Parser parser = new Parser();
        TaskList tasks = new TaskList();

        execute(parser.parse("deadline Submit assignment /by 23/8/2026 1430"), tasks);
        execute(parser.parse("event Project period /from 23/8/2026 /to 24/8/2026"), tasks);

        assertEquals(List.of(
                "[D][ ] Submit assignment (by: Aug 23 2026 14:30)",
                "[E][ ] Project period (from: Aug 23 2026 to: Aug 24 2026)"),
                tasks.getTasks().stream().map(Task::toString).toList());
    }

    @Test
    void parse_validSortCommands_expectedOrdersApplied() {
        Parser parser = new Parser();
        Task laterTask = new Deadline("Submit assignment", LocalDate.of(2026, 9, 25));
        Task earlierTask = new Deadline("Pay fees", LocalDate.of(2026, 9, 23));
        Task todo = new Todo("Buy ingredients");
        TaskList tasks = new TaskList(List.of(todo, laterTask, earlierTask));

        Command defaultSortCommand = parser.parse("sort /by date");
        assertInstanceOf(SortCommand.class, defaultSortCommand);
        execute(defaultSortCommand, tasks);
        assertEquals(List.of(earlierTask, laterTask, todo), tasks.getTasks());

        Command descendingSortCommand = parser.parse("sort /by date /order desc");
        assertInstanceOf(SortCommand.class, descendingSortCommand);
        execute(descendingSortCommand, tasks);
        assertEquals(List.of(laterTask, earlierTask, todo), tasks.getTasks());

        Command ascendingSortCommand = parser.parse("sort /by date /order asc");
        assertInstanceOf(SortCommand.class, ascendingSortCommand);
        execute(ascendingSortCommand, tasks);
        assertEquals(List.of(earlierTask, laterTask, todo), tasks.getTasks());
    }

    @Test
    void parse_invalidSortCommands_commonGuidanceReturned() {
        Parser parser = new Parser();
        String expectedMessage = "____________________________________________________________\n"
                + "Invalid sort command.\n"
                + "Use: sort /by date [/order asc|desc]\n"
                + "Example: sort /by date /order asc\n"
                + "____________________________________________________________\n";
        List<String> invalidCommands = List.of(
                "sort",
                "sort /by",
                "sort /by name",
                "sort /by deadline",
                "sort date",
                "sort /by date /order",
                "sort /by date /order ascending",
                "sort /by date /order ASC",
                "sort /order asc /by date",
                "sort /by date extra",
                "sort /by date /order asc extra",
                "sort /by date /order asc /order desc");

        for (String invalidCommand : invalidCommands) {
            RemyException exception = assertThrows(RemyException.class, () -> parser.parse(invalidCommand));
            assertEquals(expectedMessage, exception.getMessage());
        }
    }

    @Test
    void parse_sortKeywordWithoutCommandBoundary_unknownCommandReturned() {
        Parser parser = new Parser();

        RemyException exception = assertThrows(RemyException.class, () ->
                parser.parse("sorter /by date"));

        assertTrue(exception.getMessage().contains("no clue what you're talking about"));
    }

    @Test
    void parse_invalidCommandSpacing_actionableErrorThrown() {
        Parser parser = new Parser();
        List<String> invalidCommands = List.of(
                " todo Read book",
                "todo Read book ",
                "todo  Read book",
                "todo\tRead book",
                "todo\u00a0Read book",
                "deadline Submit report  /by 24/9/2026");

        for (String invalidCommand : invalidCommands) {
            RemyException exception = assertThrows(RemyException.class, () -> parser.parse(invalidCommand));
            assertTrue(exception.getMessage().contains("Invalid command spacing"));
            assertTrue(exception.getMessage().contains("one space"));
        }
    }

    @Test
    void parse_repeatedOrMisorderedParameters_formatErrorThrown() {
        Parser parser = new Parser();
        List<String> invalidDeadlineCommands = List.of(
                "deadline Submit report /by 24/9/2026 /by 25/9/2026",
                "deadline Submit report /when 24/9/2026");
        List<String> invalidEventCommands = List.of(
                "event Conference /from 24/9/2026 /from 25/9/2026 /to 26/9/2026",
                "event Conference /from 24/9/2026 /to 25/9/2026 /to 26/9/2026",
                "event Conference /to 25/9/2026 /from 24/9/2026",
                "event Conference /from 24/9/2026 /until 25/9/2026");

        for (String invalidCommand : invalidDeadlineCommands) {
            RemyException exception = assertThrows(RemyException.class, () -> parser.parse(invalidCommand));
            assertTrue(exception.getMessage().contains("Specify /by exactly once"));
        }
        for (String invalidCommand : invalidEventCommands) {
            RemyException exception = assertThrows(RemyException.class, () -> parser.parse(invalidCommand));
            assertTrue(exception.getMessage().contains("exactly once and in that order"));
        }
    }

    @Test
    void parse_unexpectedArgumentsAndSpecialIndexCharacters_formatErrorThrown() {
        Parser parser = new Parser();
        List<String> invalidIndexCommands = List.of("mark +1", "unmark 1!", "delete #1", "delete 1.0");

        RemyException listException = assertThrows(RemyException.class, () -> parser.parse("list extra"));
        RemyException byeException = assertThrows(RemyException.class, () -> parser.parse("bye now"));
        assertTrue(listException.getMessage().contains("does not accept parameters"));
        assertTrue(byeException.getMessage().contains("does not accept parameters"));

        for (String invalidCommand : invalidIndexCommands) {
            assertThrows(RemyException.class, () -> parser.parse(invalidCommand));
        }
        assertThrows(RemyException.class, () -> parser.parse("todoing Read book"));
        assertThrows(RemyException.class, () -> parser.parse("marking 1"));
    }

    @Test
    void parse_taskDescriptionContainsPunctuation_taskAdded() {
        Parser parser = new Parser();
        TaskList tasks = new TaskList();

        execute(parser.parse("todo Buy milk @ store #2!"), tasks);

        assertEquals("[T][ ] Buy milk @ store #2!", tasks.get(0).toString());
    }

    /**
     * Tests that index-based commands apply the expected task-list changes.
     */
    @Test
    void parse_indexCommands_expectedTaskChangesApplied() {
        Parser parser = new Parser();
        TaskList tasks = new TaskList(List.of(new Todo("Read book")));

        execute(parser.parse("mark 1"), tasks);
        assertEquals("[T][X] Read book", tasks.get(0).toString());

        execute(parser.parse("unmark 1"), tasks);
        assertEquals("[T][ ] Read book", tasks.get(0).toString());

        execute(parser.parse("delete 1"), tasks);
        assertTrue(tasks.isEmpty());
    }

    /**
     * Tests that the parse method throws a RemyException for unknown or invalid commands.
     */
    @Test
    void parse_unknownOrInvalidCommands_remyExceptionThrown() {
        Parser parser = new Parser();

        assertThrows(RemyException.class, () -> parser.parse(null));
        assertThrows(RemyException.class, () -> parser.parse("deadline submit /by tomorrow"));
        assertThrows(RemyException.class, () -> parser.parse("mark one"));
        assertThrows(RemyException.class, () -> parser.parse("mark 999999999999999999999999"));
    }

    @Test
    void parse_nonExistentDates_actionableErrorsThrown() {
        Parser parser = new Parser();
        String invalidDeadlineCommand = "deadline Submit report /by 30/2/2026";
        String invalidEventCommand = "event Conference /from 29/2/2025 /to 1/3/2025";

        RemyException deadlineException = assertThrows(RemyException.class, () ->
                parser.parse(invalidDeadlineCommand));
        RemyException eventException = assertThrows(RemyException.class, () -> parser.parse(invalidEventCommand));

        assertTrue(deadlineException.getMessage().contains("deadline date is invalid"));
        assertTrue(eventException.getMessage().contains("event dates are invalid"));
    }

    @Test
    void parse_eventEndpointsUseDifferentDateRepresentations_actionableErrorThrown() {
        Parser parser = new Parser();
        List<String> invalidCommands = List.of(
                "event Conference /from 24/9/2026 1500 /to 25/9/2026",
                "event Conference /from 24/9/2026 /to 25/9/2026 1500");

        for (String invalidCommand : invalidCommands) {
            RemyException exception = assertThrows(RemyException.class, () -> parser.parse(invalidCommand));
            assertTrue(exception.getMessage().contains("same supported format"));
        }
    }

    @Test
    void parse_eventStartIsNotBeforeEnd_actionableErrorThrown() {
        Parser parser = new Parser();
        List<String> invalidCommands = List.of(
                "event Conference /from 24/9/2026 /to 24/9/2026",
                "event Conference /from 25/9/2026 /to 24/9/2026",
                "event Conference /from 24/9/2026 1500 /to 24/9/2026 1500",
                "event Conference /from 24/9/2026 1600 /to 24/9/2026 1500");

        for (String invalidCommand : invalidCommands) {
            RemyException exception = assertThrows(RemyException.class, () -> parser.parse(invalidCommand));
            assertTrue(exception.getMessage().contains("must start before it ends"));
        }
    }

    @Test
    void execute_duplicateTask_errorThrownAndOriginalTaskRetained() {
        Parser parser = new Parser();
        TaskList tasks = new TaskList();
        String duplicateCommand = "deadline Submit report /by 24/9/2026";
        execute(parser.parse(duplicateCommand), tasks);

        RemyException exception = assertThrows(RemyException.class, () ->
                execute(parser.parse(duplicateCommand), tasks));

        assertTrue(exception.getMessage().contains("already exists"));
        assertEquals(1, tasks.size());
    }

    @Test
    void parse_commandsWithoutRequiredArguments_remyExceptionThrown() {
        Parser parser = new Parser();

        assertThrows(RemyException.class, () -> parser.parse("todo"));
        assertThrows(RemyException.class, () -> parser.parse("deadline"));
        assertThrows(RemyException.class, () -> parser.parse("event"));
        assertThrows(RemyException.class, () -> parser.parse("find"));
        assertThrows(RemyException.class, () -> parser.parse("mark"));
        assertThrows(RemyException.class, () -> parser.parse("unmark"));
        assertThrows(RemyException.class, () -> parser.parse("delete"));
    }

    @Test
    void parse_taskCommandsWithIndividualMissingDetails_missingDetailsIdentified() {
        Parser parser = new Parser();

        RemyException deadlineDescriptionException = assertThrows(RemyException.class, () ->
                parser.parse("deadline /by 24/9/2026"));
        RemyException deadlineDateException = assertThrows(RemyException.class, () ->
                parser.parse("deadline Submit report /by"));
        RemyException deadlineParameterException = assertThrows(RemyException.class, () ->
                parser.parse("deadline Submit report"));
        RemyException eventDescriptionException = assertThrows(RemyException.class, () ->
                parser.parse("event /from 24/9/2026 /to 25/9/2026"));
        RemyException eventStartException = assertThrows(RemyException.class, () ->
                parser.parse("event Conference /from /to 25/9/2026"));
        RemyException eventEndException = assertThrows(RemyException.class, () ->
                parser.parse("event Conference /from 24/9/2026 /to"));
        RemyException eventFromParameterException = assertThrows(RemyException.class, () ->
                parser.parse("event Conference /to 25/9/2026"));
        RemyException eventToParameterException = assertThrows(RemyException.class, () ->
                parser.parse("event Conference /from 24/9/2026"));
        RemyException eventParametersException = assertThrows(RemyException.class, () ->
                parser.parse("event Conference"));

        assertTrue(deadlineDescriptionException.getMessage().contains("description"));
        assertTrue(deadlineDateException.getMessage().contains("DEADLINE"));
        assertTrue(deadlineParameterException.getMessage().contains("DEADLINE"));
        assertTrue(eventDescriptionException.getMessage().contains("description"));
        assertTrue(eventStartException.getMessage().contains("starts"));
        assertTrue(eventEndException.getMessage().contains("ends"));
        assertTrue(eventFromParameterException.getMessage().contains("starts"));
        assertTrue(eventToParameterException.getMessage().contains("ends"));
        assertTrue(eventParametersException.getMessage().contains("starts"));
        assertTrue(eventParametersException.getMessage().contains("ends"));
    }

    /** Executes a parsed command using isolated persistence for the test. */
    private void execute(Command command, TaskList tasks) {
        Storage storage = new Storage(temporaryDirectory.resolve("tasks.txt").toString());
        command.execute(tasks, new Ui(), storage);
    }
}
