package sace;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests the interface-independent command responses used by the GUI.
 */
class SaceTest {
    @TempDir
    Path tempDir;

    @Test
    void getResponse_help_returnsBattleManualWithoutEndingSession() {
        Sace sace = new Sace(tempDir.resolve("sace.txt"));

        String response = sace.getResponse("help");

        assertTrue(response.startsWith("THE BATTLE MANUAL"));
        assertTrue(response.contains("deadline DESCRIPTION /by yyyy-MM-dd"));
        assertTrue(response.contains("event DESCRIPTION /from START /to END"));
        assertFalse(sace.isExitRequested());
    }

    @Test
    void getResponse_addThenList_returnsSavedTask() {
        Sace sace = new Sace(tempDir.resolve("sace.txt"));

        String addResponse = sace.getResponse("todo prepare slides");
        String listResponse = sace.getResponse("list");

        assertTrue(addResponse.contains("prepare slides"));
        assertTrue(listResponse.contains("1. [T][ ] prepare slides"));
    }

    @Test
    void getResponse_addAllTaskTypes_persistsTasksForNextSession() {
        Path dataFile = tempDir.resolve("sace.txt");
        Sace firstSession = new Sace(dataFile);

        firstSession.getResponse("todo read notes");
        firstSession.getResponse("deadline submit report /by 2026-09-30");
        firstSession.getResponse("event tutorial /from Monday 2pm /to Monday 4pm");
        Sace secondSession = new Sace(dataFile);
        String listResponse = secondSession.getResponse("list");

        assertTrue(listResponse.contains("1. [T][ ] read notes"));
        assertTrue(listResponse.contains("2. [D][ ] submit report (by: Sep 30 2026)"));
        assertTrue(listResponse.contains(
                "3. [E][ ] tutorial (from: Monday 2pm to: Monday 4pm)"));
    }

    @Test
    void getResponse_markUnmarkAndDelete_updatesTaskList() {
        Sace sace = new Sace(tempDir.resolve("sace.txt"));
        sace.getResponse("todo first quest");
        sace.getResponse("todo second quest");

        String markResponse = sace.getResponse("mark 2");
        String unmarkResponse = sace.getResponse("unmark 2");
        String deleteResponse = sace.getResponse("delete 1");
        String listResponse = sace.getResponse("list");

        assertTrue(markResponse.contains("[T][X] second quest"));
        assertTrue(unmarkResponse.contains("[T][ ] second quest"));
        assertTrue(deleteResponse.contains("[T][ ] first quest"));
        assertTrue(listResponse.contains("1. [T][ ] second quest"));
        assertFalse(listResponse.contains("first quest"));
    }

    @Test
    void getResponse_find_returnsOnlyDescriptionMatches() {
        Sace sace = new Sace(tempDir.resolve("sace.txt"));
        sace.getResponse("todo Read Book");
        sace.getResponse("todo prepare slides");

        String matchingResponse = sace.getResponse("find book");
        String noMatchResponse = sace.getResponse("find tutorial");

        assertTrue(matchingResponse.contains("[T][ ] Read Book"));
        assertFalse(matchingResponse.contains("prepare slides"));
        assertTrue(noMatchResponse.contains("No quests await you here."));
    }

    @Test
    void getResponse_invalidCommand_returnsFriendlyError() {
        Sace sace = new Sace(tempDir.resolve("sace.txt"));

        String response = sace.getResponse("dance");

        assertTrue(response.startsWith("The battle plan needs correction:"));
        assertTrue(sace.didLastCommandFail());
        assertFalse(sace.isExitRequested());
    }

    @Test
    void getResponse_nullOrWhitespace_returnsBlankCommandError() {
        Sace sace = new Sace(tempDir.resolve("sace.txt"));

        String nullResponse = sace.getResponse(null);
        String whitespaceResponse = sace.getResponse("   \t  ");

        assertTrue(nullResponse.contains("Your command scroll is blank"));
        assertTrue(whitespaceResponse.contains("Your command scroll is blank"));
        assertTrue(sace.didLastCommandFail());
    }

