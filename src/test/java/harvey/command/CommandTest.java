package harvey.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import harvey.HarveyException;
import harvey.storage.Storage;
import harvey.task.Task;
import harvey.task.TaskList;
import harvey.ui.Ui;

/**
 * Tests what the commands say back.
 * <p>
 * These tests became worth writing when {@code execute} started returning its reply
 * instead of printing it. Previously the only way to check a reply was to capture
 * {@code System.out}; now the reply is simply the return value, so what the user is told
 * can be asserted directly.
 * <p>
 * Every command saves, so each test writes into its own {@code @TempDir} rather than
 * touching the real {@code ./data/harvey.txt}.
 */
public class CommandTest {
    /** The line separator the commands use when building multi-line replies. */
    private static final String NEW_LINE = System.lineSeparator();

    /** An empty folder created fresh for each test and removed afterwards. */
    @TempDir
    private Path tempDir;

    private final Ui ui = new Ui();

    /** Returns a Storage writing into this test's temporary folder. */
    private Storage storage() {
        return new Storage(tempDir.toString(), "harvey.txt");
    }

    /** Returns a list holding one todo, added through the command being tested. */
    private TaskList listWithOneTodo() throws HarveyException {
        TaskList tasks = new TaskList();
        new AddCommand(CommandType.TODO, "read book").execute(tasks, ui, storage());
        return tasks;
    }

    @Test
    public void execute_addTodo_replyNamesTaskAndNewCount() throws HarveyException {
        TaskList tasks = new TaskList();
        String reply = new AddCommand(CommandType.TODO, "read book").execute(tasks, ui, storage());

        assertEquals("Consider it filed:" + NEW_LINE
                + "  [T][ ] read book" + NEW_LINE
                + "You've got 1 case on the docket.", reply);
        assertEquals(1, tasks.size());
    }

    @Test
    public void execute_addTodoWithReservedCharacter_exceptionThrown() {
        TaskList tasks = new TaskList();
        AddCommand command = new AddCommand(CommandType.TODO, "read | book");

        HarveyException e = assertThrows(HarveyException.class, () ->
                command.execute(tasks, ui, storage()));
        assertTrue(e.getMessage().contains("separate fields"));
        assertTrue(tasks.isEmpty());
    }

    @Test
    public void execute_markFirstTask_replyShowsTaskTicked() throws HarveyException {
        TaskList tasks = listWithOneTodo();
        String reply = new MarkCommand("1").execute(tasks, ui, storage());

        assertEquals("Closed. Another win:" + NEW_LINE
                + "  [T][X] read book", reply);
    }

    @Test
    public void execute_unmarkFirstTask_replyShowsTaskUnticked() throws HarveyException {
        TaskList tasks = listWithOneTodo();
        new MarkCommand("1").execute(tasks, ui, storage());
        String reply = new UnmarkCommand("1").execute(tasks, ui, storage());

        assertEquals("Reopened. Don't make a habit of it:" + NEW_LINE
                + "  [T][ ] read book", reply);
    }

    @Test
    public void execute_markTaskNumberJustPastEnd_exceptionThrown() throws HarveyException {
        TaskList tasks = listWithOneTodo();
        MarkCommand command = new MarkCommand("2");

        assertThrows(HarveyException.class, () -> command.execute(tasks, ui, storage()));
    }

    @Test
    public void execute_deleteOnlyTask_replyNamesTaskAndEmptiesList() throws HarveyException {
        TaskList tasks = listWithOneTodo();
        String reply = new DeleteCommand("1").execute(tasks, ui, storage());

        assertEquals("Dropped. That one's off the table:" + NEW_LINE
                + "  [T][ ] read book" + NEW_LINE
                + "You've got 0 cases on the docket.", reply);
        assertTrue(tasks.isEmpty());
    }

    @Test
    public void execute_listWithOneTask_replyIsNumberedFromOne() throws HarveyException {
        TaskList tasks = listWithOneTodo();
        String reply = new ListCommand().execute(tasks, ui, storage());

        assertEquals("Here's your docket:" + NEW_LINE
                + "1.[T][ ] read book", reply);
    }

