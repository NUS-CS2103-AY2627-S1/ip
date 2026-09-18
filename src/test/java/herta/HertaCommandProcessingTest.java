package herta;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests GUI-style command processing and persistence classifications.
 */
class HertaCommandProcessingTest {
    @TempDir
    Path temporaryDirectory;

    /** Stores a file's presence and bytes for persistence assertions. */
    private record FileState(boolean wasPresent, byte[] contents) {
    }

    @Test
    void getResponse_leadingWhitespaceIsNormalizedBeforeParsingAndSaving() throws Exception {
        Path dataFile = temporaryDirectory.resolve("leading-space.txt");
        Herta herta = new Herta(dataFile.toString());

        HertaResponse response = herta.getResponse("  todo read book");

        assertTrue(response.getMessage().contains("[T][ ] read book"));
        assertEquals("T | 0 | read book", Files.readString(dataFile).trim());
    }

    @Test
    void getResponse_invalidNullAndControlInputReturnsSafeResponses() throws Exception {
        Path dataFile = temporaryDirectory.resolve("safe-input.txt");
        Herta herta = new Herta(dataFile.toString());

        HertaResponse nullResponse = herta.getResponse(null);
        HertaResponse controlResponse = herta.getResponse("todo bad\u0000text");
        HertaResponse nextResponse = herta.getResponse("todo still works");

        assertEquals(ResponseCategory.USAGE_GUIDANCE, nullResponse.getResponseCategory());
        assertNotNull(nullResponse.getMessage());
        assertEquals(ResponseCategory.USAGE_GUIDANCE, controlResponse.getResponseCategory());
        assertEquals(ResponseCategory.ADD, nextResponse.getResponseCategory());
        assertEquals("T | 0 | still works", Files.readString(dataFile).trim());
    }

    @Test
    void getResponse_extraArgumentsForNoArgumentCommandsReturnsUsageGuidance() throws Exception {
        Herta herta = new Herta(temporaryDirectory.resolve("usage.txt").toString());

        HertaResponse listResponse = herta.getResponse("list anything");
        HertaResponse byeResponse = herta.getResponse("bye anything");

        assertEquals(ResponseCategory.USAGE_GUIDANCE, listResponse.getResponseCategory());
        assertEquals("Just use: list. Nothing else is required.", listResponse.getMessage());
        assertEquals(ResponseCategory.USAGE_GUIDANCE, byeResponse.getResponseCategory());
        assertEquals("Just use: bye. Nothing else is required.", byeResponse.getMessage());
    }

    @Test
    void getResponse_leadingWhitespaceWorksForEveryCommandType() throws Exception {
        Herta herta = new Herta(temporaryDirectory.resolve("all-commands.txt").toString());

        assertEquals(ResponseCategory.ADD, herta.getResponse("  todo first").getResponseCategory());
        assertEquals(ResponseCategory.ADD, herta.getResponse(
                "  deadline second /by 9999-12-31").getResponseCategory());
        assertEquals(ResponseCategory.ADD, herta.getResponse(
                "  event third /from 9999-12-30 /to 9999-12-31").getResponseCategory());
        assertEquals(ResponseCategory.QUERY, herta.getResponse("  list").getResponseCategory());
        assertEquals(ResponseCategory.QUERY, herta.getResponse("  find first").getResponseCategory());
        assertEquals(ResponseCategory.QUERY, herta.getResponse(
                "  filter /on 9999-12-31").getResponseCategory());
        assertEquals(ResponseCategory.QUERY, herta.getResponse("  upcoming 1").getResponseCategory());
        assertEquals(ResponseCategory.QUERY, herta.getResponse("  sort date").getResponseCategory());
        assertEquals(ResponseCategory.MARK, herta.getResponse("  mark 1").getResponseCategory());
        assertEquals(ResponseCategory.UNMARK, herta.getResponse("  unmark 1").getResponseCategory());
        assertEquals(ResponseCategory.MARK, herta.getResponse("  mark 1").getResponseCategory());
        assertEquals(ResponseCategory.ARCHIVE, herta.getResponse("  archive 1").getResponseCategory());
        assertEquals(ResponseCategory.QUERY, herta.getResponse("  archived").getResponseCategory());
        assertEquals(ResponseCategory.RESTORE, herta.getResponse("  restore 1").getResponseCategory());
        assertEquals(ResponseCategory.EXIT, herta.getResponse("  bye").getResponseCategory());
    }

