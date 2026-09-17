package glennon;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests GUI command responses, session state, and persistence boundaries.
 */
class GlennonTest {
    @TempDir
    private Path directory;

    private Glennon createGlennon() {
        return new Glennon(directory.resolve("missions.txt").toString());
    }

    @Test
    void normalizeLineEndings_mixedLineEndings_returnsLineFeeds() {
        assertEquals("first\nsecond\nthird", Glennon.normalizeLineEndings("first\r\nsecond\rthird"));
    }

    @Test
    void getResponse_missionLifecycle_preservesStateAndFormatting() {
        Glennon glennon = createGlennon();
        assertEquals("Hey there! Glennon online.\nWhat's the mission?", glennon.getWelcome());
        assertEquals("Mission added: [T][ ] read book\nMission log now has 1 mission.",
                glennon.getResponse("todo read book"));
        assertEquals("Mission marked complete:\n  [T][X] read book", glennon.getResponse("mark 1"));
        assertEquals("Mission log:\n1. [T][X] read book", glennon.getResponse("list"));
        assertEquals("Mission marked incomplete:\n  [T][ ] read book", glennon.getResponse("unmark 1"));
        assertEquals("Mission removed:\n  [T][ ] read book\nMission log now has 0 missions.",
                glennon.getResponse("delete 1"));
        assertEquals("Mission log:", glennon.getResponse("list"));
    }

    @Test
    void getResponse_scheduledMissions_supportsFindAndDateFilters() {
        Glennon glennon = createGlennon();
        glennon.getResponse("deadline report /by 2/12/2019 1800");
        glennon.getResponse("event workshop /from 2/12/2019 /to 3/12/2019");
        assertEquals("Matching missions located:\n1. [D][ ] report (by: Dec 2 2019, 6:00 PM)",
                glennon.getResponse("find report"));
        assertTrue(glennon.getResponse("on 3/12/2019").contains("workshop"));
        assertFalse(glennon.getResponse("on 3/12/2019").contains("report"));
    }

    @Test
    void getResponse_invalidInputs_reportsErrorsAndPreservesMissions() {
        Glennon glennon = createGlennon();
        glennon.getResponse("todo keep me");
        String before = glennon.getResponse("list");
        for (String input : new String[] {"", "todo", "delete 8", "mark nope", "find", "on 31/2/2026"}) {
            assertTrue(glennon.getResponse(input).startsWith("Mission control alert!\n"));
            assertEquals(before, glennon.getResponse("list"));
        }
    }

    @Test
    void getResponse_newSession_restoresSavedMissions() {
        Glennon original = createGlennon();
        original.getResponse("todo saved mission");
        original.getResponse("mark 1");
        Glennon restored = createGlennon();
        assertEquals("Mission log:\n1. [T][X] saved mission", restored.getResponse("list"));
        restored.getWelcome();
        assertEquals("Mission log:\n1. [T][X] saved mission", restored.getResponse("list"));
    }

    @Test
    void getResponse_exit_preventsFurtherMutations() {
        Glennon glennon = createGlennon();
        assertFalse(glennon.hasExited());
        String goodbye = "Signing off. Catch you on the next mission!";
        assertEquals(goodbye, glennon.getResponse("bye"));
        assertTrue(glennon.hasExited());
        assertEquals(goodbye, glennon.getResponse("todo too late"));
        assertEquals("Mission log:", createGlennon().getResponse("list"));
    }

    @Test
    void getWelcome_corruptStorage_blocksCommandsWithoutOverwritingFile() throws IOException {
        Path data = directory.resolve("missions.txt");
        Files.writeString(data, "corrupt data");
        Glennon glennon = createGlennon();
        assertEquals("Mission control alert!\nMission data is corrupted at line 1.", glennon.getWelcome());
        assertTrue(glennon.hasStartupError());
        assertEquals(glennon.getWelcome(), glennon.getResponse("todo cannot overwrite"));
        assertEquals("corrupt data", Files.readString(data));
    }

    @Test
    void getResponse_saveFailure_returnsHelpfulError() throws IOException {
        Path parent = directory.resolve("not-a-directory");
        Glennon glennon = new Glennon(parent.resolve("missions.txt").toString());
        glennon.getWelcome();
        Files.writeString(parent, "occupied");
        assertEquals("Mission control alert!\nGlennon could not save the mission data.",
                glennon.getResponse("todo blocked"));
        assertEquals("Mission log:", glennon.getResponse("list"));
    }

    @Test
    void getCommandResponse_validMission_returnsSuccessfulOutput() {
        Glennon glennon = createGlennon();

        CommandResponse response = glennon.getCommandResponse("todo typed response");

        assertFalse(response.isError());
        assertEquals("Mission added: [T][ ] typed response\nMission log now has 1 mission.", response.text());
        assertEquals("Mission log:\n1. [T][ ] typed response", glennon.getResponse("list"));
    }

    @Test
    void getCommandResponse_invalidInput_returnsErrorStatus() {
        Glennon glennon = createGlennon();

        CommandResponse response = glennon.getCommandResponse("todo");

        assertTrue(response.isError());
        assertEquals("Mission control alert!\nPlease enter a mission after todo.", response.text());
        assertEquals("Mission log:", glennon.getResponse("list"));
    }

