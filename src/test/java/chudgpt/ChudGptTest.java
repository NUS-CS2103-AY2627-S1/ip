package chudgpt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests responses produced for GUI interactions. */
public class ChudGptTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void getResponse_validCommand_returnsCommandResponse() {
        ChudGpt chudGpt = createChudGpt();

        String response = chudGpt.getResponse("hi");

        assertEquals("Hi! I'm ChudGPT. How can I help you?", response);
    }

    @Test
    public void getResponse_invalidCommand_returnsFormattedError() {
        ChudGpt chudGpt = createChudGpt();

        String response = chudGpt.getResponse("unknown");

        assertEquals("OOPS!!! I've run into an error :( I'm such a chud...\nDetails:\n  Invalid command.",
                response);
    }

    @Test
    public void getResponse_emptyTodoAndUnexpectedArguments_returnsUsageErrors() {
        ChudGpt chudGpt = createChudGpt();

        assertTrue(chudGpt.getResponse("todo").endsWith("Task description cannot be empty."));
        assertTrue(chudGpt.getResponse("hi there").endsWith("Usage: hi."));
        assertTrue(chudGpt.getResponse("delete 1 2").endsWith("Usage: delete <task number>."));
        assertEquals("You have no tasks in your list! Try adding some", chudGpt.getResponse("list"));
    }

    @Test
    public void getResponse_duplicateTask_rejectsNormalizedDuplicate() {
        ChudGpt chudGpt = createChudGpt();
        chudGpt.getResponse("todo read   book /priority low");

        assertTrue(chudGpt.getResponse("todo READ BOOK /priority high")
                .endsWith("This task duplicates task 1."));
        assertTrue(chudGpt.getResponse("deadline read book /by 2026-10-01").startsWith("Got it."));
        assertTrue(chudGpt.getResponse("list").contains("2. [D]"));
    }

    @Test
    public void getResponseDetails_normalErrorAndTaskAddedCommands_returnsPresentationType() {
        ChudGpt chudGpt = createChudGpt();

        ChudGpt.Response validResponse = chudGpt.getResponseDetails("hi");
        ChudGpt.Response errorResponse = chudGpt.getResponseDetails("unknown");
        ChudGpt.Response taskAddedResponse = chudGpt.getResponseDetails("todo read book");

        assertEquals("Hi! I'm ChudGPT. How can I help you?", validResponse.text());
        assertEquals(ChudGpt.ResponseType.NORMAL, validResponse.responseType());
        assertEquals(ChudGpt.ResponseType.ERROR, errorResponse.responseType());
        assertTrue(errorResponse.text().endsWith("Invalid command."));
        assertEquals(ChudGpt.ResponseType.TASK_ADDED, taskAddedResponse.responseType());
    }

    @Test
    public void getResponse_addThenList_returnsUpdatedTaskList() {
        ChudGpt chudGpt = createChudGpt();

        chudGpt.getResponse("todo read book");

        assertEquals("Here are the tasks in your list:\n1. [T][ ][P:NONE   ] read book",
                chudGpt.getResponse("list"));
    }

    @Test
    public void getWelcomeMessage_newApplication_returnsWelcomeMessage() {
        String welcomeMessage = createChudGpt().getWelcomeMessage();

        assertTrue(welcomeMessage.contains("Hello! I'm ChudGPT."));
        assertTrue(welcomeMessage.contains("What can I do for you?"));
    }

    @Test
    public void getGuiWelcomeMessage_newApplication_omitsConsoleDecoration() {
        String welcomeMessage = createChudGpt().getGuiWelcomeMessage();

        assertEquals("Hello! I'm ChudGPT.\nWhat can I do for you?", welcomeMessage);
        assertFalse(welcomeMessage.contains("____"));
    }

    @Test
    public void getResponse_exitCommand_applicationHasExited() {
        ChudGpt chudGpt = createChudGpt();

        chudGpt.getResponse("bye");

        assertTrue(chudGpt.hasExited());
    }

    @Test
    public void getResponse_saveFailure_reportsErrorAndDoesNotExit() throws Exception {
        Path saveTarget = temporaryDirectory.resolve("tasks");
        Files.createDirectory(saveTarget);
        ChudGpt chudGpt = new ChudGpt(saveTarget.toString());

        String addResponse = chudGpt.getResponse("todo read book");
        String exitResponse = chudGpt.getResponse("bye");

        assertTrue(addResponse.contains("Could not save tasks to "));
        assertTrue(addResponse.endsWith("Your changes are still available in this session."));
        assertEquals("Here are the tasks in your list:\n1. [T][ ][P:NONE   ] read book",
                chudGpt.getResponse("list"));
        assertTrue(exitResponse.contains("Could not save tasks to "));
        assertFalse(chudGpt.hasExited());
    }

    @Test
    public void getGuiWelcomeMessage_malformedSaveFile_reportsRecoveryWarning() throws Exception {
        Files.writeString(temporaryDirectory.resolve("tasks.txt"),
                "T | 0 | NONE | valid\nD | 0 | HIGH | invalid | 2026-02-30");

        String welcomeMessage = createChudGpt().getGuiWelcomeMessage();

        assertTrue(welcomeMessage.contains("Some saved tasks could not be loaded"));
        assertTrue(welcomeMessage.contains("Line 2 was skipped"));
        assertTrue(welcomeMessage.contains("A backup will be created"));
    }

    @Test
    public void getGuiWelcomeMessage_savePathIsDirectory_reportsLoadError() throws Exception {
        Path saveTarget = temporaryDirectory.resolve("tasks");
        Files.createDirectory(saveTarget);

        String welcomeMessage = new ChudGpt(saveTarget.toString()).getGuiWelcomeMessage();

        assertTrue(welcomeMessage.contains("I couldn't retrieve the save file"));
        assertTrue(welcomeMessage.contains("The task data path is not a regular file"));
    }

    @Test
    public void getResponse_afterExit_exitStatusRemainsTrue() {
        ChudGpt chudGpt = createChudGpt();

        chudGpt.getResponse("bye");
        chudGpt.getResponse("unknown");

        assertTrue(chudGpt.hasExited());
    }

    @Test
    public void getResponse_sortedViewThenCommands_targetCurrentUnderlyingNumbers() {
        ChudGpt chudGpt = createChudGpt();
        chudGpt.getResponse("todo first");
        chudGpt.getResponse("todo second /priority extreme");

        assertEquals("Here are the tasks in your list:\n2. [T][ ][P:EXTREME] second\n1. [T][ ][P:NONE   ] first",
                chudGpt.getResponse("  LIST /SORT   PRIORITY  "));
        chudGpt.getResponse("mark 2");
        assertEquals("Got it. I've set this task's priority to HIGH:\n  [T][X][P:HIGH   ] second",
                chudGpt.getResponse("priority 2 high"));
        chudGpt.getResponse("delete 1");

        assertEquals("Here are the tasks in your list:\n1. [T][X][P:HIGH   ] second", chudGpt.getResponse("list"));
        assertEquals(chudGpt.getResponse("list"), createChudGpt().getResponse("list"));
    }

    @Test
    public void getResponse_invalidPriorityCommands_preserveTaskListAndFile() throws Exception {
        ChudGpt chudGpt = createChudGpt();
        chudGpt.getResponse("todo book /priority low");
        String expected = chudGpt.getResponse("list");
        Path file = temporaryDirectory.resolve("tasks.txt");
        String saved = Files.readString(file);

        assertEquals("OOPS!!! I've run into an error :( I'm such a chud...\nDetails:\n  "
                + "The /priority argument requires a priority value.",
                chudGpt.getResponse("todo extra /priority"));
        for (String command : new String[]{"todo extra /priority 1", "priority", "priority 1 urgent",
            "priority 0 high", "priority -1 high", "priority 2 high", "priority one high"}) {
            assertTrue(chudGpt.getResponse(command).startsWith("OOPS!!!"));
        }

        assertEquals(expected, chudGpt.getResponse("list"));
        assertEquals(saved, Files.readString(file));
    }

    @Test
    public void getResponse_invalidListArguments_reportUsage() {
        ChudGpt chudGpt = createChudGpt();
        chudGpt.getResponse("todo first");
        chudGpt.getResponse("todo second /priority high");
        for (String command : new String[]{"list extra", "list /sort", "list /sort date",
            "list /sort priority extra"}) {
            assertEquals("OOPS!!! I've run into an error :( I'm such a chud...\nDetails:\n  "
                    + "Usage: list or list /sort priority.", chudGpt.getResponse(command));
        }
    }

    /** Returns a ChudGPT instance backed by a temporary save file. */
    private ChudGpt createChudGpt() {
        return new ChudGpt(temporaryDirectory.resolve("tasks.txt").toString());
    }
}