    @Test
    void getResponse_processesCommandAndReturnsOutput() throws Exception {
        Path dataFile = temporaryDirectory.resolve("herta.txt");
        Herta herta = new Herta(dataFile.toString());

        HertaResponse addResponse = herta.getResponse("todo read book");
        HertaResponse listResponse = herta.getResponse("list");
        HertaResponse invalidResponse = herta.getResponse("blah");
        HertaResponse goodbyeResponse = herta.getResponse("bye");

        assertTrue(addResponse.getMessage().contains("There. I've added it:"));
        assertTrue(listResponse.getMessage().contains("1. [T][ ] read book"));
        assertTrue(invalidResponse.getMessage().contains("That command isn't in my vocabulary."));
        assertTrue(goodbyeResponse.getMessage().contains("Leaving already? Goodbye."));
        assertEquals(ResponseCategory.ADD, addResponse.getResponseCategory());
        assertEquals(ResponseCategory.QUERY, listResponse.getResponseCategory());
        assertEquals(ResponseCategory.USAGE_GUIDANCE, invalidResponse.getResponseCategory());
        assertEquals(ResponseCategory.EXIT, goodbyeResponse.getResponseCategory());
        assertFalse(addResponse.isExitRequested());
        assertFalse(invalidResponse.isExitRequested());
        assertTrue(goodbyeResponse.isExitRequested());
        assertEquals("T | 0 | read book", Files.readString(dataFile).trim());
    }

    @Test
    void getResponse_classifiesTaskCommandsAndExecutionFailures() {
        Herta herta = new Herta(temporaryDirectory.resolve("herta.txt").toString());

        HertaResponse addResponse = herta.getResponse("todo read book");
        HertaResponse markResponse = herta.getResponse("mark 1");
        HertaResponse unmarkResponse = herta.getResponse("unmark 1");
        HertaResponse deleteResponse = herta.getResponse("delete 1");
        HertaResponse executionFailureResponse = herta.getResponse("mark 1");

        assertEquals(ResponseCategory.ADD, addResponse.getResponseCategory());
        assertEquals(ResponseCategory.MARK, markResponse.getResponseCategory());
        assertEquals(ResponseCategory.UNMARK, unmarkResponse.getResponseCategory());
        assertEquals(ResponseCategory.DELETE, deleteResponse.getResponseCategory());
        assertEquals(ResponseCategory.ERROR, executionFailureResponse.getResponseCategory());
        assertFalse(executionFailureResponse.isExitRequested());
    }

    @Test
    void getResponse_distinguishesUsageGuidanceFromExecutionErrors() {
        Herta herta = new Herta(temporaryDirectory.resolve("herta.txt").toString());

        HertaResponse malformedCommandResponse = herta.getResponse("mark nope");
        HertaResponse missingTaskResponse = herta.getResponse("mark 999");

        assertEquals(ResponseCategory.USAGE_GUIDANCE,
                malformedCommandResponse.getResponseCategory());
        assertEquals(ResponseCategory.ERROR, missingTaskResponse.getResponseCategory());
    }