    @Test
    void getCommandResponse_successAfterError_clearsErrorStatusAndPreservesMissions() {
        Glennon glennon = createGlennon();
        glennon.getCommandResponse("todo preserve during recovery");

        assertTrue(glennon.getCommandResponse("delete 9").isError());
        CommandResponse response = glennon.getCommandResponse("list");

        assertFalse(response.isError());
        assertEquals("Mission log:\n1. [T][ ] preserve during recovery", response.text());
    }

    @Test
    void getCommandResponse_errorWordsInMission_remainsSuccessful() {
        Glennon glennon = createGlennon();

        CommandResponse added = glennon.getCommandResponse("todo Mission control alert!");
        CommandResponse listed = glennon.getCommandResponse("list");

        assertFalse(added.isError());
        assertFalse(listed.isError());
        assertEquals("Mission log:\n1. [T][ ] Mission control alert!", listed.text());
    }

    @Test
    void getCommandResponse_corruptStorage_returnsErrorAndPreservesFile() throws IOException {
        Path data = directory.resolve("missions.txt");
        Files.writeString(data, "unreadable mission");
        Glennon glennon = createGlennon();

        CommandResponse response = glennon.getCommandResponse("todo cannot replace data");

        assertTrue(response.isError());
        assertTrue(glennon.hasStartupError());
        assertEquals(glennon.getWelcome(), response.text());
        assertEquals("unreadable mission", Files.readString(data));
    }

    @Test
    void getCommandResponse_saveFailure_returnsErrorStatus() throws IOException {
        Path parent = directory.resolve("blocked-parent");
        Glennon glennon = new Glennon(parent.resolve("missions.txt").toString());
        glennon.getWelcome();
        Files.writeString(parent, "occupied");

        CommandResponse response = glennon.getCommandResponse("todo cannot save");

        assertTrue(response.isError());
        assertEquals("Mission control alert!\nGlennon could not save the mission data.", response.text());
        assertEquals("Mission log:", glennon.getResponse("list"));
    }

    @Test
    void getWelcome_invalidStoredDate_blocksEveryAttemptAndPreservesFile() throws IOException {
        Path data = directory.resolve("missions.txt");
        String invalidDate = Base64.getEncoder().encodeToString(
                "2026-02-30T12:00".getBytes(StandardCharsets.UTF_8));
        String contents = "D\t0\tdGFzaw==\t" + invalidDate + "\n";
        Files.writeString(data, contents);
        Glennon glennon = createGlennon();

        String error = "Mission control alert!\nMission data is corrupted at line 1.";
        assertEquals(error, glennon.getWelcome());
        assertTrue(glennon.hasStartupError());
        assertEquals(error, glennon.getResponse("todo cannot overwrite malformed date"));
        assertEquals(error, glennon.getResponse("list"));
        assertEquals(contents, Files.readString(data));
    }

    @Test
    void getResponse_failedDeletionThenRetry_removesOnlyTheRequestedMission() throws IOException {
        Path data = directory.resolve("missions.txt");
        Path backup = directory.resolve("original.txt");
        Glennon glennon = createGlennon();
        glennon.getResponse("todo first saved mission");
        glennon.getResponse("todo second saved mission");
        String before = glennon.getResponse("list");
        Files.move(data, backup);
        Files.createDirectory(data);

        assertTrue(glennon.getCommandResponse("delete 1").isError());
        assertEquals(before, glennon.getResponse("list"));
        Files.delete(data);
        Files.move(backup, data);
        assertFalse(glennon.getCommandResponse("delete 1").isError());
        assertEquals("Mission log:\n1. [T][ ] second saved mission", createGlennon().getResponse("list"));
    }

    @Test
    void getResponse_duplicateAfterRestart_preservesFileAndCompletionState() throws IOException {
        Path data = directory.resolve("missions.txt");
        Glennon glennon = createGlennon();
        glennon.getResponse("deadline existing /by 2/12/2019");
        glennon.getResponse("mark 1");
        String contents = Files.readString(data);
        Glennon restored = createGlennon();

        assertEquals("Mission control alert!\nThat mission already exists in the log.",
                restored.getResponse("deadline existing /by 2/12/2019 2359"));
        assertEquals("Mission log:\n1. [D][X] existing (by: Dec 2 2019, 11:59 PM)",
                restored.getResponse("list"));
        assertEquals(contents, Files.readString(data));
    }

    @Test
    void getWelcome_duplicateSavedMissions_blocksChangesAndPreservesFile() throws IOException {
        Path data = directory.resolve("missions.txt");
        String contents = "T\t0\tdGFzaw==\nT\t1\tdGFzaw==\n";
        Files.writeString(data, contents);
        Glennon glennon = createGlennon();

        String error = "Mission control alert!\nMission data is corrupted at line 2: duplicate mission.";
        assertEquals(error, glennon.getWelcome());
        assertTrue(glennon.hasStartupError());
        assertEquals(error, glennon.getResponse("todo cannot overwrite duplicate data"));
        assertEquals(contents, Files.readString(data));
    }

    @Test
    void getCommandResponse_exit_returnsSuccessfulGoodbyeAndBlocksCommands() {
        Glennon glennon = createGlennon();

        CommandResponse goodbye = glennon.getCommandResponse("bye");
        CommandResponse afterExit = glennon.getCommandResponse("todo too late for typed response");

        assertFalse(goodbye.isError());
        assertEquals("Signing off. Catch you on the next mission!", goodbye.text());
        assertEquals(goodbye, afterExit);
        assertTrue(glennon.hasExited());
        assertEquals("Mission log:", createGlennon().getResponse("list"));
    }
}
