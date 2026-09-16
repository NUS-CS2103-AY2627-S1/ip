package downtowngurl.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import downtowngurl.command.Command;
import downtowngurl.command.FindCommand;
import downtowngurl.exception.DowntownGurlException;
import downtowngurl.storage.Storage;
import downtowngurl.task.Deadline;
import downtowngurl.task.TaskList;
import downtowngurl.task.Todo;
import downtowngurl.ui.Ui;

/**
 * Tests conversion of raw user input into executable commands.
 */
public class ParserTest {
    private final Storage storage = new NoOpStorage();
    private final Ui ui = new Ui();

    /**
     * Checks that the bye command is parsed as an exit command.
     *
     * @throws DowntownGurlException If parsing unexpectedly fails.
     */
    @Test
    public void parse_byeCommand_returnsExitCommand() throws DowntownGurlException {
        Command command = Parser.parse("bye");

        assertTrue(command.isExit());
    }

    /**
     * Checks that a todo command creates and adds a todo task.
     *
     * @throws DowntownGurlException If parsing or command execution unexpectedly fails.
     */
    @Test
    public void parse_todoCommand_addsTodoTask() throws DowntownGurlException {
        TaskList tasks = new TaskList();

        Parser.parse("todo read book").execute(tasks, this.storage, this.ui);

        assertEquals(1, tasks.size());
        assertEquals("[T][ ] read book", tasks.get(0).toString());
    }

    /**
     * Checks that a deadline command creates and adds a deadline task.
     *
     * @throws DowntownGurlException If parsing or command execution unexpectedly fails.
     */
    @Test
    public void parse_deadlineCommand_addsDeadlineTask() throws DowntownGurlException {
        TaskList tasks = new TaskList();

        Parser.parse("deadline return book /by 2/12/2019 1800").execute(tasks, this.storage, this.ui);

        assertEquals(1, tasks.size());
        assertEquals("[D][ ] return book (by: 02 Dec 2019, Monday 18:00)", tasks.get(0).toString());
    }

    /**
     * Checks that a deadline command can create a weekly recurring deadline task.
     *
     * @throws DowntownGurlException If parsing or command execution unexpectedly fails.
     */
    @Test
    public void parse_recurringDeadlineCommand_addsRecurringDeadlineTask() throws DowntownGurlException {
        TaskList tasks = new TaskList();

        Parser.parse("deadline return book /by 2/12/2019 1800 /repeat weekly")
                .execute(tasks, this.storage, this.ui);

        assertEquals(1, tasks.size());
        assertEquals("[D][ ] return book (by: 02 Dec 2019, Monday 18:00) (repeats weekly)",
                tasks.get(0).toString());
    }

    /**
     * Checks that an event command creates and adds an event task.
     *
     * @throws DowntownGurlException If parsing or command execution unexpectedly fails.
     */
    @Test
    public void parse_eventCommand_addsEventTask() throws DowntownGurlException {
        TaskList tasks = new TaskList();

        Parser.parse("event project meeting /from 2/12/2019 1800 /to 2/12/2019 2000")
                .execute(tasks, this.storage, this.ui);

        assertEquals(1, tasks.size());
        assertEquals("[E][ ] project meeting (from: 02 Dec 2019, Monday 18:00 to: 02 Dec 2019, Monday 20:00)",
                tasks.get(0).toString());
    }

    /**
     * Checks that an event command can create a weekly recurring event task.
     *
     * @throws DowntownGurlException If parsing or command execution unexpectedly fails.
     */
    @Test
    public void parse_recurringEventCommand_addsRecurringEventTask() throws DowntownGurlException {
        TaskList tasks = new TaskList();

        Parser.parse("event project meeting /from 2/12/2019 1800 /to 2/12/2019 2000 /repeat weekly")
                .execute(tasks, this.storage, this.ui);

        assertEquals(1, tasks.size());
        assertEquals("[E][ ] project meeting (from: 02 Dec 2019, Monday 18:00 to: 02 Dec 2019, Monday 20:00)"
                + " (repeats weekly)", tasks.get(0).toString());
    }

    /**
     * Checks that a mark command marks the selected task as done.
     *
     * @throws DowntownGurlException If parsing or command execution unexpectedly fails.
     */
    @Test
    public void parse_markCommand_marksSelectedTaskAsDone() throws DowntownGurlException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        Parser.parse("mark 1").execute(tasks, this.storage, this.ui);

