package bobby;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import bobby.Bobby.CommandResult;
import bobby.task.TaskList;
import bobby.task.Todo;

/**
 * Tests Bobby's command responses independently of either user interface.
 */
class BobbyTest {
    private static final Clock FIXED_CLOCK = Clock.fixed(
            Instant.parse("2026-09-10T04:00:00Z"), ZoneId.of("Asia/Singapore"));
    private static final Path SAVE_FILE = Path.of("data", "bobby.txt");

    private byte[] originalSaveFile;

    /**
     * Backs up an existing save file before tests exercise mutating Bobby commands.
     */
    @BeforeEach
    void backUpExistingSaveFile() throws IOException {
        if (Files.exists(SAVE_FILE)) {
            originalSaveFile = Files.readAllBytes(SAVE_FILE);
        }
    }

    /**
     * Restores the original save file after each test.
     */
    @AfterEach
    void restoreOriginalSaveFile() throws IOException {
        Files.deleteIfExists(SAVE_FILE);
        if (originalSaveFile != null) {
            Files.write(SAVE_FILE, originalSaveFile);
        }
    }

    /**
     * Verifies that listing tasks returns their numbered display forms.
     */
    @Test
    void getResponse_listCommand_numberedTaskListReturned() {
        Bobby bobby = new Bobby(new TaskList(List.of(
                new Todo("read book"),
                new Todo("write essay"))));

        String response = bobby.getResponse("list");

        assertEquals("Behold, the full register of thy appointed duties:\n"
                + "     1.[T][ ] read book\n"
                + "     2.[T][ ] write essay", response);
    }

    /**
     * Verifies that listing and finding in an empty register return their specific empty messages.
     */
    @Test
    void getResponse_emptyListAndFindCommands_specificEmptyMessagesReturned() {
        Bobby bobby = new Bobby(new TaskList());

        assertEquals("Behold, the full register of thy appointed duties:\n"
                        + "The royal register standeth presently unburdened; no duty hath yet been inscribed.",
                bobby.getResponse("list"));
        assertEquals("Behold, the duties answering thy inquiry:\n"
                        + "Alas, no duty within the register answereth thy inquiry.",
                bobby.getResponse("find missing"));
    }

    /**
     * Verifies that finding tasks returns matching descriptions only.
     */
    @Test
    void getResponse_findCommand_matchingTasksReturned() {
        Bobby bobby = new Bobby(new TaskList(List.of(
                new Todo("Read book"),
                new Todo("buy groceries"))));

        String response = bobby.getResponse("find book");

        assertEquals("Behold, the duties answering thy inquiry:\n"
                + "     1.[T][ ] Read book", response);
    }

    /**
     * Verifies that statistics use the current calendar week and current task states.
     */
    @Test
    void getResponse_statsCommand_currentTaskStatisticsReturned() {
        Todo completedThisWeek = new Todo("read book");
        completedThisWeek.markAsDone(LocalDateTime.of(2026, 9, 7, 0, 0));
        Todo completedBeforeThisWeek = new Todo("submit essay");
        completedBeforeThisWeek.markAsDone(LocalDateTime.of(2026, 9, 6, 23, 59, 59));
        Todo completedAtUnknownTime = new Todo("legacy task");
        completedAtUnknownTime.markAsDone();
        Todo pendingTask = new Todo("buy groceries");
        Bobby bobby = new Bobby(new TaskList(List.of(
                completedThisWeek, completedBeforeThisWeek, completedAtUnknownTime, pendingTask)),
                FIXED_CLOCK);

        String response = bobby.getResponse("stats");

        assertEquals("Attend now to the formal reckoning of thy duties:\n"
                + "     Period under review: Sep 07 2026 to Sep 13 2026\n"
                + "     Accomplished within the present week: 1\n"
                + "     Accomplished across all recorded time: 3\n"
                + "     Yet awaiting fulfilment: 1\n"
                + "     Total duties inscribed: 4", response);
    }