    @Test
    void getResponse_validCommandAfterError_resetsErrorState() {
        Sace sace = new Sace(tempDir.resolve("sace.txt"));
        sace.getResponse("dance");

        String response = sace.getResponse("list");

        assertTrue(response.contains("The moonlit ledger records these quests"));
        assertFalse(sace.didLastCommandFail());
    }

    @Test
    void getResponse_emptyTaskOperation_returnsFriendlyError() {
        Sace sace = new Sace(tempDir.resolve("sace.txt"));

        String response = sace.getResponse("mark 1");

        assertTrue(response.contains("Your quest log is empty"));
        assertTrue(sace.didLastCommandFail());
    }

    @Test
    void getResponse_bye_marksSessionForExit() {
        Sace sace = new Sace(tempDir.resolve("sace.txt"));

        String response = sace.getResponse("bye");

        assertTrue(response.contains("The battle rests for now"));
        assertTrue(sace.isExitRequested());
    }

    @Test
    void getResponse_commandAfterBye_startsNewSessionState() {
        Sace sace = new Sace(tempDir.resolve("sace.txt"));
        sace.getResponse("bye");

        sace.getResponse("list");

        assertFalse(sace.isExitRequested());
    }

    @Test
    void getWelcomeMessage_corruptedArchive_reportsWarningAndStartsEmpty()
            throws IOException {
        Path dataFile = tempDir.resolve("sace.txt");
        Files.writeString(
                dataFile,
                "T | invalid | broken task",
                StandardCharsets.UTF_8);
        Sace sace = new Sace(dataFile);

        String welcomeMessage = sace.getWelcomeMessage();
        String listResponse = sace.getResponse("list");

        assertTrue(welcomeMessage.contains("The royal archive is in disarray"));
        assertTrue(welcomeMessage.contains("damaged at line 1"));
        assertTrue(listResponse.contains("No quests await you here."));
    }

    @Test
    void getResponse_saveFailure_rollsBackAddedTask() throws IOException {
        Path directoryUsedAsFile = Files.createDirectory(tempDir.resolve("archive"));
        Sace sace = new Sace(directoryUsedAsFile);

        String addResponse = sace.getResponse("todo prepare slides");
        String listResponse = sace.getResponse("list");

        assertTrue(addResponse.contains("could not save quests"));
        assertTrue(listResponse.contains("No quests await you here."));
    }

    @Test
    void getResponse_saveFailures_rollBackMarkUnmarkAndDelete() throws IOException {
        Sace markSace = loadThenBlockDataFile(
                "mark", "T | 0 | unfinished quest");
        Sace unmarkSace = loadThenBlockDataFile(
                "unmark", "T | 1 | completed quest");
        Sace deleteSace = loadThenBlockDataFile(
                "delete", "T | 0 | first quest\nT | 0 | second quest");

        String markError = markSace.getResponse("mark 1");
        String unmarkError = unmarkSace.getResponse("unmark 1");
        String deleteError = deleteSace.getResponse("delete 1");

        assertTrue(markError.contains("could not save quests"));
        assertTrue(markSace.getResponse("list").contains("[T][ ] unfinished quest"));
        assertTrue(unmarkError.contains("could not save quests"));
        assertTrue(unmarkSace.getResponse("list").contains("[T][X] completed quest"));
        assertTrue(deleteError.contains("could not save quests"));
        assertTrue(deleteSace.getResponse("list").contains("1. [T][ ] first quest"));
        assertTrue(deleteSace.getResponse("list").contains("2. [T][ ] second quest"));
    }

    /**
     * Loads a Sace instance, then replaces its data file with a directory so saves fail.
     */
    private Sace loadThenBlockDataFile(String folderName, String storedTasks)
            throws IOException {
        Path folder = Files.createDirectory(tempDir.resolve(folderName));
        Path dataFile = folder.resolve("sace.txt");
        Files.writeString(dataFile, storedTasks, StandardCharsets.UTF_8);
        Sace sace = new Sace(dataFile);
        Files.delete(dataFile);
        Files.createDirectory(dataFile);
        return sace;
    }
}
