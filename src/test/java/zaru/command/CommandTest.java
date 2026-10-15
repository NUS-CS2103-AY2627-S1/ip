package zaru.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import zaru.exception.ZaruException;
import zaru.storage.Storage;
import zaru.task.TaskList;
import zaru.task.ToDo;

/** Tests command execution that changes task-list state. */
public class CommandTest {
    @TempDir
    Path temporaryDirectory;

    /** Verifies that executing a todo command adds an incomplete todo task. */
    @Test
    public void todoCommand_execute_addsTask() throws ZaruException {
        TaskList tasks = createTaskList("todo.txt");

        String response = new TodoCommand("read book").execute(tasks);

        assertEquals(1, tasks.size());
        assertEquals("[T][ ] read book", tasks.getTaskString(1));
        assertEquals("Got it! I've tucked this task into your list:\n   [T][ ] read book\nYou now have 1 task.",
                response);
    }

    /** Verifies that executing a deadline command adds a deadline task. */
    @Test
    public void deadlineCommand_execute_addsDeadline() throws ZaruException {
        TaskList tasks = createTaskList("deadline.txt");

        new DeadlineCommand("submit report", "2026-08-19 1430").execute(tasks);

        assertEquals(1, tasks.size());
        assertEquals("[D][ ] submit report (by: Aug 19 2026, 2:30PM)", tasks.getTaskString(1));
    }

    /** Verifies that executing an event command adds an event task. */
    @Test
    public void eventCommand_execute_addsEvent() throws ZaruException {
        TaskList tasks = createTaskList("event.txt");

        new EventCommand("project meeting", "2026-08-20 1000", "2026-08-20 1100").execute(tasks);

        assertEquals(1, tasks.size());
        assertEquals("[E][ ] project meeting (from: Aug 20 2026, 10:00AM) (to: Aug 20 2026, 11:00AM)",
                tasks.getTaskString(1));
    }

    /** Verifies that mark, unmark, and delete commands update task state. */
    @Test
    public void markUnmarkDeleteCommands_execute_updatesTaskList() throws ZaruException {
        TaskList tasks = createTaskList("state-changes.txt");
        new TodoCommand("read book").execute(tasks);

        new MarkCommand("1").execute(tasks);
        assertEquals("[T][x] read book", tasks.getTaskString(1));

        new UnmarkCommand("1").execute(tasks);
        assertEquals("[T][ ] read book", tasks.getTaskString(1));

        new DeleteCommand("1").execute(tasks);
        assertEquals(0, tasks.size());
    }

    /** Verifies that a deadline without a due date is rejected. */
    @Test
    public void deadlineCommand_missingDueDate_throwsException() {
        TaskList tasks = createTaskList("invalid-deadline.txt");

        ZaruException exception = assertThrows(ZaruException.class, () ->
                new DeadlineCommand("submit report", null).execute(tasks));

        assertEquals("Please provide a deadline date using /by.", exception.getMessage());
    }

    /** Verifies that an event must end after it starts. */
    @Test
    public void eventCommand_nonIncreasingTimeRange_throwsException() {
        TaskList tasks = createTaskList("invalid-event-range.txt");

        ZaruException sameTimeException = assertThrows(ZaruException.class, () ->
                new EventCommand("meeting", "2026-08-20 1000", "2026-08-20 1000").execute(tasks));
        ZaruException reversedTimeException = assertThrows(ZaruException.class, () ->
                new EventCommand("meeting", "2026-08-20 1100", "2026-08-20 1000").execute(tasks));

        assertEquals("Event start time must be earlier than its end time.", sameTimeException.getMessage());
        assertEquals("Event start time must be earlier than its end time.", reversedTimeException.getMessage());
        assertEquals(0, tasks.size());
    }

    /** Verifies that both event boundary parameters are required. */
    @Test
    public void eventCommand_missingEndTime_throwsException() {
        TaskList tasks = createTaskList("missing-event-end.txt");

        ZaruException exception = assertThrows(ZaruException.class, () ->
                new EventCommand("meeting", "2026-08-20 1000", null).execute(tasks));

        assertEquals("Please provide an event end time using /to.", exception.getMessage());
        assertEquals(0, tasks.size());
    }

    /** Verifies that the save-file field separator cannot appear in a task description. */
    @Test
    public void todoCommand_reservedCharacter_throwsException() {
        TaskList tasks = createTaskList("reserved-character.txt");

        ZaruException exception = assertThrows(ZaruException.class, () ->
                new TodoCommand("read | write").execute(tasks));

        assertEquals("Task descriptions cannot contain the reserved | character.", exception.getMessage());
        assertEquals(0, tasks.size());
    }

    /** Verifies that a task number containing extra characters is rejected. */
    @Test
    public void markCommand_nonNumericTaskNumber_throwsException() throws ZaruException {
        TaskList tasks = createTaskList("invalid-number.txt");
        tasks.add(new ToDo("read book"));

        ZaruException exception = assertThrows(ZaruException.class, () -> new MarkCommand("1st").execute(tasks));

        assertEquals("Please provide one whole task number.", exception.getMessage());
    }

    /** Verifies command words, default metadata, and exit metadata for every command type. */
    @Test
    public void commandMetadata_allCommandTypes_returnsExpectedValues() {
        assertEquals("bye", new ByeCommand().getCommand());
        assertEquals("list", new ListCommand().getCommand());
        assertEquals("sort", new SortCommand().getCommand());
        assertEquals("mark", new MarkCommand("1").getCommand());
        assertEquals("unmark", new UnmarkCommand("1").getCommand());
        assertEquals("delete", new DeleteCommand("1").getCommand());
        assertEquals("todo", new TodoCommand("read book").getCommand());
        assertEquals("deadline", new DeadlineCommand("report", "2026-09-08").getCommand());
        assertEquals("event", new EventCommand("meeting", "2026-09-08 0900", "2026-09-08 1000")
                .getCommand());
        assertEquals("find", new FindCommand("book").getCommand());
        assertTrue(new ByeCommand().isExit());
        assertFalse(new TodoCommand("read book").isExit());
    }