    /**
     * Verifies that statistics for an empty task list contain zero-valued counts.
     */
    @Test
    void getResponse_statsCommandWithEmptyList_zeroStatisticsReturned() {
        Bobby bobby = new Bobby(new TaskList(), FIXED_CLOCK);

        String response = bobby.getResponse("stats");

        assertEquals("Attend now to the formal reckoning of thy duties:\n"
                + "     Period under review: Sep 07 2026 to Sep 13 2026\n"
                + "     Accomplished within the present week: 0\n"
                + "     Accomplished across all recorded time: 0\n"
                + "     Yet awaiting fulfilment: 0\n"
                + "     Total duties inscribed: 0", response);
    }

    /**
     * Verifies that invalid input is converted into a user-facing response.
     */
    @Test
    void getResponse_invalidCommand_errorResponseReturned() {
        Bobby bobby = new Bobby(new TaskList());

        String response = bobby.getResponse("unknown");

        assertEquals("     Prithee, forgive this humble steward, for thy decree exceedeth my understanding. "
                + "I beseech thee, employ one of the appointed commands.", response);
    }

    /**
     * Verifies that GUI clients can distinguish command errors from successful replies.
     */
    @Test
    void getCommandResult_invalidAndValidCommands_errorStateReturned() {
        Bobby bobby = new Bobby(new TaskList());

        CommandResult invalidResult = bobby.getCommandResult("unknown");
        CommandResult validResult = bobby.getCommandResult("list");

        assertTrue(invalidResult.isError());
        assertFalse(validResult.isError());
        assertEquals("     Prithee, forgive this humble steward, for thy decree exceedeth my understanding. "
                        + "I beseech thee, employ one of the appointed commands.",
                invalidResult.getMessage());
    }

    /**
     * Verifies that an equivalent task cannot be added twice.
     */
    @Test
    void getCommandResult_duplicateTask_errorReturnedAndTaskListUnchanged() {
        TaskList taskList = new TaskList(List.of(new Todo("Read book")));
        Bobby bobby = new Bobby(taskList);

        CommandResult result = bobby.getCommandResult("todo read book");

        assertTrue(result.isError());
        assertEquals("     That very duty already standeth upon the royal register.", result.getMessage());
        assertEquals(1, taskList.size());
    }

    /**
     * Verifies that additions update memory and disk and use singular and plural count messages.
     */
    @Test
    void getResponse_addCommands_tasksStoredAndCountMessagesReturned() throws IOException {
        TaskList taskList = new TaskList();
        Bobby bobby = new Bobby(taskList, FIXED_CLOCK);

        String firstResponse = bobby.getResponse("todo read book");
        String secondResponse = bobby.getResponse("todo write essay");

        assertEquals("     It is done. By thy command, I have inscribed this duty upon the royal register:\n"
                + "       [T][ ] read book\n"
                + "     One duty now standeth upon the register.", firstResponse);
        assertEquals("     It is done. By thy command, I have inscribed this duty upon the royal register:\n"
                + "       [T][ ] write essay\n"
                + "     There now stand 2 duties upon the register.", secondResponse);
        assertEquals(2, taskList.size());
        assertEquals(List.of(
                "T | 0 | read book | -",
                "T | 0 | write essay | -"), Files.readAllLines(SAVE_FILE));
    }

