package nori;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class NoriTest {
    @TempDir
    private Path tempDirectory;

    @Test
    void getResponse_addAndListCommands_returnsTaskDetails() {
        Nori nori = new Nori(tempDirectory.resolve("nori.txt").toString());

        assertTrue(nori.getResponse("todo read book").contains("[T][ ] read book"));
        assertTrue(nori.getResponse("list").contains("1. [T][ ] read book"));
    }

    @Test
    void getResponse_invalidCommand_returnsExplanation() {
        Nori nori = new Nori(tempDirectory.resolve("nori.txt").toString());

        assertTrue(nori.getResponse("dance").contains("Try help"));
    }

    @Test
    void getResponse_helpCommand_returnsCommandUsage() {
        Nori nori = new Nori(tempDirectory.resolve("nori.txt").toString());

        String response = nori.getResponse("help");

        assertTrue(response.contains("todo DESCRIPTION"));
        assertTrue(response.contains("deadline DESCRIPTION /by DATE"));
        assertTrue(response.contains("event DESCRIPTION /from START /to END"));
    }

    @Test
    void getResponse_byeCommand_setsExitState() {
        Nori nori = new Nori(tempDirectory.resolve("nori.txt").toString());

        assertEquals("All tucked away. Take care, and see you next time!", nori.getResponse("bye"));
        assertTrue(nori.shouldExit());
    }

    @Test
    void getResponse_blankInput_returnsHelpfulError() {
        Nori nori = new Nori(tempDirectory.resolve("nori.txt").toString());

        String response = nori.getResponse("   ");

        assertTrue(response.contains("Type help"));
        assertTrue(nori.wasLastResponseError());
    }

    @Test
    void getResponse_validCommandAfterError_clearsErrorState() {
        Nori nori = new Nori(tempDirectory.resolve("nori.txt").toString());
        nori.getResponse("unknown");

        nori.getResponse("list");

        assertFalse(nori.wasLastResponseError());
    }

    @Test
    void getResponse_taskWithStorageSeparator_rejectsTask() {
        Nori nori = new Nori(tempDirectory.resolve("nori.txt").toString());

        String response = nori.getResponse("todo buy fruit | vegetables");

        assertTrue(response.contains("avoid using ' | '"));
        assertEquals("Your task list is empty.", nori.getResponse("list"));
    }

    @Test
    void getResponse_deadlineWithInvalidDate_returnsExpectedFormat() {
        Nori nori = new Nori(tempDirectory.resolve("nori.txt").toString());

        String response = nori.getResponse("deadline submit report /by tomorrow");

        assertTrue(response.contains("yyyy-MM-dd"));
        assertTrue(nori.wasLastResponseError());
    }

    @Test
    void getResponse_eventWithoutEnd_returnsUsage() {
        Nori nori = new Nori(tempDirectory.resolve("nori.txt").toString());

        String response = nori.getResponse("event project meeting /from 2pm");

        assertTrue(response.contains("event DESCRIPTION /from START /to END"));
    }

    @Test
    void getResponse_markUnmarkAndDelete_updatesTaskList() {
        Nori nori = new Nori(tempDirectory.resolve("nori.txt").toString());
        nori.getResponse("todo read book");

        assertTrue(nori.getResponse("mark 1").contains("[T][X] read book"));
        assertTrue(nori.getResponse("unmark 1").contains("[T][ ] read book"));
        assertTrue(nori.getResponse("delete 1").contains("Now you have 0 tasks"));
        assertEquals("Your task list is empty.", nori.getResponse("list"));
    }

    @Test
    void getResponse_invalidTaskNumbers_returnSpecificErrors() {
        Nori nori = new Nori(tempDirectory.resolve("nori.txt").toString());

        assertTrue(nori.getResponse("mark").contains("provide a task number"));
        assertTrue(nori.getResponse("mark first").contains("whole number"));
        assertTrue(nori.getResponse("mark 1").contains("not in the list"));
    }

    @Test
    void getResponse_addTask_persistsForNextSession() {
        Path saveFile = tempDirectory.resolve("nori.txt");
        Nori firstSession = new Nori(saveFile.toString());
        firstSession.getResponse("deadline submit iP /by 2026-09-18");

        Nori secondSession = new Nori(saveFile.toString());

        assertTrue(secondSession.getResponse("list").contains("submit iP (by: Sep 18 2026)"));
    }

    @Test
    void constructor_corruptSaveFile_warnsUserAndStartsEmpty() throws IOException {
        Path saveFile = tempDirectory.resolve("nori.txt");
        Files.writeString(saveFile, "not valid task data");

        Nori nori = new Nori(saveFile.toString());

        assertTrue(nori.getWelcomeMessage().contains("couldn't read a task"));
        assertEquals("Your task list is empty.", nori.getResponse("list"));
        assertEquals("not valid task data", Files.readString(saveFile));
    }

    @Test
    void getResponse_unwritableStorage_returnsErrorInsteadOfCrashing() throws IOException {
        Path directoryUsedAsFile = Files.createDirectory(tempDirectory.resolve("save-directory"));
        Nori nori = new Nori(directoryUsedAsFile.toString());

        String response = nori.getResponse("todo read book");

        assertTrue(response.contains("couldn't save"));
        assertTrue(nori.wasLastResponseError());
    }
}
