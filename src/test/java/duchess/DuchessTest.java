package duchess;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import duchess.storage.Storage;
import duchess.task.Task;
import duchess.task.TaskList;
import duchess.task.Todo;

/** Tests the command-response boundary shared by the CLI and JavaFX interfaces. */
public class DuchessTest {
    @TempDir
    private Path temporaryDirectory;

    /** Verifies that the storage-backed constructor restores persisted tasks. */
    @Test
    public void constructor_nullTaskList_loadsTasksFromStorage() throws Exception {
        Storage storage = new Storage(dataFile());
        storage.saveTasks(new TaskList(new Todo("saved task")));

        Duchess duchess = new Duchess(storage, null);

        assertEquals("Here are the tasks in your list:\n1.[T][ ] saved task",
                duchess.getResponse("list"));
    }

    /** Verifies collaborator preconditions used by the application constructor. */
    @Test
    public void constructor_nullCollaborator_throwsAssertionError() {
        assertAll(
                () -> assertThrows(AssertionError.class,
                        () -> new Duchess(null, new TaskList(), Clock.systemUTC())),
                () -> assertThrows(AssertionError.class,
                        () -> new Duchess(new Storage(dataFile()), new TaskList(), null))
        );
    }

    /** Verifies that an unsaved change is displayed as an error in the GUI. */
    @Test
    public void getResponse_saveFailure_setsErrorStyle() throws Exception {
        Path blockedParent = temporaryDirectory.resolve("not-a-directory");
        Files.writeString(blockedParent, "block directory creation");
        Duchess duchess = new Duchess(new Storage(blockedParent.resolve("tasks.txt")), new TaskList());
        String response = duchess.getResponse("todo read book");
        assertTrue(response.contains("couldn't save"));
        assertEquals("error", duchess.getCommandType());
        assertTrue(duchess.getResponse("list").contains("read book"));
    }

    /** Verifies retrying after a write failure persists every change kept in memory. */
    @Test
    public void getResponse_saveFailureThenRecovery_savesEntireList() throws Exception {
        Path parent = temporaryDirectory.resolve("blocked");
        Files.writeString(parent, "block directory creation");
        Storage storage = new Storage(parent.resolve("tasks.txt"));
        Duchess duchess = new Duchess(storage, new TaskList());

        assertTrue(duchess.getResponse("todo first").contains("couldn't save"));
        assertTrue(duchess.getResponse("mark 1").contains("couldn't save"));
        Files.delete(parent);
        assertEquals("added: [T][ ] second", duchess.getResponse("todo second"));

        TaskList restored = storage.loadTasks();
        assertEquals(2, restored.size());
        assertEquals("first", restored.get(0).getDescription());
        assertTrue(restored.get(0).isDone());
        assertEquals("second", restored.get(1).getDescription());
    }

    /** Verifies that the GUI-facing response includes the current task list. */
    @Test
    public void getResponse_listCommand_returnsTaskListResponse() {
        Duchess duchess = createDuchess(new TaskList());

        String response = duchess.getResponse("list");

        assertAll(
                () -> assertEquals("Here are the tasks in your list:", response),
                () -> assertEquals("list", duchess.getCommandType()),
                () -> assertFalse(duchess.isExitRequested())
        );
    }

    /** Verifies that a populated list is numbered in insertion order. */
    @Test
    public void getResponse_listCommandWithTasks_returnsNumberedTasks() {
        Duchess duchess = createDuchess(new TaskList(
                new Todo("first task"), new Todo("second task")));

        String response = duchess.getResponse("list");

        assertEquals("Here are the tasks in your list:\n"
                + "1.[T][ ] first task\n"
                + "2.[T][ ] second task", response);
    }

    /** Verifies that null input is handled as an empty command. */
    @Test
    public void getResponse_nullCommand_returnsEmptyCommandError() {
        Duchess duchess = createDuchess(new TaskList());

        String response = duchess.getResponse(null);

        assertTrue(response.startsWith("OOPS!!! A command cannot be empty."));
        assertEquals("error", duchess.getCommandType());
    }

