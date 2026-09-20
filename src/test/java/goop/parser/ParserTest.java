package goop.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Path;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import goop.command.Command;
import goop.command.FindCommand;
import goop.exception.GoopException;
import goop.storage.Storage;
import goop.task.Deadline;
import goop.task.Event;
import goop.task.Task;
import goop.task.TaskList;
import goop.ui.Ui;

/**
 * Tests command parsing through the public {@link Parser#parse(String)} method.
 */
class ParserTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    void parse_nonPositiveTaskNumbers_throwsGoopException() {
        for (String commandWord : new String[] {"mark", "unmark", "delete"}) {
            assertThrows(GoopException.class, () -> new Parser().parse(commandWord + " 0"));
            assertThrows(GoopException.class, () -> new Parser().parse(commandWord + " -1"));
        }
    }

    @Test
    void parse_validDayFirstDeadline_createsDeadlineWithParsedDateTime() throws Exception {
        Task task = parseAndExecute("deadline return book /by 2/12/2019 1800");

        Deadline deadline = assertInstanceOf(Deadline.class, task);
        assertEquals("return book", deadline.getDescription());
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), deadline.getBy());
    }

    @Test
    void parse_delimiterTextInsideDescription_usesOnlySeparateDelimiterToken()
            throws Exception {
        Task task = parseAndExecute(
                "event discuss /fromage recipe /from Monday 2pm /to Monday 4pm");

        Event event = assertInstanceOf(Event.class, task);
        assertEquals("discuss /fromage recipe", event.getDescription());
        assertEquals("Monday 2pm", event.getFrom());
        assertEquals("Monday 4pm", event.getTo());
    }

    @Test
    void parse_impossibleDeadlineDate_throwsHelpfulException() {
        GoopException error = assertThrows(GoopException.class, () ->
                new Parser().parse("deadline return book /by 31/2/2019 1800"));

        assertEquals("The deadline date and time must use d/M/yyyy HHmm "
                + "or yyyy-MM-dd HHmm. For example: deadline return book "
                + "/by 2/12/2019 1800.", error.getMessage());
    }

    @Test
    void parse_findWithKeyword_createsFindCommand() throws Exception {
        Command command = new Parser().parse("find book");

        assertInstanceOf(FindCommand.class, command);
    }

    @Test
    void parse_findWithoutKeyword_throwsHelpfulException() {
        GoopException error = assertThrows(GoopException.class, () ->
                new Parser().parse("find"));

        assertEquals("The find command needs a keyword. Use: find <keyword>.",
                error.getMessage());
    }

    @Test
    void parse_eventWithSeveralMissingFields_reportsDescriptionFirst() {
        GoopException error = assertThrows(GoopException.class, () ->
                new Parser().parse("event /from"));

        assertEquals("An event needs a description before '/from'. "
                + "Use: event <description> /from <start> /to <end>.", error.getMessage());
    }

    @Test
    void parse_eventWithEmptyStartAndEnd_reportsStartFirst() {
        GoopException error = assertThrows(GoopException.class, () ->
                new Parser().parse("event meeting /from /to"));

        assertEquals("An event needs a start time after '/from'. "
                + "Use: event <description> /from <start> /to <end>.", error.getMessage());
    }

    /** Parses and executes an add command so the task produced by the parser can be inspected. */
    private Task parseAndExecute(String input) throws Exception {
        TaskList tasks = new TaskList();
        Command command = new Parser().parse(input);
        Storage storage = new Storage(temporaryDirectory.resolve("tasks.txt"));

        command.execute(tasks, new Ui(), storage);
        assertEquals(1, tasks.size());
        return tasks.get(0);
    }
}
