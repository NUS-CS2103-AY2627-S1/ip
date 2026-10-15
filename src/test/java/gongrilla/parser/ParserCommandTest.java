package gongrilla.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import gongrilla.command.Command;
import gongrilla.command.DeleteCommand;
import gongrilla.command.ExitCommand;
import gongrilla.command.FindCommand;
import gongrilla.command.HelpCommand;
import gongrilla.exception.GongrillaException;
import gongrilla.storage.Storage;
import gongrilla.task.Deadline;
import gongrilla.task.Event;
import gongrilla.task.TaskList;
import gongrilla.task.Todo;
import gongrilla.ui.Ui;

/**
 * Tests command parsing and execution boundaries.
 */
class ParserCommandTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void parse_supportedTaskTypes_addsAndPersistsTasksWithCorrectLabels() throws Exception {
        String[] commands = {
            "todo read book",
            "deadline submit report /by 6/9/2026 1700",
            "event meeting /from 6/9/2026 0900 /to 6/9/2026 1000"
        };
        String[] typeNames = {"todo", "deadline", "event"};
        TaskList tasks = new TaskList();
        Storage storage = createStorage();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Ui ui = new Ui(new ByteArrayInputStream(new byte[0]), new PrintStream(output));

        for (int i = 0; i < commands.length; i++) {
            output.reset();
            Parser.parse(commands[i]).execute(tasks, ui, storage);

            assertEquals(i + 1, tasks.size());
            assertTrue(output.toString().startsWith("higa higa click click. New " + typeNames[i] + ":"));
            assertEquals(tasks.get(i).toDataString(), storage.load().get(i).toDataString());
        }
    }

    @Test
    void parse_mixedCaseIndexCommands_acceptsMatchingDispatchKeywords() throws Exception {
        TaskList tasks = new TaskList(new Todo("first"));
        Storage storage = createStorage();

        Parser.parse("MaRk 1").execute(tasks, createUi(), storage);
        assertTrue(tasks.get(0).isDone());
        Parser.parse("UnMaRk 1").execute(tasks, createUi(), storage);
        assertEquals(false, tasks.get(0).isDone());
        Parser.parse("DeLeTe 1").execute(tasks, createUi(), storage);
        assertEquals(0, tasks.size());
    }

    @Test
    void parse_mixedCaseTaskCommands_preservesDescriptionsAndDates() throws Exception {
        TaskList tasks = new TaskList();
        Storage storage = createStorage();
        String[] commands = {
            "  ToDo   Read Book  ",
            "  DeAdLiNe Submit Report /BY 2026-09-06 1700  ",
            "  EvEnT Team Meeting /FROM 6/9/2026 /TO 7/9/2026 1000  "
        };
        for (String command : commands) {
            Parser.parse(command).execute(tasks, createUi(), storage);
        }

        assertEquals("Read Book", tasks.get(0).getName());
        Deadline deadline = assertInstanceOf(Deadline.class, tasks.get(1));
        assertEquals("Submit Report", deadline.getName());
        assertEquals(LocalDateTime.of(2026, 9, 6, 17, 0), deadline.getBy());
        Event event = assertInstanceOf(Event.class, tasks.get(2));
        assertEquals("Team Meeting", event.getName());
        assertEquals(LocalDateTime.of(2026, 9, 6, 0, 0), event.getFrom());
        assertEquals(LocalDateTime.of(2026, 9, 7, 10, 0), event.getTo());
    }

    @Test
    void parse_unicodeWhitespaceInRequiredFields_reportsMissingDetails() {
        assertParsingError("deadline \u2003 /by 1/1/2026", "No task. What Gongrilla supposed to do?");
        assertParsingError("deadline task /by \u2003", "When task due? Gongrilla need date or date-time.");
        assertParsingError("event \u2003 /from 1/1/2026 /to 2/1/2026", "Task missing. No task, no banana.");
        assertParsingError("event task /from \u2003 /to 2/1/2026",
                "When event start? When event end? Gongrilla need know.");
        assertParsingError("event task /from 1/1/2026 /to \u2003",
                "When event start? When event end? Gongrilla need know.");
    }

    @Test
    void parse_missingTaskDetails_preservesSpecificErrors() {
        assertParsingError("todo   ", "Empty task. What Gongrilla do? Give something.");
        assertParsingError("deadline report", "Missing /by. Type help for the format.");
        assertParsingError("deadline report /by", "When task due? Gongrilla need date or date-time.");
        assertParsingError("event meeting /from 6/9/2026",
                "Missing /to. Type help for the format.");
        assertParsingError("event meeting /from /to 7/9/2026",
                "When event start? When event end? Gongrilla need know.");
    }

    @Test
    void parse_invalidTaskDates_preservesDateAndRangeValidation() {
        assertThrows(DateTimeParseException.class, () -> Parser.parse("deadline report /by 31/2/2026"));
        assertThrows(DateTimeParseException.class, () ->
                Parser.parse("event meeting /from invalid /to 7/9/2026"));
        assertThrows(DateTimeParseException.class, () ->
                Parser.parse("event meeting /from 6/9/2026 /to invalid"));
        assertThrows(IllegalArgumentException.class, () ->
                Parser.parse("event meeting /from 7/9/2026 /to 6/9/2026"));
    }

    @Test
    void parse_unknownCommandBoundaries_preservesRejection() {
        String[] commands = {null, "", "   ", "todoish read", "list extra", "bye extra"};
        for (String command : commands) {
            assertParsingError(command, "Hmm. Gongrilla no know that :-(");
        }
    }

    @Test
    void parse_helpWithWhitespace_returnsNonExitingHelpCommand() throws Exception {
        Command command = Parser.parse("  HeLp  ");

        assertInstanceOf(HelpCommand.class, command);
        assertFalse(command.isExit());
    }

    @Test
    void parse_byeWithWhitespace_returnsExitCommand() throws Exception {
        Command command = Parser.parse("  BYE  ");

        assertInstanceOf(ExitCommand.class, command);
        assertTrue(command.isExit());
    }

    @Test
    void parse_deleteTwo_deletesSecondTask() throws Exception {
        TaskList tasks = new TaskList(
                new Todo("first"), new Todo("second"), new Todo("third"));
        Command command = Parser.parse("delete 2");

        command.execute(tasks, createUi(), createStorage());

        assertInstanceOf(DeleteCommand.class, command);
        assertEquals(List.of("first", "third"),
                tasks.asList().stream().map(task -> task.getName()).toList());
    }

    @Test
    void parse_findWithMixedCaseAndWhitespace_displaysMatchingTasks() throws Exception {
        TaskList tasks = new TaskList(
                new Todo("read book"), new Todo("write essay"), new Todo("return BOOK"));
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Ui ui = new Ui(new ByteArrayInputStream(new byte[0]), new PrintStream(output));

        Command command = Parser.parse("  FIND   book  ");
        command.execute(tasks, ui, createStorage());

        assertInstanceOf(FindCommand.class, command);
        assertEquals("Here are the matching tasks in your list:" + System.lineSeparator()
                        + "  1.[T][ ] read book" + System.lineSeparator()
                        + "  2.[T][ ] return BOOK" + System.lineSeparator(),
                output.toString());
    }

    @Test
    void parse_findWithoutKeyword_rejectsCommand() {
        GongrillaException exception = assertThrows(GongrillaException.class, () ->
                Parser.parse("find   "));

        assertEquals("What find? Gongrilla need keyword.", exception.getMessage());
    }

    @Test
    void parse_taskNumberContainingOtherText_rejectsCommand() {
        GongrillaException exception = assertThrows(GongrillaException.class, () ->
                Parser.parse("delete abc2"));

        assertEquals("Dis not number. Even banana know number.", exception.getMessage());
    }

    @Test
    void parse_zeroTaskNumber_rejectsCommand() {
        GongrillaException exception = assertThrows(GongrillaException.class, () ->
                Parser.parse("mark 0"));

        assertEquals("Task 0? Human counting start at 1. ", exception.getMessage());
    }

    @Test
    void parse_taskNumberWithPlusSign_rejectsCommand() {
        GongrillaException exception = assertThrows(GongrillaException.class, () ->
                Parser.parse("mark +2"));

        assertEquals("Don't put +. Number already positive. Human make simple thing hard.",
                exception.getMessage());
    }

    @Test
    void parse_eventWithToBeforeFrom_rejectsCommand() {
        GongrillaException exception = assertThrows(GongrillaException.class, () ->
                Parser.parse("event meeting /to 3/12/2019 1700 /from 3/12/2019 0900"));

        assertEquals("Use /from then /to in that order.",
                exception.getMessage());
    }

    @Test
    void delete_indexBeyondTaskList_reportsExistingError() throws Exception {
        Command command = Parser.parse("delete 2");
        TaskList tasks = new TaskList(new Todo("only task"));

        GongrillaException exception = assertThrows(GongrillaException.class, () ->
                command.execute(tasks, createUi(), createStorage()));

        assertEquals("No task there. Human seeing things?",
                exception.getMessage());
    }

    @Test
    void parse_missingTaskNumber_reportsSpecificError() {
        assertParsingError("delete", "No number. Gongrilla pick air?");
    }

    @Test
    void parse_negativeTaskNumber_reportsSpecificError() {
        assertParsingError("mark -1", "Negative task? What next, negative banana?");
    }

    @Test
    void parse_overflowingTaskNumber_reportsSpecificError() {
        assertParsingError("unmark 999999999999999999999999",
                "Number too big. Gongrilla run out of fingers.");
    }

    @Test
    void parse_keywordPrefixes_rejectsUnknownCommands() {
        String[] keywords = {"find", "deadline", "todo", "event", "delete", "mark", "unmark"};
        for (String keyword : keywords) {
            assertParsingError(keyword + "ish value", "Hmm. Gongrilla no know that :-(");

        }
    }

    @Test
    void parse_bareMixedCaseKeywords_preservesCommandSpecificErrors() {
        assertParsingError("FiNd", "What find? Gongrilla need keyword.");
        assertParsingError("ToDo", "Empty task. What Gongrilla do? Give something.");
        assertParsingError("DeAdLiNe", "Missing /by. Type help for the format.");
        assertParsingError("EvEnT",
                "Missing /from. Type help for the format.");
        for (String keyword : new String[]{"DeLeTe", "MaRk", "UnMaRk"}) {
            assertParsingError(keyword, "No number. Gongrilla pick air?");
        }
    }

    private void assertParsingError(String command, String expectedMessage) {
        GongrillaException exception = assertThrows(GongrillaException.class, () ->
                Parser.parse(command));
        assertEquals(expectedMessage, exception.getMessage());
    }

    private Ui createUi() {
        return new Ui(new ByteArrayInputStream(new byte[0]),
                new PrintStream(new ByteArrayOutputStream()));
    }

    private Storage createStorage() {
        return new Storage(temporaryDirectory.resolve("data/tasks.txt"));
    }
}