        assertTrue(tasks.get(0).isDone());
    }

    /**
     * Checks that an unmark command marks the selected task as not done.
     *
     * @throws DowntownGurlException If parsing or command execution unexpectedly fails.
     */
    @Test
    public void parse_unmarkCommand_marksSelectedTaskAsNotDone() throws DowntownGurlException {
        TaskList tasks = new TaskList();
        Todo task = new Todo("read book");
        task.markAsDone();
        tasks.add(task);

        Parser.parse("unmark 1").execute(tasks, this.storage, this.ui);

        assertFalse(tasks.get(0).isDone());
    }

    /**
     * Checks that a delete command removes the selected task.
     *
     * @throws DowntownGurlException If parsing or command execution unexpectedly fails.
     */
    @Test
    public void parse_deleteCommand_removesSelectedTask() throws DowntownGurlException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Todo("write report"));

        Parser.parse("delete 1").execute(tasks, this.storage, this.ui);

        assertEquals(1, tasks.size());
        assertEquals("[T][ ] write report", tasks.get(0).toString());
    }

    /**
     * Checks that a repeat command makes the selected dated task recur weekly.
     *
     * @throws DowntownGurlException If parsing or command execution unexpectedly fails.
     */
    @Test
    public void parse_repeatCommand_makesSelectedDatedTaskRecurring() throws DowntownGurlException {
        TaskList tasks = new TaskList();
        tasks.add(new Deadline("submit report", LocalDateTime.of(2019, 12, 2, 18, 0)));

        Parser.parse("repeat 1 /weekly").execute(tasks, this.storage, this.ui);

        assertTrue(tasks.get(0).isRecurring());
        assertEquals("[D][ ] submit report (by: 02 Dec 2019, Monday 18:00) (repeats weekly)",
                tasks.get(0).toString());
    }

    /**
     * Checks that an unrepeat command removes recurrence from the selected task.
     *
     * @throws DowntownGurlException If parsing or command execution unexpectedly fails.
     */
    @Test
    public void parse_unrepeatCommand_removesRecurrenceFromSelectedTask() throws DowntownGurlException {
        TaskList tasks = new TaskList();
        Parser.parse("deadline submit report /by 2/12/2019 1800 /repeat weekly")
                .execute(tasks, this.storage, this.ui);

        Parser.parse("unrepeat 1").execute(tasks, this.storage, this.ui);

        assertFalse(tasks.get(0).isRecurring());
        assertEquals("[D][ ] submit report (by: 02 Dec 2019, Monday 18:00)", tasks.get(0).toString());
    }

    /**
     * Checks that mixed-case command words and separators are accepted.
     *
     * @throws DowntownGurlException If parsing or command execution unexpectedly fails.
     */
    @Test
    public void parse_mixedCaseCommandSyntax_executesCommands() throws DowntownGurlException {
        TaskList tasks = new TaskList();

        Parser.parse("toDo Read Book").execute(tasks, this.storage, this.ui);
        Parser.parse("DeAdLiNe Submit Report /BY 2/12/2019 1800 /RePeAt WeEkLy")
                .execute(tasks, this.storage, this.ui);
        Parser.parse("MaRk 1").execute(tasks, this.storage, this.ui);
        Parser.parse("uNrEpEaT 2").execute(tasks, this.storage, this.ui);
        Command listCommand = Parser.parse("LiSt");
        Command findCommand = Parser.parse("FiNd Book");
        Command byeCommand = Parser.parse("ByE");
        Parser.parse("dElEtE 1").execute(tasks, this.storage, this.ui);

        assertEquals(1, tasks.size());
        assertEquals("[D][ ] Submit Report (by: 02 Dec 2019, Monday 18:00)", tasks.get(0).toString());
        assertFalse(listCommand.isExit());
        assertInstanceOf(FindCommand.class, findCommand);
        assertTrue(byeCommand.isExit());
    }

    /**
     * Checks that a find command returns a find command object.
     */
    @Test
    public void parse_findCommand_returnsFindCommand() throws DowntownGurlException {
        Command command = Parser.parse("find book");

        assertInstanceOf(FindCommand.class, command);
    }

    @Test
    public void parse_unknownCommand_throwsDowntownGurlException() {
        assertThrows(DowntownGurlException.class, () -> Parser.parse("dance"));
    }

    /**
     * Checks that accidental blank input is rejected with a user-facing exception.
     */
    @Test
    public void parse_blankCommand_throwsDowntownGurlException() {
        assertThrows(DowntownGurlException.class, () -> Parser.parse(""));
        assertThrows(DowntownGurlException.class, () -> Parser.parse("   "));
    }

    /**
     * Checks that null input is rejected with a user-facing exception instead of a runtime crash.
     */
    @Test
    public void parse_nullCommand_throwsDowntownGurlException() {
        assertThrows(DowntownGurlException.class, () -> Parser.parse(null));
    }

    /**
     * Checks that extra spaces around a command do not stop command recognition.
     *
     * @throws DowntownGurlException If parsing unexpectedly fails.
     */
    @Test
    public void parse_commandWithOuterSpaces_returnsExpectedCommand() throws DowntownGurlException {
        Command command = Parser.parse("  find book  ");

        assertInstanceOf(FindCommand.class, command);
    }

    /**
     * Checks that a todo command without a description is rejected.
     */
    @Test
    public void parse_emptyTodoDescription_throwsDowntownGurlException() {
        DowntownGurlException exception = assertThrows(DowntownGurlException.class,
                () -> Parser.parse("todo   "));

        assertEquals(createFormatHint("todo <task name>"), exception.getMessage());
    }

    /**
     * Checks that a deadline command without a due date is rejected.
     */
    @Test
    public void parse_emptyFindKeyword_throwsDowntownGurlException() {
        DowntownGurlException exception = assertThrows(DowntownGurlException.class,
                () -> Parser.parse("find   "));

        assertEquals(createFormatHint("find <keyword>"), exception.getMessage());
    }

    @Test
    public void parse_missingDeadlineDate_throwsDowntownGurlException() {
        DowntownGurlException exception = assertThrows(DowntownGurlException.class,
                () -> Parser.parse("deadline return book"));

        assertEquals(createFormatHint("deadline <task name> /by dd/mm/yyyy time"), exception.getMessage());
    }

    /**
     * Checks that an event command without an end date is rejected.
     */
    @Test
    public void parse_missingEventEndDate_throwsDowntownGurlException() {
        DowntownGurlException exception = assertThrows(DowntownGurlException.class,
                () -> Parser.parse("event project meeting /from 2/12/2019 1800"));

        assertEquals(createFormatHint("event <event name> /from dd/mm/yyyy time /to dd/mm/yyyy time"),
                exception.getMessage());
    }

    /**
     * Checks that unsupported recurrence frequencies are rejected.
     */
    @Test
    public void parse_unsupportedRepeatFrequency_throwsDowntownGurlException() {
        DowntownGurlException exception = assertThrows(DowntownGurlException.class, () ->
                Parser.parse("deadline submit report /by 2/12/2019 1800 /repeat daily"));

        assertEquals(createFormatHint("deadline <task name> /by dd/mm/yyyy time /repeat weekly"),
                exception.getMessage());
    }

    /**
     * Checks that malformed repeat commands are rejected.
     */
    @Test
    public void parse_malformedRepeatCommand_throwsDowntownGurlException() {
        DowntownGurlException missingSeparatorException = assertThrows(DowntownGurlException.class,
                () -> Parser.parse("repeat 1 weekly"));
        DowntownGurlException missingTaskNumberException = assertThrows(DowntownGurlException.class,
                () -> Parser.parse("repeat /weekly"));

        assertEquals(createFormatHint("repeat <task number> /weekly"), missingSeparatorException.getMessage());
        assertEquals(createFormatHint("repeat <task number> /weekly"), missingTaskNumberException.getMessage());
    }

    /**
     * Checks that non-positive and non-numeric task numbers are rejected.
     */
    @Test
    public void parse_invalidTaskNumber_throwsDowntownGurlException() {
        DowntownGurlException nonPositiveException = assertThrows(DowntownGurlException.class,
                () -> Parser.parse("mark 0"));
        DowntownGurlException nonNumericException = assertThrows(DowntownGurlException.class,
                () -> Parser.parse("mark one"));

        assertEquals(createFormatHint("mark <task number>"), nonPositiveException.getMessage());
        assertEquals(createFormatHint("mark <task number>"), nonNumericException.getMessage());
    }

    /**
     * Checks that commands that select tasks explain their expected task-number argument.
     */
    @Test
    public void parse_missingTaskNumber_throwsDowntownGurlException() {
        assertFormatHint("mark", "mark <task number>");
        assertFormatHint("unmark", "unmark <task number>");
        assertFormatHint("delete", "delete <task number>");
        assertFormatHint("unrepeat", "unrepeat <task number>");
    }

    /**
     * Checks that a validly formatted but out-of-range task number gives a list-number hint.
     */
    @Test
    public void execute_taskNumberOutOfRange_throwsDowntownGurlException() throws DowntownGurlException {
        TaskList tasks = new TaskList();

        DowntownGurlException exception = assertThrows(DowntownGurlException.class,
                () -> Parser.parse("mark 1").execute(tasks, this.storage, this.ui));

        assertEquals("Maybe you could try using a task number from your list."
                + "\nGirl you need some sleep... I don't understand whatchu talking about.",
                exception.getMessage());
    }

    /**
     * Checks a command input against its expected format-hint message.
     *
     * @param command Command input to parse.
     * @param commandFormat Expected command format.
     */
    private static void assertFormatHint(String command, String commandFormat) {
        DowntownGurlException exception = assertThrows(DowntownGurlException.class, () -> Parser.parse(command));

        assertEquals(createFormatHint(commandFormat), exception.getMessage());
    }

    /**
     * Creates the standard two-line format hint expected by parser errors.
     *
     * @param commandFormat Expected command format.
     * @return Format hint message.
     */
    private static String createFormatHint(String commandFormat) {
        return "Maybe you could try formatting it as: " + commandFormat
                + "\nSoz queen you gotta at least give me SOMETHING to work with.";
    }

    /**
     * Storage double that lets command execution mutate the task list without touching disk.
     */
    private static class NoOpStorage extends Storage {
        /**
         * Creates a storage double with an unused path.
         */
        NoOpStorage() {
            super(Path.of("unused.txt"));
        }

        /**
         * Pretends to save tasks without writing to disk.
         *
         * @param tasks Current task list, unused by this test double.
         */
        @Override
        public void saveTasks(Iterable<downtowngurl.task.Task> tasks) {
            // Tests here focus on parser output and command effects, not persistence.
        }
    }
}