    /** Verifies that leading, trailing, and repeated command whitespace is harmless. */
    @Test
    public void getResponse_whitespacePaddedCommands_processesNormally() {
        Duchess duchess = createDuchess(new TaskList());

        String addResponse = duchess.getResponse("   todo    read book   ");
        String markResponse = duchess.getResponse("  mark     1  ");

        assertAll(
                () -> assertEquals("added: [T][ ] read book", addResponse),
                () -> assertTrue(markResponse.contains("[T][X] read book"))
        );
    }

    /** Verifies that the help command lists every supported command and input format. */
    @Test
    public void getResponse_helpCommand_listsAvailableCommands() {
        Duchess duchess = createDuchess(new TaskList());

        String response = duchess.getResponse("help");

        assertAll(
                () -> assertTrue(response.contains("todo <description>")),
                () -> assertTrue(response.contains("deadline <description> /by <date>")),
                () -> assertTrue(response.contains("event <description> /from <date> /to <date>")),
                () -> assertTrue(response.contains("find <keyword>")),
                () -> assertTrue(response.contains("case <description>")),
                () -> assertTrue(response.contains("rollcall")),
                () -> assertTrue(response.contains("intel <keyword>")),
                () -> assertTrue(response.contains("signoff")),
                () -> assertTrue(response.contains("mark <task number>")),
                () -> assertTrue(response.contains("unmark <task number>")),
                () -> assertTrue(response.contains("delete <task number>")),
                () -> assertTrue(response.contains("help")),
                () -> assertTrue(response.contains("bye")),
                () -> assertEquals("help", duchess.getCommandType())
        );
    }

    /** Verifies that squad aliases preserve the behavior of their canonical commands. */
    @Test
    public void getResponse_squadAliases_preserveCommandBehavior() {
        Duchess duchess = createDuchess(new TaskList());

        String addResponse = duchess.getResponse("case read book");
        String deadlineResponse = duchess.getResponse("timer return book /by 2026-09-30");
        String eventResponse = duchess.getResponse("briefing team meeting /at Friday 3pm");
        String findResponse = duchess.getResponse("intel book");
        String closeResponse = duchess.getResponse("close 1");
        String reopenResponse = duchess.getResponse("reopen 1");
        String archiveResponse = duchess.getResponse("archive 2");
        String rollcallResponse = duchess.getResponse("rollcall");
        String reportResponse = duchess.getResponse("report");
        String briefResponse = duchess.getResponse("brief");

        assertAll(
                () -> assertTrue(addResponse.startsWith("added:")),
                () -> assertTrue(deadlineResponse.startsWith("added:")),
                () -> assertTrue(eventResponse.startsWith("added:")),
                () -> assertTrue(findResponse.contains("read book")),
                () -> assertTrue(closeResponse.contains("marked this task as done")),
                () -> assertTrue(reopenResponse.contains("marked this task as not done")),
                () -> assertTrue(archiveResponse.contains("removed this task")),
                () -> assertTrue(rollcallResponse.contains("Here are the tasks")),
                () -> assertTrue(reportResponse.contains("Task statistics:")),
                () -> assertTrue(briefResponse.contains("Squad playbook:"))
        );

        duchess.getResponse("signoff");
        assertTrue(duchess.isExitRequested());
    }

    /** Verifies that the help response communicates Duchess's squad-room personality. */
    @Test
    public void getResponse_helpCommand_usesSquadRoomVoice() {
        Duchess duchess = createDuchess(new TaskList());

        String response = duchess.getResponse("help");

        assertTrue(response.contains("The squad desk is open."));
        assertTrue(response.contains("I'll keep the banter light and your case file organized."));
    }

    /** Verifies that the GUI can process the command used to end a conversation. */
    @Test
    public void getResponse_byeCommand_requestsExit() {
        Duchess duchess = createDuchess(new TaskList());

        String response = duchess.getResponse("bye");

        assertAll(
                () -> assertEquals("Bye. Hope to see you again soon!", response),
                () -> assertEquals("bye", duchess.getCommandType()),
                () -> assertTrue(duchess.isExitRequested())
        );
    }