    @Test
    void getResponse_commandLengthBoundaryAcceptsMaximumAndRejectsOverLimit() throws Exception {
        Path dataFile = temporaryDirectory.resolve("command-length.txt");
        Herta herta = new Herta(dataFile.toString());
        int maximumCommandLength = 4_096;
        String maximumCommand = "list" + " ".repeat(maximumCommandLength - "list".length());
        String overlongCommand = maximumCommand + " ";

        HertaResponse acceptedResponse = herta.getResponse(maximumCommand);
        HertaResponse rejectedResponse = herta.getResponse(overlongCommand);

        assertEquals(ResponseCategory.QUERY, acceptedResponse.getResponseCategory());
        assertEquals(ResponseCategory.USAGE_GUIDANCE, rejectedResponse.getResponseCategory());
        assertTrue(Files.notExists(dataFile));
    }

    @Test
    void herta_invalidArchiveRestoreThenValidOperations_preservesPersistence() throws Exception {
        Path activeFile = temporaryDirectory.resolve("archive-restore-recovery.txt");
        Path archiveFile = activeFile.resolveSibling("archive.txt");
        Herta herta = new Herta(activeFile.toString());
        herta.getResponse("todo incomplete");
        FileState activeBeforeArchive = captureFileState(activeFile);
        FileState archiveBeforeArchive = captureFileState(archiveFile);

        HertaResponse invalidArchive = herta.getResponse("archive 1");

        assertEquals(ResponseCategory.ERROR, invalidArchive.getResponseCategory());
        assertFileUnchanged(activeFile, activeBeforeArchive);
        assertFileUnchanged(archiveFile, archiveBeforeArchive);

        herta.getResponse("mark 1");
        assertEquals(ResponseCategory.ARCHIVE, herta.getResponse("archive 1").getResponseCategory());
        FileState activeBeforeRestore = captureFileState(activeFile);
        FileState archiveBeforeRestore = captureFileState(archiveFile);

        HertaResponse invalidRestore = herta.getResponse("restore 2");

        assertEquals(ResponseCategory.ERROR, invalidRestore.getResponseCategory());
        assertFileUnchanged(activeFile, activeBeforeRestore);
        assertFileUnchanged(archiveFile, archiveBeforeRestore);
        assertEquals(ResponseCategory.RESTORE, herta.getResponse("restore 1").getResponseCategory());
        assertEquals(List.of("T | 1 | incomplete"), Files.readAllLines(activeFile));
        assertEquals(List.of(), Files.readAllLines(archiveFile));
    }

    @Test
    void herta_restartAfterTypedUnicodeChanges_preservesTypesStatusesAndOrder() throws Exception {
        Path activeFile = temporaryDirectory.resolve("typed-unicode-restart.txt");
        Herta firstSession = new Herta(activeFile.toString());
        firstSession.getResponse("todo café 🚀");
        firstSession.getResponse("deadline due /by 9999-12-31");
        firstSession.getResponse("event meeting /from 9999-12-30 /to 9999-12-31");
        firstSession.getResponse("mark 1");
        firstSession.getResponse("archive 1");

        Herta restartedSession = new Herta(activeFile.toString());
        String activeOutput = restartedSession.getResponse("list").getMessage();
        String archiveOutput = restartedSession.getResponse("archived").getMessage();

        assertTrue(activeOutput.contains("1. [D][ ] due (by: Dec 31 9999)"));
        assertTrue(activeOutput.contains("2. [E][ ] meeting (from: Dec 30 9999 to: Dec 31 9999)"));
        assertTrue(activeOutput.indexOf("1. [D]") < activeOutput.indexOf("2. [E]"));
        assertTrue(archiveOutput.contains("1. [T][X] café 🚀"));
    }

    private FileState captureFileState(Path file) throws Exception {
        boolean wasPresent = Files.exists(file);
        byte[] contents = wasPresent ? Files.readAllBytes(file) : new byte[0];
        return new FileState(wasPresent, contents);
    }

    private void assertFileUnchanged(Path file, FileState expectedState) throws Exception {
        assertEquals(expectedState.wasPresent(), Files.exists(file));
        if (expectedState.wasPresent()) {
            assertArrayEquals(expectedState.contents(), Files.readAllBytes(file));
        }
    }
}