    @Test
    public void execute_listOnEmptyList_exceptionThrown() {
        TaskList tasks = new TaskList();
        ListCommand command = new ListCommand();

        assertThrows(HarveyException.class, () -> command.execute(tasks, ui, storage()));
    }

    @Test
    public void execute_findMatchingKeyword_replyListsOnlyTheMatches() throws HarveyException {
        TaskList tasks = listWithOneTodo();
        new AddCommand(CommandType.TODO, "buy milk").execute(tasks, ui, storage());
        String reply = new FindCommand("milk").execute(tasks, ui, storage());

        // Numbered 1 among the matches, not 2 as it stands in the full list.
        assertEquals("Here's what I dug up:" + NEW_LINE
                + "1.[T][ ] buy milk", reply);
    }

    @Test
    public void execute_findKeywordInNoTask_exceptionThrown() throws HarveyException {
        TaskList tasks = listWithOneTodo();
        FindCommand command = new FindCommand("holiday");

        assertThrows(HarveyException.class, () -> command.execute(tasks, ui, storage()));
    }

    @Test
    public void execute_exit_repliesWithFarewellAndIsExitIsTrue() throws HarveyException {
        ExitCommand command = new ExitCommand();
        String reply = command.execute(new TaskList(), ui, storage());

        assertEquals("We're done here. Go win something.", reply);
        assertTrue(command.isExit());
    }

    @Test
    public void execute_addEventClashingWithExisting_exceptionThrown() throws HarveyException {
        TaskList tasks = new TaskList();
        new AddCommand(CommandType.EVENT,
                "tuition Sarah /from 2026-09-10 1600 /to 2026-09-10 1800")
                .execute(tasks, ui, storage());

        AddCommand clashing = new AddCommand(CommandType.EVENT,
                "tuition Ben /from 2026-09-10 1700 /to 2026-09-10 1900");
        HarveyException e = assertThrows(HarveyException.class, () ->
                clashing.execute(tasks, ui, storage()));

        // The message has to name the event in the way, or the user cannot act on it.
        assertTrue(e.getMessage().contains("tuition Sarah"));
        assertEquals(1, tasks.size());
    }

    @Test
    public void execute_addEventClashingWithExisting_listAndFileAreUnchanged() throws Exception {
        TaskList tasks = new TaskList();
        Storage storage = storage();
        new AddCommand(CommandType.EVENT,
                "tuition Sarah /from 2026-09-10 1600 /to 2026-09-10 1800")
                .execute(tasks, ui, storage);

        AddCommand clashing = new AddCommand(CommandType.EVENT,
                "tuition Ben /from 2026-09-10 1700 /to 2026-09-10 1900");
        assertThrows(HarveyException.class, () -> clashing.execute(tasks, ui, storage));

        // A rejected event must leave no trace, so the saved file still holds one line.
        assertEquals(1, storage.load().size());
    }

    @Test
    public void execute_addEventBackToBackWithExisting_addsIt() throws HarveyException {
        // Consecutive lessons are the normal case for a tutor and must not be refused.
        TaskList tasks = new TaskList();
        new AddCommand(CommandType.EVENT,
                "tuition Sarah /from 2026-09-10 1600 /to 2026-09-10 1800")
                .execute(tasks, ui, storage());
        new AddCommand(CommandType.EVENT,
                "tuition Ben /from 2026-09-10 1800 /to 2026-09-10 2000")
                .execute(tasks, ui, storage());

        assertEquals(2, tasks.size());
    }

    @Test
    public void execute_addEventClashingWithTodo_addsIt() throws HarveyException {
        // A todo occupies no time, so it can never stand in an event's way.
        TaskList tasks = listWithOneTodo();
        new AddCommand(CommandType.EVENT,
                "tuition Sarah /from 2026-09-10 1600 /to 2026-09-10 1800")
                .execute(tasks, ui, storage());

        assertEquals(2, tasks.size());
    }

    @Test
    public void execute_addEventEndingBeforeItStarts_exceptionThrown() {
        TaskList tasks = new TaskList();
        AddCommand command = new AddCommand(CommandType.EVENT,
                "tuition Sarah /from 2026-09-10 1800 /to 2026-09-10 1600");

        HarveyException e = assertThrows(HarveyException.class, () ->
                command.execute(tasks, ui, storage()));
        assertTrue(e.getMessage().contains("end after it starts"));
        assertTrue(tasks.isEmpty());
    }

