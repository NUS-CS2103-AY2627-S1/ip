package yachiyo.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static yachiyo.TestAssertions.assertYachiyoException;
import static yachiyo.exception.ErrorCategory.INVALID_OPERATION;
import static yachiyo.exception.ErrorCategory.WARNING;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import yachiyo.command.AddCommand;
import yachiyo.command.Command;
import yachiyo.command.DeleteCommand;
import yachiyo.command.EditDeadlineDateTimeCommand;
import yachiyo.command.EditDescriptionCommand;
import yachiyo.command.EditEventEndDateTimeCommand;
import yachiyo.command.EditEventStartDateTimeCommand;
import yachiyo.command.ExitCommand;
import yachiyo.command.FindCommand;
import yachiyo.command.FindOnDateCommand;
import yachiyo.command.ListCommand;
import yachiyo.command.MarkCommand;
import yachiyo.command.UnmarkCommand;
import yachiyo.exception.ErrorCategory;
import yachiyo.exception.YachiyoException;
import yachiyo.storage.Storage;
import yachiyo.task.Deadline;
import yachiyo.task.Event;
import yachiyo.task.Task;
import yachiyo.task.TaskList;
import yachiyo.task.ToDo;
import yachiyo.ui.Ui;

/**
 * Tests conversion of user input into executable commands.
 */
public class ParserTest {
    private final SilentUi ui = new SilentUi();
    private final Storage storage = new NoOpStorage();

    @Test
    public void parse_listCommand_listCommandReturned() throws YachiyoException {
        assertInstanceOf(ListCommand.class, Parser.parse("list"));
    }

    @Test
    public void parse_byeCommand_exitCommandReturned() throws YachiyoException {
        assertInstanceOf(ExitCommand.class, Parser.parse("bye"));
    }

    @Test
    public void parse_listCommandWithArguments_warningThrown() {
        assertNoArgumentCommandRejected("list everything", "list");
    }

    @Test
    public void parse_byeCommandWithArguments_warningThrown() {
        assertNoArgumentCommandRejected("bye for now", "bye");
    }

    @Test
    public void parse_findCommand_findCommandReturned() throws YachiyoException {
        assertInstanceOf(FindCommand.class, Parser.parse("find book"));
    }

    @Test
    public void parse_markCommand_taskNumberPassed() throws YachiyoException {
        Task firstTask = new ToDo("Read book");
        Task secondTask = new ToDo("Return book");
        TaskList tasks = new TaskList(List.of(firstTask, secondTask));
        Command command = Parser.parse("mark 2");

        assertInstanceOf(MarkCommand.class, command);
        command.execute(tasks, ui, storage);
        assertFalse(firstTask.isCompleted());
        assertTrue(secondTask.isCompleted());
    }

    @Test
    public void parse_unmarkCommand_taskNumberPassed() throws YachiyoException {
        Task firstTask = new ToDo("Read book");
        Task secondTask = new ToDo("Return book");
        secondTask.markAsDone();
        TaskList tasks = new TaskList(List.of(firstTask, secondTask));
        Command command = Parser.parse("unmark 2");

        assertInstanceOf(UnmarkCommand.class, command);
        command.execute(tasks, ui, storage);
        assertFalse(secondTask.isCompleted());
    }

    @Test
    public void parse_deleteCommand_taskNumberPassed() throws YachiyoException {
        Task firstTask = new ToDo("Read book");
        TaskList tasks = new TaskList(List.of(firstTask, new ToDo("Return book")));
        Command command = Parser.parse("delete 2");

        assertInstanceOf(DeleteCommand.class, command);
        command.execute(tasks, ui, storage);
        assertEquals(List.of(firstTask), tasks.getTasks());
    }

    @Test
    public void parse_editDescriptionCommand_detailsPassed() throws YachiyoException {
        TaskList tasks = new TaskList(new ToDo("Read book"));
        Command command = Parser.parse("edit 1 /description Return book");

        assertInstanceOf(EditDescriptionCommand.class, command);
        command.execute(tasks, ui, storage);
        assertEquals("Return book", tasks.get(1).getDescription());
    }