    /**
     * Verifies that mark and unmark commands change state and save the fixed completion time.
     */
    @Test
    void getResponse_markAndUnmarkCommands_statusChangedAndSaved() throws IOException {
        Todo task = new Todo("read book");
        TaskList taskList = new TaskList(List.of(task));
        Bobby bobby = new Bobby(taskList, FIXED_CLOCK);

        String markResponse = bobby.getResponse("mark 1");
        String repeatedMarkResponse = bobby.getResponse("mark 1");

        assertEquals("     Most excellent. I have proclaimed this duty duly accomplished:\n"
                + "       [T][X] read book", markResponse);
        assertEquals(markResponse, repeatedMarkResponse);
        assertTrue(task.isDone());
        assertEquals(LocalDateTime.of(2026, 9, 10, 12, 0),
                task.getCompletionDateTime().orElseThrow());
        assertEquals("T | 1 | read book | 2026-09-10T12:00:00",
                Files.readString(SAVE_FILE).strip());

        String unmarkResponse = bobby.getResponse("unmark 1");
        String repeatedUnmarkResponse = bobby.getResponse("unmark 1");

        assertEquals("     As thou commandest. I have restored this duty to the ranks of unfinished business:\n"
                + "       [T][ ] read book", unmarkResponse);
        assertEquals(unmarkResponse, repeatedUnmarkResponse);
        assertFalse(task.isDone());
        assertEquals("T | 0 | read book | -", Files.readString(SAVE_FILE).strip());
    }

    /**
     * Verifies that deletions remove the selected tasks and report every remaining-count form.
     */
    @Test
    void getResponse_deleteCommands_tasksRemovedAndCountMessagesReturned() {
        TaskList taskList = new TaskList(List.of(
                new Todo("first task"),
                new Todo("second task"),
                new Todo("third task")));
        Bobby bobby = new Bobby(taskList, FIXED_CLOCK);

        String pluralResponse = bobby.getResponse("delete 1");
        String singularResponse = bobby.getResponse("delete 1");
        String emptyResponse = bobby.getResponse("delete 1");

        assertTrue(pluralResponse.endsWith("There now remain 2 duties upon the register."));
        assertTrue(singularResponse.endsWith("One duty now remaineth upon the register."));
        assertTrue(emptyResponse.endsWith("The register now standeth empty."));
        assertTrue(taskList.isEmpty());
    }

    /**
     * Verifies that the default constructor loads valid saved tasks.
     */
    @Test
    void constructor_validSaveFile_savedTasksLoaded() throws IOException {
        Files.createDirectories(SAVE_FILE.getParent());
        Files.writeString(SAVE_FILE, "T | 1 | read book | 2026-09-10T12:00:00");

        Bobby bobby = new Bobby();

        assertFalse(bobby.hasLoadingError());
        assertEquals("Behold, the full register of thy appointed duties:\n"
                + "     1.[T][X] read book", bobby.getResponse("list"));
    }

    /**
     * Verifies that the default constructor falls back to an empty list for malformed saved data.
     */
    @Test
    void constructor_invalidSaveFile_emptyTaskListUsed() throws IOException {
        Files.createDirectories(SAVE_FILE.getParent());
        Files.writeString(SAVE_FILE, "invalid task data");

        Bobby bobby = new Bobby();

        assertTrue(bobby.hasLoadingError());
        assertEquals("Behold, the full register of thy appointed duties:\n"
                        + "The royal register standeth presently unburdened; no duty hath yet been inscribed.",
                bobby.getResponse("list"));
    }

    /**
     * Verifies that package-level constructors reject missing collaborators.
     */
    @Test
    void constructor_missingCollaborator_assertionErrorThrown() {
        assertThrows(AssertionError.class, () -> new Bobby(null));
        assertThrows(AssertionError.class, () -> new Bobby(new TaskList(), null));
    }

    /**
     * Verifies that the exit command receives Bobby's farewell response.
     */
    @Test
    void getResponse_byeCommand_farewellResponseReturned() {
        Bobby bobby = new Bobby(new TaskList());

        CommandResult result = bobby.getCommandResult("bye");

        assertEquals("     I humbly take my leave. May good fortune attend thee until next we meet.",
                result.getMessage());
        assertFalse(result.isError());
    }

    /**
     * Verifies that only the exit command requests application termination.
     */
    @Test
    void isExitCommand_variedCommands_expectedExitDecisionsReturned() {
        Bobby bobby = new Bobby(new TaskList());

        assertTrue(bobby.isExitCommand("  ByE  "));
        assertFalse(bobby.isExitCommand("bye now"));
        assertFalse(bobby.isExitCommand("list"));
    }
}
