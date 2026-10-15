package rex.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import rex.RexException;
import rex.TaskList;
import rex.Ui;
import rex.task.Deadline;
import rex.task.TaskDateTime;
import rex.task.ToDo;

/**
 * Tests carrying out each kind of command: what it does to the task list, what
 * it tells the user, and whether it ends the session or asks for a save.
 *
 * What the user would have seen is read back through the Ui's own capture,
 * the one the GUI already uses to collect a whole reply before showing it. A
 * command therefore runs against the real Ui here rather than a stand-in, so
 * these tests cover the wording of the replies as well as the commands.
 *
 * Test methods are deliberately not public, for the reason given in
 * TaskDateTimeTest.
 */
public class CommandTest {
    private TaskList tasks;
    private Ui ui;

    /** Joins lines the way the Ui does, so an expected reply reads as a list of lines. */
    private static String lines(String... lines) {
        return String.join(System.lineSeparator(), lines);
    }

    @BeforeEach
    void setUp() {
        tasks = new TaskList();
        ui = new Ui();
    }

    /** Runs a command and returns what it showed the user. */
    private String replyTo(Command command) throws RexException {
        ui.startCapture();
        command.execute(tasks, ui);
        return ui.takeCapture();
    }

    @Test
    void execute_addCommand_taskAddedAndConfirmed() throws RexException {
        String reply = replyTo(new AddCommand(new ToDo("read book")));

        assertEquals(1, tasks.size());
        assertEquals(lines("Got it! I've fetched this task for you:",
                "  [T][ ] read book",
                "You now have 1 tasks in your bowl!"), reply);
    }

    @Test
    void execute_markCommand_taskDoneAndShownBack() throws RexException {
        tasks.add(new ToDo("read book"));

        String reply = replyTo(new MarkCommand(1));

        assertEquals("X", tasks.get(0).getStatusIcon());
        assertEquals(lines("Nice catch! I've marked this task as done:",
                "  [T][X] read book"), reply);
    }

    @Test
    void execute_unmarkCommand_taskNotDoneAndShownBack() throws RexException {
        tasks.add(new ToDo("read book"));
        tasks.get(0).markAsDone();

        String reply = replyTo(new UnmarkCommand(1));

        assertEquals(" ", tasks.get(0).getStatusIcon());
        assertTrue(reply.contains("[T][ ] read book"), reply);
    }

    @Test
    void execute_markCommandOnANumberNoTaskHas_exceptionThrownAndNothingChanged() {
        tasks.add(new ToDo("read book"));

        assertThrows(RexException.class, () -> new MarkCommand(2).execute(tasks, ui));

        assertEquals(" ", tasks.get(0).getStatusIcon());
    }

    @Test
    void execute_deleteCommand_taskRemovedAndRemainderCounted() throws RexException {
        tasks.add(new ToDo("read book"));
        tasks.add(new ToDo("write essay"));

        String reply = replyTo(new DeleteCommand(1));

        assertEquals(1, tasks.size());
        assertEquals("write essay", tasks.get(0).getDescription());
        assertEquals(lines("Gotcha! I've removed this task from your bowl:",
                "  [T][ ] read book",
                "You now have 1 tasks in your bowl!"), reply);
    }

    @Test
    void execute_deleteCommandOnANumberNoTaskHas_exceptionThrownAndNothingRemoved() {
        tasks.add(new ToDo("read book"));

        assertThrows(RexException.class, () -> new DeleteCommand(5).execute(tasks, ui));

        assertEquals(1, tasks.size());
    }

    @Test
    void execute_listCommand_everyTaskNumberedFromOne() throws RexException {
        tasks.add(new ToDo("read book"));
        tasks.add(new Deadline("return book", TaskDateTime.parse("2019-10-15")));

        String reply = replyTo(new ListCommand());

        assertEquals(lines("Here's what's in your bowl:",
                "1.[T][ ] read book",
                "2.[D][ ] return book (by: Oct 15 2019)"), reply);
    }

    @Test
    void execute_listCommandOnAnEmptyList_headingAlone() throws RexException {
        String reply = replyTo(new ListCommand());

        assertEquals("Here's what's in your bowl:", reply);
    }

    @Test
    void execute_onCommand_matchingTasksKeepTheirNumbersFromTheFullList() throws RexException {
        // The second task is the only one on the day, and has to come back
        // numbered 2 rather than renumbered to 1, or marking it by the number
        // just shown would mark the wrong task.
        tasks.add(new ToDo("read book"));
        tasks.add(new Deadline("return book", TaskDateTime.parse("2019-10-15")));

        String reply = replyTo(new OnCommand(LocalDate.of(2019, 10, 15)));

        assertEquals(lines("Here's what's on Oct 15 2019:",
                "2.[D][ ] return book (by: Oct 15 2019)"), reply);
    }

    @Test
    void execute_onCommandWithNothingThatDay_saysSo() throws RexException {
        String reply = replyTo(new OnCommand(LocalDate.of(2019, 10, 15)));

        assertEquals("Nothing on Oct 15 2019 — your bowl's empty that day!", reply);
    }

    @Test
    void execute_findCommand_matchesKeepTheirNumbersFromTheFullList() throws RexException {
        tasks.add(new ToDo("write essay"));
        tasks.add(new ToDo("read book"));

        String reply = replyTo(new FindCommand("book"));

        assertEquals(lines("Here's what matches \"book\":",
                "2.[T][ ] read book"), reply);
    }

    @Test
    void execute_findCommandMatchingNothing_saysSo() throws RexException {
        tasks.add(new ToDo("read book"));

        String reply = replyTo(new FindCommand("bicycle"));

        assertEquals("No sign of \"bicycle\" in your bowl!", reply);
    }

    @Test
    void execute_exitCommand_saysGoodbye() throws RexException {
        String reply = replyTo(new ExitCommand());

        assertEquals("Bye! *wags tail* Hope to fetch for you again soon!", reply);
    }

    @Test
    void execute_unknownCommand_alwaysRefuses() {
        RexException thrown = assertThrows(
                RexException.class, () -> new UnknownCommand().execute(tasks, ui));

        assertTrue(thrown.getMessage().contains("don't know what that means"), thrown.getMessage());
    }

    @Test
    void isExit_everyCommand_onlyTheExitCommandSaysYes() {
        assertTrue(new ExitCommand().isExit());
        assertFalse(new ListCommand().isExit());
        assertFalse(new AddCommand(new ToDo("read book")).isExit());
        assertFalse(new UnknownCommand().isExit());
    }

    @Test
    void isTaskListChanged_everyCommand_onlyTheOnesThatChangeItSayYes() {
        // What this answers decides whether the tasks are written to disk, so a
        // command answering wrongly either loses the user's work or writes the
        // file after a command that only looked at the list.
        assertTrue(new AddCommand(new ToDo("read book")).isTaskListChanged());
        assertTrue(new MarkCommand(1).isTaskListChanged());
        assertTrue(new UnmarkCommand(1).isTaskListChanged());
        assertTrue(new DeleteCommand(1).isTaskListChanged());

        assertFalse(new ListCommand().isTaskListChanged());
        assertFalse(new FindCommand("book").isTaskListChanged());
        assertFalse(new OnCommand(LocalDate.of(2019, 10, 15)).isTaskListChanged());
        assertFalse(new ExitCommand().isTaskListChanged());
        assertFalse(new UnknownCommand().isTaskListChanged());
    }
}