    @Test
    public void parse_editDeadlineCommand_detailsPassed() throws YachiyoException {
        Deadline deadline = new Deadline(
                "Submit report", LocalDateTime.of(2026, 9, 20, 17, 0));
        TaskList tasks = new TaskList(deadline);
        Command command = Parser.parse("edit 1 /by 21/9/2026 1800");

        assertInstanceOf(EditDeadlineDateTimeCommand.class, command);
        command.execute(tasks, ui, storage);
        Deadline editedDeadline = (Deadline) tasks.get(1);
        assertEquals(LocalDateTime.of(2026, 9, 21, 18, 0), editedDeadline.getBy());
    }

    @Test
    public void parse_editEventStartCommand_detailsPassed() throws YachiyoException {
        Event event = new Event(
                "Workshop",
                LocalDateTime.of(2026, 9, 20, 9, 0),
                LocalDateTime.of(2026, 9, 20, 17, 0)
        );
        TaskList tasks = new TaskList(event);
        Command command = Parser.parse("edit 1 /from 20/9/2026 1000");

        assertInstanceOf(EditEventStartDateTimeCommand.class, command);
        command.execute(tasks, ui, storage);
        Event editedEvent = (Event) tasks.get(1);
        assertEquals(LocalDateTime.of(2026, 9, 20, 10, 0), editedEvent.getFrom());
    }

    @Test
    public void parse_editEventEndCommand_detailsPassed() throws YachiyoException {
        Event event = new Event(
                "Workshop",
                LocalDateTime.of(2026, 9, 20, 9, 0),
                LocalDateTime.of(2026, 9, 20, 17, 0)
        );
        TaskList tasks = new TaskList(event);
        Command command = Parser.parse("edit 1 /to 20/9/2026 1800");

        assertInstanceOf(EditEventEndDateTimeCommand.class, command);
        command.execute(tasks, ui, storage);
        Event editedEvent = (Event) tasks.get(1);
        assertEquals(LocalDateTime.of(2026, 9, 20, 18, 0), editedEvent.getTo());
    }

    @Test
    public void parse_todoCommand_descriptionPassed() throws YachiyoException {
        Task task = executeAddCommand("  ToDo   Read book  ");

        assertInstanceOf(ToDo.class, task);
        assertEquals("Read book", task.getDescription());
    }

    @Test
    public void parse_deadlineCommand_detailsPassed() throws YachiyoException {
        Task task = executeAddCommand("deadline Submit report /by 20/8/2026 1700");

        assertInstanceOf(Deadline.class, task);
        assertEquals(
                "DEADLINE | 0 | Submit report | 2026-08-20T17:00",
                task.toFileFormat()
        );
    }

    @Test
    public void parse_eventCommand_detailsPassed() throws YachiyoException {
        Task task = executeAddCommand(
                "event Orientation /from 20/8/2026 0900 /to 22/8/2026 1700"
        );

        assertInstanceOf(Event.class, task);
        assertEquals(
                "EVENT | 0 | Orientation | 2026-08-20T09:00 | 2026-08-22T17:00",
                task.toFileFormat()
        );
    }

    @Test
    public void parse_onCommand_datePassed() throws YachiyoException {
        LocalDate expectedDate = LocalDate.of(2026, 8, 20);
        Command command = Parser.parse("on 20/8/2026");

        assertInstanceOf(FindOnDateCommand.class, command);
        command.execute(new TaskList(), ui, storage);
        assertEquals(expectedDate, ui.lastDateShown);
    }

    @Test
    public void parse_unknownCommand_exceptionThrown() {
        assertParsingFailsWithCategory("unknown", WARNING);
    }

    @Test
    public void parse_taskNumberMissing_exceptionThrown() {
        assertParsingFailsWithCategory("mark", WARNING);
    }