    /** Verifies that statistics use the injected current time and exact response format. */
    @Test
    public void getResponse_statsCommand_returnsStatistics() {
        Instant now = Instant.parse("2026-09-11T00:00:00Z");
        Task completedTask = new Todo("completed task");
        TaskList tasks = new TaskList(completedTask, new Todo("pending task"));
        Duchess duchess = new Duchess(new Storage(dataFile()), tasks, Clock.fixed(now, ZoneOffset.UTC));

        duchess.getResponse("mark 1");
        String response = duchess.getResponse("STATS");

        assertAll(
                () -> assertEquals("Task statistics:\n"
                        + "Total tasks: 2\n"
                        + "Completed tasks: 1\n"
                        + "Incomplete tasks: 1\n"
                        + "Completed in the past 7 days: 1\n"
                        + "Completion rate: 50%", response),
                () -> assertEquals("stats", duchess.getCommandType())
        );
    }

    /** Verifies that the stats command rejects arguments instead of silently ignoring them. */
    @Test
    public void getResponse_statsCommandWithArguments_returnsHelpfulError() {
        Duchess duchess = createDuchess(new TaskList());

        String response = duchess.getResponse("stats today");

        assertEquals("OOPS!!! Please use 'stats' without arguments.", response);
    }

    /** Verifies that every argument-free command rejects unexpected parameters. */
    @Test
    public void getResponse_argumentFreeCommandsWithArguments_returnHelpfulErrors() {
        Duchess duchess = createDuchess(new TaskList());

        assertAll(
                () -> assertEquals("OOPS!!! Please use 'list' without arguments.",
                        duchess.getResponse("list now")),
                () -> assertEquals("OOPS!!! Please use 'help' without arguments.",
                        duchess.getResponse("help now")),
                () -> assertEquals("OOPS!!! Please use 'bye' without arguments.",
                        duchess.getResponse("bye now")),
                () -> assertFalse(duchess.isExitRequested())
        );
    }

    /** Verifies that adding and finding commands share the same task state. */
    @Test
    public void getResponse_addAndFindCommands_shareTaskState() {
        Duchess duchess = createDuchess(new TaskList());

        duchess.getResponse("todo read book");
        duchess.getResponse("deadline return book /by 2019-12-02");
        String response = duchess.getResponse("find BOOK");

        assertEquals("Here are the matching tasks in your list:\n"
                + "1.[T][ ] read book\n"
                + "2.[D][ ] return book (by: Dec 02 2019)", response);
    }

    /** Verifies missing and unmatched searches return stable, helpful responses. */
    @Test
    public void getResponse_findMissingOrUnmatchedKeyword_returnsExpectedResponses() {
        Duchess duchess = createDuchess(new TaskList(new Todo("read book")));

        String missingKeywordResponse = duchess.getResponse("find");
        String unmatchedKeywordResponse = duchess.getResponse("find code");

        assertAll(
                () -> assertEquals("OOPS!!! Please use 'find <keyword>', for example: find book.",
                        missingKeywordResponse),
                () -> assertEquals("No matching tasks found. Try a different keyword.", unmatchedKeywordResponse)
        );
    }

    /** Verifies that a number shown by search selects the same task in indexed commands. */
    @Test
    public void getResponse_findResultNumber_marksMatchingTask() {
        TaskList tasks = new TaskList(new Todo("first"), new Todo("second"), new Todo("target"));
        Duchess duchess = createDuchess(tasks);

        assertEquals("Here are the matching tasks in your list:\n3.[T][ ] target",
                duchess.getResponse("find target"));
        duchess.getResponse("mark 3");
        assertTrue(tasks.get(2).isDone());
        assertFalse(tasks.get(0).isDone());
    }