    @Test
    public void execute_addEventWithUnreadableTime_exceptionThrown() {
        TaskList tasks = new TaskList();
        AddCommand command = new AddCommand(CommandType.EVENT,
                "tuition Sarah /from Mon 2pm /to Mon 4pm");

        HarveyException e = assertThrows(HarveyException.class, () ->
                command.execute(tasks, ui, storage()));
        assertTrue(e.getMessage().contains("yyyy-mm-dd HHmm"));
        assertTrue(tasks.isEmpty());
    }

    @Test
    public void execute_addDuplicateTodoInOtherCase_exceptionThrownAndNothingAdded() throws HarveyException {
        TaskList tasks = listWithOneTodo();
        AddCommand command = new AddCommand(CommandType.TODO, "Read Book");

        HarveyException e = assertThrows(HarveyException.class, () ->
                command.execute(tasks, ui, storage()));
        assertTrue(e.getMessage().contains("task 1"), e.getMessage());
        assertEquals(1, tasks.size());
    }

    @Test
    public void execute_addDuplicateOfClosedTask_messageSuggestsUnmark() throws HarveyException {
        TaskList tasks = listWithOneTodo();
        new MarkCommand("1").execute(tasks, ui, storage());
        AddCommand command = new AddCommand(CommandType.TODO, "read book");

        HarveyException e = assertThrows(HarveyException.class, () ->
                command.execute(tasks, ui, storage()));
        assertTrue(e.getMessage().contains("unmark 1"), e.getMessage());
    }

    @Test
    public void execute_addDeadlineSameDescriptionOtherDate_added() throws HarveyException {
        TaskList tasks = new TaskList();
        new AddCommand(CommandType.DEADLINE, "essay /by 2026-10-01").execute(tasks, ui, storage());
        new AddCommand(CommandType.DEADLINE, "essay /by 2026-10-02").execute(tasks, ui, storage());

        assertEquals(2, tasks.size());
    }

    @Test
    public void execute_addDeadlineWithByTwice_exceptionNamesTheOption() {
        TaskList tasks = new TaskList();
        AddCommand command = new AddCommand(CommandType.DEADLINE, "essay /by 2026-10-01 /by 2026-11-01");

        HarveyException e = assertThrows(HarveyException.class, () ->
                command.execute(tasks, ui, storage()));
        assertTrue(e.getMessage().contains("/by more than once"), e.getMessage());
        assertTrue(tasks.isEmpty());
    }

    @Test
    public void execute_addEventWithToBeforeFrom_exceptionExplainsOrder() {
        TaskList tasks = new TaskList();
        AddCommand command = new AddCommand(CommandType.EVENT,
                "tuition Sarah /to 2026-09-10 1800 /from 2026-09-10 1600");

        HarveyException e = assertThrows(HarveyException.class, () ->
                command.execute(tasks, ui, storage()));
        assertTrue(e.getMessage().contains("/from before /to"), e.getMessage());
        assertTrue(tasks.isEmpty());
    }

    @Test
    public void execute_markTaskAlreadyDone_exceptionThrown() throws HarveyException {
        TaskList tasks = listWithOneTodo();
        new MarkCommand("1").execute(tasks, ui, storage());
        MarkCommand command = new MarkCommand("1");

        assertThrows(HarveyException.class, () -> command.execute(tasks, ui, storage()));
    }

    @Test
    public void execute_unmarkTaskNotDone_exceptionThrownAndTaskUnchanged() throws HarveyException {
        TaskList tasks = listWithOneTodo();
        UnmarkCommand command = new UnmarkCommand("1");

        assertThrows(HarveyException.class, () -> command.execute(tasks, ui, storage()));
        Task task = tasks.get(1);
        assertTrue(!task.isDone());
    }

    @Test
    public void execute_markWithoutNumber_exceptionShowsExample() throws HarveyException {
        TaskList tasks = listWithOneTodo();
        MarkCommand command = new MarkCommand("");

        HarveyException e = assertThrows(HarveyException.class, () -> command.execute(tasks, ui, storage()));
        assertTrue(e.getMessage().contains("mark 2"), e.getMessage());
    }

