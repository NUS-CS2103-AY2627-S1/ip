package lizzy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests the command-response interface shared with the JavaFX GUI.
 */
public class LizzyTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void getResponse_addThenList_stateRetainedAcrossCommands() {
        Lizzy lizzy = new Lizzy(temporaryDirectory.resolve("lizzy.txt"));

        String addResponse = lizzy.getResponse("todo test the GUI");
        String listResponse = lizzy.getResponse("list");

        assertEquals("Here comes another matter to keep track of:\n"
                + "  [T][ ] test the GUI\n"
                + "That makes 1 task awaiting your attention.", addResponse);
        assertEquals("Let us see what has found its way onto your list:\n"
                + "1.[T][ ] test the GUI", listResponse);
    }

    @Test
    void getResponse_invalidCommand_validationMessageReturned() {
        Lizzy lizzy = new Lizzy(temporaryDirectory.resolve("lizzy.txt"));

        String response = lizzy.getResponse("unknown");

        assertEquals("I'm afraid \"unknown\" is quite beyond my acquaintance.\n"
                + "You may try: todo, deadline, event, within, list, find, on, mark, unmark, delete, help, or bye.",
                response);
    }

    @Test
    void getResponse_helpCommand_formatsAndPurposesReturned() {
        Lizzy lizzy = new Lizzy(temporaryDirectory.resolve("lizzy.txt"));

        String response = lizzy.getResponse("help");

        assertTrue(response.startsWith("A brief guide, should memory prove uncooperative:"));
        assertTrue(response.contains("• todo <description> — undated task"));
        assertTrue(response.contains("find <keyword> — partially search descriptions"));
        assertTrue(response.contains("Dates use yyyy-MM-dd (for example, 2026-09-10);"
                + " times use HH:mm (for example, 14:30)."));
    }

    @Test
    void getResponseWithStatus_validCommand_responseIsNotError() {
        Lizzy lizzy = new Lizzy(temporaryDirectory.resolve("lizzy.txt"));

        Lizzy.Response response = lizzy.getResponseWithStatus("list");

        assertEquals("Your list is blissfully free of obligations.", response.text());
        assertFalse(response.isError());
        assertFalse(response.isExit());
    }

    @Test
    void getResponseWithStatus_invalidCommand_responseIsError() {
        Lizzy lizzy = new Lizzy(temporaryDirectory.resolve("lizzy.txt"));

        Lizzy.Response response = lizzy.getResponseWithStatus("dance");

        assertTrue(response.text().startsWith("I'm afraid \"dance\" is quite beyond my acquaintance."));
        assertTrue(response.isError());
        assertFalse(response.isExit());
    }

    @Test
    void getResponseWithStatus_byeCommand_responseEndsSession() {
        Lizzy lizzy = new Lizzy(temporaryDirectory.resolve("lizzy.txt"));

        Lizzy.Response response = lizzy.getResponseWithStatus("bye");

        assertTrue(response.text().startsWith("Goodbye!"));
        assertFalse(response.isError());
        assertTrue(response.isExit());
    }

    @Test
    void getStartupError_unreadableDataFile_errorAvailableAndEmptyListUsed() throws IOException {
        Path directoryPath = temporaryDirectory.resolve("tasks");
        Files.createDirectory(directoryPath);
        Lizzy lizzy = new Lizzy(directoryPath);

        assertTrue(lizzy.getStartupError().orElseThrow().contains("couldn't read your saved tasks"));
        assertEquals("Your list is blissfully free of obligations.", lizzy.getResponse("list"));
    }

    @Test
    void getResponse_duplicateTask_errorReturnedWithoutChangingTaskList() {
        Lizzy lizzy = new Lizzy(temporaryDirectory.resolve("lizzy.txt"));
        lizzy.getResponse("todo read chapter");

        Lizzy.Response duplicateResponse = lizzy.getResponseWithStatus("todo read chapter");
        String listResponse = lizzy.getResponse("list");

        assertTrue(duplicateResponse.text().contains("already keeping its place"));
        assertTrue(duplicateResponse.isError());
        assertEquals("Let us see what has found its way onto your list:\n"
                + "1.[T][ ] read chapter", listResponse);
    }

    @Test
    void getResponse_redundantStatusUpdate_errorReturnedWithoutChangingTask() {
        Lizzy lizzy = new Lizzy(temporaryDirectory.resolve("lizzy.txt"));
        lizzy.getResponse("todo read chapter");

        Lizzy.Response unmarkResponse = lizzy.getResponseWithStatus("unmark 1");
        lizzy.getResponse("mark 1");
        Lizzy.Response markResponse = lizzy.getResponseWithStatus("mark 1");

        assertTrue(unmarkResponse.text().contains("already waiting to be done"));
        assertTrue(unmarkResponse.isError());
        assertTrue(markResponse.text().contains("already settled"));
        assertTrue(markResponse.isError());
        assertTrue(lizzy.getResponse("list").contains("1.[T][X] read chapter"));
    }

    @Test
    void getResponse_nullInput_friendlyErrorReturned() {
        Lizzy lizzy = new Lizzy(temporaryDirectory.resolve("lizzy.txt"));

        Lizzy.Response response = lizzy.getResponseWithStatus(null);

        assertTrue(response.text().startsWith("Silence may be elegant"));
        assertTrue(response.isError());
    }

    @Test
    void getResponse_allCommands_responsesAndPersistentStateCorrect() {
        Path dataFilePath = temporaryDirectory.resolve("lizzy.txt");
        Lizzy lizzy = new Lizzy(dataFilePath);

        assertFalse(lizzy.getResponseWithStatus("todo read book").isError());
        assertFalse(lizzy.getResponseWithStatus("deadline submit report /by 2026-09-20").isError());
        assertFalse(lizzy.getResponseWithStatus(
                "event conference /from 2026-09-20 /to 2026-09-22").isError());
        assertFalse(lizzy.getResponseWithStatus(
                "within collect certificate /from 2026-09-21 /to 2026-09-23").isError());
        assertTrue(lizzy.getResponse("mark 2").contains("[D][X] submit report"));
        assertTrue(lizzy.getResponse("unmark 2").contains("[D][ ] submit report"));
        assertTrue(lizzy.getResponse("delete 1").contains("[T][ ] read book"));
        assertTrue(lizzy.getResponse("find report").contains("1.[D][ ] submit report"));
        assertTrue(lizzy.getResponse("on 2026-09-21").contains("2.[E][ ] conference"));
        assertTrue(lizzy.getResponse("bye").startsWith("Goodbye!"));

        Lizzy reloadedLizzy = new Lizzy(dataFilePath);
        String reloadedList = reloadedLizzy.getResponse("list");
        assertTrue(reloadedList.contains("1.[D][ ] submit report"));
        assertTrue(reloadedList.contains("2.[E][ ] conference"));
        assertTrue(reloadedList.contains("3.[W][ ] collect certificate"));
    }

    @Test
    void getResponse_saveFailure_errorReturnedAndMemoryStateRetained() throws IOException {
        Path blockingFile = temporaryDirectory.resolve("not-a-folder");
        Files.writeString(blockingFile, "block");
        Lizzy lizzy = new Lizzy(blockingFile.resolve("lizzy.txt"));

        Lizzy.Response response = lizzy.getResponseWithStatus("todo keep working");

        assertTrue(response.isError());
        assertTrue(response.text().contains("couldn't save it"));
        assertTrue(lizzy.getResponse("list").contains("1.[T][ ] keep working"));
    }
}