    /** Verifies that listing a populated task list includes every numbered task. */
    @Test
    public void listCommand_populatedTaskList_returnsTasks() throws ZaruException {
        TaskList tasks = createTaskList("list-populated.txt");
        tasks.add(new ToDo("read book"));
        tasks.add(new ToDo("write notes"));

        String response = new ListCommand().execute(tasks);

        assertEquals("Here are your tasks:\n1. [T][ ] read book\n2. [T][ ] write notes", response);
    }

    /** Verifies that sorting an empty list returns the empty-list response. */
    @Test
    public void sortCommand_emptyTaskList_returnsEmptyMessage() throws ZaruException {
        TaskList tasks = createTaskList("sort-empty.txt");

        String response = new SortCommand().execute(tasks);

        assertEquals("You have no tasks to sort!", response);
    }

    /** Verifies all task-number boundary and overflow errors. */
    @Test
    public void markCommand_invalidTaskNumber_throwsSpecificException() throws ZaruException {
        TaskList tasks = createTaskList("invalid-number-boundaries.txt");
        tasks.add(new ToDo("read book"));

        ZaruException missing = assertThrows(ZaruException.class, () -> new MarkCommand(" ").execute(tasks));
        ZaruException tooLow = assertThrows(ZaruException.class, () -> new MarkCommand("0").execute(tasks));
        ZaruException tooHigh = assertThrows(ZaruException.class, () -> new MarkCommand("2").execute(tasks));
        ZaruException overflow = assertThrows(ZaruException.class, () ->
                new MarkCommand("999999999999999999999999").execute(tasks));

        assertEquals("Please provide a number.", missing.getMessage());
        assertEquals("Task number must be between 1 and 1!", tooLow.getMessage());
        assertEquals("Task number must be between 1 and 1!", tooHigh.getMessage());
        assertEquals("Number 999999999999999999999999 must be a valid number.", overflow.getMessage());
    }

    /** Verifies task-description length and control-character restrictions. */
    @Test
    public void todoCommand_unsafeDescription_throwsSpecificException() {
        TaskList tasks = createTaskList("unsafe-description.txt");

        ZaruException tooLong = assertThrows(ZaruException.class, () ->
                new TodoCommand("a".repeat(501)).execute(tasks));
        ZaruException controlCharacter = assertThrows(ZaruException.class, () ->
                new TodoCommand("read\nbook").execute(tasks));

        assertEquals("Task descriptions cannot exceed 500 characters.", tooLong.getMessage());
        assertEquals("Task descriptions cannot contain line breaks or control characters.",
                controlCharacter.getMessage());
    }

    /** Verifies pluralization after adding more than one task. */
    @Test
    public void todoCommand_secondTask_usesPluralCount() throws ZaruException {
        TaskList tasks = createTaskList("plural-count.txt");
        new TodoCommand("read book").execute(tasks);

        String response = new TodoCommand("write notes").execute(tasks);

        assertTrue(response.endsWith("You now have 2 tasks."));
    }

    /** Verifies that a find command requires a search target. */
    @Test
    public void findCommand_emptyKeyword_throwsException() {
        TaskList tasks = createTaskList("find-empty.txt");

        ZaruException exception = assertThrows(ZaruException.class, () -> new FindCommand(" ").execute(tasks));

        assertEquals("Include a search target!", exception.getMessage());
    }

    /** Verifies that a find command displays only tasks matching the search text. */
    @Test
    public void findCommand_execute_displaysMatchingTasks() throws ZaruException {
        TaskList tasks = createTaskList("find.txt");
        tasks.add(new ToDo("read book"));
        tasks.add(new ToDo("return book"));
        tasks.add(new ToDo("watch movie"));

        String response = new FindCommand("book").execute(tasks);

        assertTrue(response.contains("[T][ ] read book"));
        assertTrue(response.contains("[T][ ] return book"));
        assertFalse(response.contains("watch movie"));
    }

    /** Verifies that the sort command sorts tasks and displays their new order. */
    @Test
    public void sortCommand_execute_sortsAndDisplaysTasks() throws ZaruException {
        TaskList tasks = createTaskList("sort.txt");
        tasks.add(new ToDo("write notes"));
        tasks.add(new ToDo("buy book"));

        String response = new SortCommand().execute(tasks);

        assertEquals("Here are your sorted tasks:\n1. [T][ ] buy book\n2. [T][ ] write notes", response);
    }

    /** Verifies that an empty task list receives Zaru's lightly themed response. */
    @Test
    public void listCommand_emptyTaskList_returnsPersonableResponse() {
        TaskList tasks = createTaskList("empty-list.txt");

        String response = new ListCommand().execute(tasks);

        assertEquals("Your task list is empty. Nothing to pounce on yet!", response);
    }

    /** Verifies that the exit command receives Zaru's lightly themed farewell. */
    @Test
    public void byeCommand_execute_returnsPersonableResponse() {
        TaskList tasks = createTaskList("bye.txt");

        String response = new ByeCommand().execute(tasks);

        assertEquals("Bye! I'll be curled up here when you need me.", response);
    }

    /** Creates a task list backed by a temporary save file. */
    private TaskList createTaskList(String fileName) {
        return new TaskList(new Storage(temporaryDirectory.resolve(fileName)));
    }
}