    @Test
    public void parse_findKeywordMissing_exceptionThrown() {
        assertParsingFailsWithCategory("find", WARNING);
    }

    @Test
    public void parse_taskNumberNotWholeNumber_exceptionThrown() {
        assertParsingFailsWithCategory("delete two", WARNING);
    }

    @Test
    public void parse_editTaskNumberMissing_exceptionThrown() {
        assertParsingFailsWithCategory("edit", WARNING);
    }

    @Test
    public void parse_editTaskNumberNotWholeNumber_exceptionThrown() {
        assertParsingFailsWithCategory(
                "edit one /description Return book", WARNING);
    }

    @Test
    public void parse_editFieldMissing_exceptionThrown() {
        YachiyoException exception = assertYachiyoException(
                WARNING, () -> Parser.parse("edit 1"));

        assertEquals(
                "Which detail should I edit? Try /description, /by, /from, or /to "
                        + "followed by its new value.",
                exception.getMessage()
        );
    }

    @Test
    public void parse_editFieldUnsupported_exceptionThrown() {
        assertParsingFailsWithCategory("edit 1 /priority high", INVALID_OPERATION);
    }

    @Test
    public void parse_editDescriptionMissing_exceptionThrown() {
        assertParsingFailsWithCategory("edit 1 /description", WARNING);
    }

    @Test
    public void parse_editDateTimeMissing_exceptionThrown() {
        assertParsingFailsWithCategory("edit 1 /by", WARNING);
    }

    @Test
    public void parse_editDateTimeInvalid_exceptionThrown() {
        assertParsingFailsWithCategory(
                "edit 1 /from 31/2/2026 1000", WARNING);
    }

    @Test
    public void parse_todoDescriptionMissing_exceptionThrown() {
        assertParsingFailsWithCategory("todo", WARNING);
    }

    @Test
    public void parse_descriptionsContainPipe_warningThrown() {
        assertDescriptionRejected("todo Compare option A | option B");
        assertDescriptionRejected(
                "deadline Submit | review /by 20/8/2026 1700");
        assertDescriptionRejected(
                "event Project | meeting /from 20/8/2026 0900 /to 20/8/2026 1700");
        assertDescriptionRejected("edit 1 /description Compare option A | option B");
    }

    @Test
    public void parse_todoDescriptionContainsOtherSpecialCharacters_taskReturned()
            throws YachiyoException {
        Task task = executeAddCommand("todo Send Mum's report #2! 🥳");

        assertEquals("Send Mum's report #2! 🥳", task.getDescription());
    }

    @Test
    public void parse_todoDescriptionContainsCommandDelimiter_taskReturned()
            throws YachiyoException {
        Task task = executeAddCommand("todo Compare deadlines /by 12");

        assertEquals("Compare deadlines /by 12", task.getDescription());
    }

    @Test
    public void parse_deadlineDescriptionMissing_exceptionThrown() {
        assertParsingFailsWithCategory("deadline /by 20/8/2026 1700", WARNING);
    }

    @Test
    public void parse_deadlineDateTimeMissing_exceptionThrown() {
        assertParsingFailsWithCategory("deadline Submit report", WARNING);
    }

    @Test
    public void parse_deadlineDateTimeInvalid_exceptionThrown() {
        assertParsingFailsWithCategory(
                "deadline Submit report /by 31/2/2026 1700", WARNING);
    }

    @Test
    public void parse_eventDescriptionMissing_exceptionThrown() {
        assertParsingFailsWithCategory(
                "event /from 20/8/2026 0900 /to 20/8/2026 1700", WARNING);
    }

    @Test
    public void parse_eventStartMissing_exceptionThrown() {
        assertParsingFailsWithCategory("event Orientation", WARNING);
    }

    @Test
    public void parse_eventEndMissing_exceptionThrown() {
        assertParsingFailsWithCategory(
                "event Orientation /from 20/8/2026 0900", WARNING);
    }

