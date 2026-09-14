package cookie;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests Cookie's single-command response API used by the GUI. */
public class CookieTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void getCommandResult_validAndInvalidCommands_returnsMatchingResponsesAndTypes() {
        Cookie cookie = new Cookie(temporaryDirectory.resolve("cookie.txt").toString(), false);

        Cookie.CommandResult addResult = cookie.getCommandResult("todo buy milk");
        Cookie.CommandResult listResult = cookie.getCommandResult("list");
        Cookie.CommandResult errorResult = cookie.getCommandResult("unknown");

        assertTrue(addResult.message().contains("[T][ ] buy milk"));
        assertTrue(listResult.message().contains("1. [T][ ] buy milk"));
        assertEquals("Bruh... What is that command!?", errorResult.message());
        assertFalse(addResult.isError());
        assertFalse(listResult.isError());
        assertTrue(errorResult.isError());
    }

    @Test
    public void getStartupResponse_malformedRecords_reportsLineNumbersAndKeepsValidTasks()
            throws IOException {
        Path dataFile = temporaryDirectory.resolve("cookie.txt");
        List<String> savedLines = List.of(
                "T | Not Done | valid task",
                "invalid record",
                "D | Not Done | invalid date | 2026-02-30");
        Files.write(dataFile, savedLines);
        Cookie cookie = new Cookie(dataFile.toString(), false);

        Cookie.CommandResult startupResult = cookie.getStartupResult();

        assertTrue(startupResult.message().contains("lines 2, 3"));
        assertTrue(startupResult.isError());
        assertTrue(cookie.getResponse("list").contains("1. [T][ ] valid task"));
        assertEquals(savedLines, Files.readAllLines(dataFile));
    }

    @Test
    public void getResponse_sortDescription_displaysTemporaryViewAndPreservesStoredNumbers()
            throws IOException {
        Path dataFile = temporaryDirectory.resolve("cookie.txt");
        Cookie cookie = new Cookie(dataFile.toString(), false);
        cookie.getResponse("todo zebra");
        cookie.getResponse("todo apple");
        List<String> linesBeforeSort = Files.readAllLines(dataFile);

        String sortResponse = cookie.getResponse("sort description");

        assertTrue(sortResponse.indexOf("2. [T][ ] apple")
                < sortResponse.indexOf("1. [T][ ] zebra"));
        assertEquals(linesBeforeSort, Files.readAllLines(dataFile));

        cookie.getResponse("mark 2");
        String listResponse = cookie.getResponse("list");
        assertTrue(listResponse.indexOf("1. [T][ ] zebra")
                < listResponse.indexOf("2. [T][X] apple"));
    }

    @Test
    public void getResponse_duplicateTask_rejectsSecondTaskWithoutChangingStorage()
            throws IOException {
        Path dataFile = temporaryDirectory.resolve("cookie.txt");
        Cookie cookie = new Cookie(dataFile.toString(), false);
        cookie.getResponse("todo buy milk");
        List<String> linesAfterFirstTask = Files.readAllLines(dataFile);

        Cookie.CommandResult duplicateResult = cookie.getCommandResult("todo buy milk");

        assertEquals("Bruh... That task already exists.", duplicateResult.message());
        assertTrue(duplicateResult.isError());
        assertEquals(linesAfterFirstTask, Files.readAllLines(dataFile));
        assertTrue(cookie.getResponse("list").contains("1. [T][ ] buy milk"));
    }

    @Test
    public void getResponse_saveFailure_rollsBackAddition() throws IOException {
        Path parentFile = temporaryDirectory.resolve("not-a-directory");
        Files.writeString(parentFile, "existing data");
        Cookie cookie = new Cookie(parentFile.resolve("cookie.txt").toString(), false);

        Cookie.CommandResult result = cookie.getCommandResult("todo buy milk");

        assertTrue(result.isError());
        assertTrue(result.message().startsWith("Oh no! I couldn't save your tasks:"));
        assertFalse(cookie.getResponse("list").contains("buy milk"));
        assertEquals("existing data", Files.readString(parentFile));
    }

    @Test
    public void getResponse_loadFailure_blocksChangesToProtectExistingData() {
        Cookie cookie = new Cookie(temporaryDirectory.toString(), false);

        Cookie.CommandResult startupResult = cookie.getStartupResult();
        Cookie.CommandResult addResult = cookie.getCommandResult("todo buy milk");

        assertTrue(startupResult.isError());
        assertTrue(startupResult.message().startsWith("Oh no! I couldn't load your tasks:"));
        assertEquals("Bruh... I can't change tasks because the task file could not be loaded. "
                + "Fix the file problem and restart Cookie first.", addResult.message());
        assertTrue(addResult.isError());
        assertTrue(Files.isDirectory(temporaryDirectory));
    }
}