    /** Verifies both interfaces receive a warning and failed saves preserve damaged data. */
    @Test
    public void getStartupWarning_damagedFile_protectsOriginalData() throws Exception {
        Files.writeString(dataFile(), "damaged record\n");
        Duchess duchess = new Duchess(new Storage(dataFile()), null);

        assertTrue(duchess.getStartupWarning().contains("Saving is disabled"));
        assertTrue(duchess.getResponse("todo temporary task").contains("couldn't save"));
        assertTrue(duchess.getResponse("list").contains("temporary task"));
        assertEquals("damaged record\n", Files.readString(dataFile()));
        assertEquals("", createDuchess(new TaskList()).getStartupWarning());
    }

    /** Verifies that equivalent task details cannot be added twice. */
    @Test
    public void getResponse_duplicateTask_returnsErrorWithoutAddingTask() {
        TaskList tasks = new TaskList();
        Duchess duchess = createDuchess(tasks);

        duchess.getResponse("deadline Submit   Report /by 2026-09-30");
        String response = duchess.getResponse("deadline submit report /by 2026-09-30");

        assertAll(
                () -> assertEquals("OOPS!!! That task already exists in your list.", response),
                () -> assertEquals("error", duchess.getCommandType()),
                () -> assertEquals(1, tasks.size())
        );
    }

    /** Verifies that indexed commands explain when no task can be selected. */
    @Test
    public void getResponse_taskNumberOnEmptyList_returnsEmptyListError() {
        Duchess duchess = createDuchess(new TaskList());

        String response = duchess.getResponse("delete 1");

        assertEquals("OOPS!!! Your task list is empty, so there is no task to update.", response);
    }

    /** Verifies missing numbers and out-of-range numbers are rejected without mutation. */
    @Test
    public void getResponse_invalidIndexedCommands_returnSpecificErrors() {
        TaskList tasks = new TaskList(new Todo("only task"));
        Duchess duchess = createDuchess(tasks);

        assertAll(
                () -> assertEquals("OOPS!!! Please use 'mark <task number>', for example: mark 1.",
                        duchess.getResponse("mark")),
                () -> assertEquals("OOPS!!! Please use 'unmark <task number>', for example: unmark 1.",
                        duchess.getResponse("unmark")),
                () -> assertEquals("OOPS!!! Please use 'delete <task number>', for example: delete 1.",
                        duchess.getResponse("delete")),
                () -> assertEquals("OOPS!!! Please provide a valid task number between 1 and 1.",
                        duchess.getResponse("delete 2")),
                () -> assertEquals(1, tasks.size())
        );
    }

    /** Verifies that mark, unmark, and delete commands update task state in order. */
    @Test
    public void getResponse_markUnmarkDeleteCommands_updateTaskState() {
        TaskList tasks = new TaskList(new Todo("first task"), new Todo("second task"));
        Duchess duchess = createDuchess(tasks);

        duchess.getResponse("mark 1");
        duchess.getResponse("unmark 1");
        String response = duchess.getResponse("delete 2");

        assertAll(
                () -> assertEquals("Noted. I've removed this task:\n  [T][ ] second task\n"
                        + "Now you have 1 tasks in the list.", response),
                () -> assertFalse(tasks.get(0).isDone()),
                () -> assertEquals(1, tasks.size())
        );
    }

    /** Verifies that invalid commands set the GUI-facing error category. */
    @Test
    public void getResponse_invalidCommand_setsErrorCommandType() {
        Duchess duchess = createDuchess(new TaskList());

        String response = duchess.getResponse("not a command");

        assertAll(
                () -> assertTrue(response.startsWith("OOPS!!!")),
                () -> assertEquals("error", duchess.getCommandType()),
                () -> assertFalse(duchess.isExitRequested())
        );
    }

    /** Returns a Duchess instance whose persistence is isolated to this test. */
    private Duchess createDuchess(TaskList tasks) {
        return new Duchess(new Storage(dataFile()), tasks);
    }

    /** Returns the temporary data file used by this test class. */
    private Path dataFile() {
        return temporaryDirectory.resolve("duchess.txt");
    }
}