    @Test
    public void execute_markOnEmptyList_exceptionSaysNothingToMark() {
        // Checked before the number, so the user is not told to pick from 1 to 0.
        MarkCommand command = new MarkCommand("1");

        HarveyException e = assertThrows(HarveyException.class, () ->
                command.execute(new TaskList(), ui, storage()));
        assertTrue(e.getMessage().contains("no tasks yet"), e.getMessage());
    }

    @Test
    public void execute_markWord_exceptionSaysNotATaskNumber() throws HarveyException {
        TaskList tasks = listWithOneTodo();
        MarkCommand command = new MarkCommand("book");

        HarveyException e = assertThrows(HarveyException.class, () -> command.execute(tasks, ui, storage()));
        assertTrue(e.getMessage().contains("\"book\" is not a task number"), e.getMessage());
    }

    @Test
    public void execute_deleteTaskNumberJustPastEnd_nothingRemoved() throws HarveyException {
        TaskList tasks = listWithOneTodo();
        DeleteCommand command = new DeleteCommand("2");

        assertThrows(HarveyException.class, () -> command.execute(tasks, ui, storage()));
        assertEquals(1, tasks.size());
    }

    @Test
    public void execute_addTodoWithoutDescription_exceptionUsesArticleA() {
        AddCommand command = new AddCommand(CommandType.TODO, "");

        HarveyException e = assertThrows(HarveyException.class, () ->
                command.execute(new TaskList(), ui, storage()));
        assertTrue(e.getMessage().startsWith("A todo needs a description."), e.getMessage());
    }

    @Test
    public void execute_addEventWithoutDescription_exceptionUsesArticleAn() {
        AddCommand command = new AddCommand(CommandType.EVENT, "");

        HarveyException e = assertThrows(HarveyException.class, () ->
                command.execute(new TaskList(), ui, storage()));
        assertTrue(e.getMessage().startsWith("An event needs a description."), e.getMessage());
    }

    @Test
    public void execute_addDeadlineWithoutBy_exceptionAsksForDueDate() {
        AddCommand command = new AddCommand(CommandType.DEADLINE, "essay");

        HarveyException e = assertThrows(HarveyException.class, () ->
                command.execute(new TaskList(), ui, storage()));
        assertTrue(e.getMessage().contains("needs a due date after /by"), e.getMessage());
    }

    @Test
    public void execute_addDeadlineWithNothingAfterBy_exceptionAsksForDueDate() {
        AddCommand command = new AddCommand(CommandType.DEADLINE, "essay /by");

        HarveyException e = assertThrows(HarveyException.class, () ->
                command.execute(new TaskList(), ui, storage()));
        assertTrue(e.getMessage().contains("needs a due date after /by"), e.getMessage());
    }

    @Test
    public void execute_addDeadlineWithNothingBeforeBy_exceptionThrown() {
        // A description made only of the option would otherwise save a task named "".
        AddCommand command = new AddCommand(CommandType.DEADLINE, "/by 2026-10-01");

        assertThrows(HarveyException.class, () -> command.execute(new TaskList(), ui, storage()));
    }

    @Test
    public void execute_addEventWithoutTo_exceptionExplainsBothOptions() {
        AddCommand command = new AddCommand(CommandType.EVENT, "tuition /from 2026-09-10 1600");

        HarveyException e = assertThrows(HarveyException.class, () ->
                command.execute(new TaskList(), ui, storage()));
        assertTrue(e.getMessage().contains("/from") && e.getMessage().contains("/to"), e.getMessage());
    }

    @Test
    public void execute_findWithoutKeyword_exceptionShowsExample() throws HarveyException {
        TaskList tasks = listWithOneTodo();
        FindCommand command = new FindCommand("");

        HarveyException e = assertThrows(HarveyException.class, () -> command.execute(tasks, ui, storage()));
        assertTrue(e.getMessage().contains("find book"), e.getMessage());
    }

    @Test
    public void execute_addTodo_taskWrittenToSaveFile() throws Exception {
        // The reply is checked elsewhere; this checks the command really saved.
        new AddCommand(CommandType.TODO, "read book").execute(new TaskList(), ui, storage());

        assertEquals(List.of("T | 0 | read book"),
                Files.readAllLines(tempDir.resolve("harvey.txt")));
    }
}
