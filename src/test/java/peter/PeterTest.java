package peter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class PeterTest {
    @TempDir
    private Path tempDir;

    @Test
    public void getResponseResult_unknownCommand_marksResponseAsError() {
        Peter peter = new Peter(tempDir.resolve("tasks.txt").toString());

        Peter.Response response = peter.getResponseResult("abracadabra");

        assertTrue(response.isError());
        assertTrue(response.message().contains("route"));
    }

    @Test
    public void getResponseResult_validCommand_marksResponseAsNormal() {
        Peter peter = new Peter(tempDir.resolve("tasks.txt").toString());

        Peter.Response response = peter.getResponseResult("list");

        assertFalse(response.isError());
        assertTrue(response.message().contains("map"));
    }

    @Test
    public void getWelcomeMessage_usesPeterNavigatorPersonality() {
        Peter peter = new Peter(tempDir.resolve("tasks.txt").toString());

        String welcomeMessage = peter.getWelcomeMessage();

        assertTrue(welcomeMessage.contains("Peter"));
        assertTrue(welcomeMessage.contains("chart"));
    }

    @Test
    public void getResponseResult_noArgumentCommandWithExtraText_marksResponseAsError() {
        Peter peter = new Peter(tempDir.resolve("tasks.txt").toString());

        Peter.Response response = peter.getResponseResult("list please");

        assertTrue(response.isError());
    }

    @Test
    public void getResponseResult_duplicateTask_marksSecondResponseAsError() {
        Peter peter = new Peter(tempDir.resolve("tasks.txt").toString());
        peter.getResponseResult("todo read book");

        Peter.Response response = peter.getResponseResult("todo READ BOOK");

        assertTrue(response.isError());
        assertTrue(response.message().contains("already"));
    }

    @Test
    public void getResponseResult_unwritableDestination_reportsSaveError() {
        Peter peter = new Peter(tempDir.toString());

        Peter.Response response = peter.getResponseResult("todo read book");

        assertTrue(response.isError());
        assertTrue(response.message().contains("couldn't save"));
        assertTrue(peter.getResponse("list").contains("no tasks"));
    }

    @Test
    public void getLoadingWarning_storagePathIsDirectory_returnsWarning() {
        Peter peter = new Peter(tempDir.toString());

        String warning = peter.getLoadingWarning();

        assertTrue(warning.contains("isn't a regular file"));
    }

    @Test
    public void commandWorkflow_allSupportedCommands_updateAndQueryTasks() {
        Peter peter = new Peter(tempDir.resolve("workflow.txt").toString());

        assertFalse(peter.getResponseResult("todo read book").isError());
        assertFalse(peter.getResponseResult("deadline submit report /by 2026-09-11 1800").isError());
        assertFalse(peter.getResponseResult("event meeting /from 0900 /to 1000").isError());

        String list = peter.getResponse("list");
        assertTrue(list.contains("read book"));
        assertTrue(list.contains("submit report"));
        assertTrue(list.contains("meeting"));

        assertTrue(peter.getResponse("mark 1").contains("[X]"));
        assertTrue(peter.getResponse("unmark 1").contains("[ ]"));
        assertTrue(peter.getResponse("find REPORT").contains("submit report"));
        assertTrue(peter.getResponse("find absent").contains("found no tasks"));
        assertTrue(peter.getResponse("view 2026-09-11").contains("submit report"));
        assertTrue(peter.getResponse("view 2026-09-12").contains("No tasks"));
        assertTrue(peter.getResponse("sort").contains("Course arranged"));
        assertTrue(peter.getResponse("delete 1").contains("removed"));
        assertTrue(peter.getResponse("bye").contains("Journey paused"));
    }

    @Test
    public void emptyList_listAndSort_returnFriendlyEmptyMessages() {
        Peter peter = new Peter(tempDir.resolve("empty.txt").toString());

        assertTrue(peter.getResponse("list").contains("no tasks"));
        assertTrue(peter.getResponse("sort").contains("no deadlines"));
    }

    @Test
    public void persistedTask_newPeterInstance_restoresTask() {
        Path dataFile = tempDir.resolve("persisted.txt");
        Peter firstSession = new Peter(dataFile.toString());
        firstSession.getResponse("todo read book");

        Peter secondSession = new Peter(dataFile.toString());

        assertTrue(secondSession.getResponse("list").contains("read book"));
    }

    @Test
    public void damagedStorage_validEntriesRecoveredAndWarningExposed() throws IOException {
        Path dataFile = tempDir.resolve("damaged.txt");
        Files.writeString(dataFile, "T | 0 | read book\ninvalid entry\n");

        Peter peter = new Peter(dataFile.toString());

        assertTrue(peter.getLoadingWarning().contains("skipped 1"));
        assertTrue(peter.getResponse("list").contains("read book"));
    }

    @Test
    public void invalidCommands_returnErrorsWithoutThrowing() {
        Peter peter = new Peter(tempDir.resolve("invalid.txt").toString());
        String[] invalidCommands = {
            null,
            "",
            "unknown",
            "mark 1",
            "unmark 1",
            "delete 1",
            "todo",
            "deadline missing marker",
            "event meeting /from 1000 /to 0900",
            "view 2026-02-30",
            "find",
            "sort extra",
            "bye extra"
        };

        for (String command : invalidCommands) {
            Peter.Response response = peter.getResponseResult(command);
            assertTrue(response.isError(), "Expected an error for command: " + command);
        }
    }

    @Test
    public void responseRecord_exposesSuppliedValues() {
        Peter.Response response = new Peter.Response("message", true);

        assertEquals("message", response.message());
        assertTrue(response.isError());
    }

    @Test
    public void mark_saveFailure_rollsBackCompletionState() throws IOException {
        Path dataFile = tempDir.resolve("mark.txt");
        Files.writeString(dataFile, "T | 0 | read book\n");
        Peter peter = new Peter(dataFile.toString());
        Files.delete(dataFile);
        Files.createDirectory(dataFile);

        Peter.Response response = peter.getResponseResult("mark 1");

        assertTrue(response.isError());
        assertTrue(peter.getResponse("list").contains("[T][ ] read book"));
    }

    @Test
    public void unmark_saveFailure_rollsBackCompletionState() throws IOException {
        Path dataFile = tempDir.resolve("unmark.txt");
        Files.writeString(dataFile, "T | 1 | read book\n");
        Peter peter = new Peter(dataFile.toString());
        Files.delete(dataFile);
        Files.createDirectory(dataFile);

        Peter.Response response = peter.getResponseResult("unmark 1");

        assertTrue(response.isError());
        assertTrue(peter.getResponse("list").contains("[T][X] read book"));
    }

    @Test
    public void deleteAndSort_saveFailure_preserveOriginalInMemoryList() throws IOException {
        Path dataFile = tempDir.resolve("mutations.txt");
        Files.writeString(dataFile, "D | 0 | later | 2026-09-12 1200\n"
                + "D | 0 | earlier | 2026-09-10 1200\n");
        Peter peter = new Peter(dataFile.toString());
        Files.delete(dataFile);
        Files.createDirectory(dataFile);

        assertTrue(peter.getResponseResult("delete 1").isError());
        assertTrue(peter.getResponseResult("sort").isError());

        String list = peter.getResponse("list");
        assertTrue(list.contains("1.[D][ ] later"));
        assertTrue(list.contains("2.[D][ ] earlier"));
    }
}