    @Test
    public void parse_eventStartInvalid_exceptionThrown() {
        assertParsingFailsWithCategory(
                "event Orientation /from tomorrow /to 20/8/2026 1700", WARNING);
    }

    @Test
    public void parse_eventEndInvalid_exceptionThrown() {
        assertParsingFailsWithCategory(
                "event Orientation /from 20/8/2026 0900 /to later", WARNING);
    }

    @Test
    public void parse_eventEndNotAfterStart_exceptionThrown() {
        assertParsingFailsWithCategory(
                "event Orientation /from 20/8/2026 1700 /to 20/8/2026 1700",
                INVALID_OPERATION);
    }

    @Test
    public void parse_onDateMissing_exceptionThrown() {
        assertParsingFailsWithCategory("on", WARNING);
    }

    @Test
    public void parse_onDateInvalid_exceptionThrown() {
        assertParsingFailsWithCategory("on 31/2/2026", WARNING);
    }

    /**
     * Parses and executes an add command, returning the task it created.
     *
     * @param input add command to parse.
     * @return task added by the parsed command.
     * @throws YachiyoException if parsing or execution fails.
     */
    private Task executeAddCommand(String input) throws YachiyoException {
        Command command = Parser.parse(input);
        assertInstanceOf(AddCommand.class, command);
        TaskList tasks = new TaskList();

        command.execute(tasks, ui, storage);

        return tasks.get(1);
    }

    /**
     * Verifies that a command containing a pipe in its description is rejected as a warning.
     *
     * @param input command containing an unsafe description.
     */
    private void assertDescriptionRejected(String input) {
        YachiyoException exception = assertYachiyoException(
                WARNING, () -> Parser.parse(input));

        assertEquals(
                "Descriptions can't contain the \"|\" character. Could you remove it?",
                exception.getMessage()
        );
    }

    /**
     * Verifies that a no-argument command rejects trailing details as a warning.
     *
     * @param input command containing unexpected arguments.
     * @param commandWord command word expected in the correction message.
     */
    private void assertNoArgumentCommandRejected(String input, String commandWord) {
        YachiyoException exception = assertYachiyoException(
                WARNING, () -> Parser.parse(input));

        assertEquals(
                String.format("Hmm... the %s command doesn't need anything after it. "
                        + "Did you mean \"%s\"?", commandWord, commandWord),
                exception.getMessage()
        );
    }

    /**
     * Verifies that parsing an input fails with the expected error category.
     *
     * @param input command expected to be rejected.
     * @param expectedCategory expected presentation category.
     */
    private void assertParsingFailsWithCategory(String input,
            ErrorCategory expectedCategory) {
        assertYachiyoException(expectedCategory, () -> Parser.parse(input));
    }

    /**
     * UI test double that suppresses output and records searched dates.
     */
    private static final class SilentUi extends Ui {
        private LocalDate lastDateShown;

        @Override
        public void showTaskAdded(Task task, int totalCount) {
            // No output is needed while testing parsing.
        }

        @Override
        public void showTaskDeleted(Task task, int totalCount) {
            // No output is needed while testing parsing.
        }

        @Override
        public void showTaskEdited(Task task) {
            // No output is needed while testing parsing.
        }

        @Override
        public void showTaskMarked(Task task, int remainingCount) {
            // No output is needed while testing parsing.
        }

        @Override
        public void showTaskUnmarked(Task task, int remainingCount) {
            // No output is needed while testing parsing.
        }

        @Override
        public void showNoTasksOnDate(LocalDate date) {
            lastDateShown = date;
        }
    }

    /**
     * Storage test double that avoids writing files during parser tests.
     */
    private static final class NoOpStorage extends Storage {
        private NoOpStorage() {
            super(Path.of("unused"));
        }

        @Override
        public void saveTasks(List<Task> tasks) {
            // Persistence behavior is covered by StorageTest.
        }
    }
}
