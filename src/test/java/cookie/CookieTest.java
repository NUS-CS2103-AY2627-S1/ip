package cookie;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
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
    public void getResponse_supportedCommands_executesCompleteCommandWorkflow() {
        Cookie cookie = new Cookie(temporaryDirectory.resolve("cookie.txt").toString(), false);

        assertTrue(cookie.getResponse("deadline submit report /by 2026-08-27")
                .contains("[D][ ] submit report"));
        assertTrue(cookie.getResponse(
                "event project meeting /from 2026-08-27 0900 /to 2026-08-27 1000")
                .contains("[E][ ] project meeting"));
        assertTrue(cookie.getResponse("find report").contains("1. [D][ ] submit report"));
        assertTrue(cookie.getResponse("on 2026-08-27").contains("2. [E][ ] project meeting"));
        assertTrue(cookie.getResponse("sort date desc").contains("sorted by date (descending)"));
        assertTrue(cookie.getResponse("mark 1").contains("[D][X] submit report"));
        assertTrue(cookie.getResponse("unmark 1").contains("[D][ ] submit report"));
        assertTrue(cookie.getResponse("delete 2").contains("[E][ ] project meeting"));
        assertEquals("Bye. I'm going to sleep.", cookie.getResponse("bye"));

        String listResponse = cookie.getResponse("list");
        assertTrue(listResponse.contains("1. [D][ ] submit report"));
        assertFalse(listResponse.contains("project meeting"));
    }

    @Test
    public void getStartupResponse_cleanLoad_returnsNoIssue() {
        Cookie cookie = new Cookie(temporaryDirectory.resolve("cookie.txt").toString(), false);

        assertEquals("", cookie.getStartupResponse());
        assertFalse(cookie.getStartupResult().isError());
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
    public void getResponse_saveFailure_rollsBackMark() throws IOException {
        Path storageDirectory = temporaryDirectory.resolve("storage");
        Path dataFile = storageDirectory.resolve("cookie.txt");
        Cookie cookie = new Cookie(dataFile.toString(), false);
        cookie.getResponse("todo buy milk");
        blockStorageDirectory(storageDirectory, dataFile);

        Cookie.CommandResult result = cookie.getCommandResult("mark 1");

        assertTrue(result.isError());
        assertTrue(result.message().startsWith("Oh no! I couldn't save your tasks:"));
        assertTrue(cookie.getResponse("list").contains("1. [T][ ] buy milk"));
    }

    @Test
    public void getResponse_saveFailure_rollsBackUnmark() throws IOException {
        Path storageDirectory = temporaryDirectory.resolve("storage");
        Path dataFile = storageDirectory.resolve("cookie.txt");
        Cookie cookie = new Cookie(dataFile.toString(), false);
        cookie.getResponse("todo buy milk");
        cookie.getResponse("mark 1");
        blockStorageDirectory(storageDirectory, dataFile);

        Cookie.CommandResult result = cookie.getCommandResult("unmark 1");

        assertTrue(result.isError());
        assertTrue(result.message().startsWith("Oh no! I couldn't save your tasks:"));
        assertTrue(cookie.getResponse("list").contains("1. [T][X] buy milk"));
    }

    @Test
    public void getResponse_saveFailure_rollsBackDelete() throws IOException {
        Path storageDirectory = temporaryDirectory.resolve("storage");
        Path dataFile = storageDirectory.resolve("cookie.txt");
        Cookie cookie = new Cookie(dataFile.toString(), false);
        cookie.getResponse("todo buy milk");
        blockStorageDirectory(storageDirectory, dataFile);

        Cookie.CommandResult result = cookie.getCommandResult("delete 1");

        assertTrue(result.isError());
        assertTrue(result.message().startsWith("Oh no! I couldn't save your tasks:"));
        assertTrue(cookie.getResponse("list").contains("1. [T][ ] buy milk"));
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

    @Test
    public void run_commandsFromStandardInput_printsGreetingResponsesAndExit() {
        InputStream originalInput = System.in;
        PrintStream originalOutput = System.out;
        ByteArrayOutputStream printedBytes = new ByteArrayOutputStream();
        String commands = "todo buy milk" + System.lineSeparator()
                + "list" + System.lineSeparator()
                + "bye" + System.lineSeparator();

        try {
            System.setIn(new ByteArrayInputStream(commands.getBytes(StandardCharsets.UTF_8)));
            System.setOut(new PrintStream(printedBytes, true, StandardCharsets.UTF_8));
            new Cookie(temporaryDirectory.resolve("cookie.txt").toString()).run();
        } finally {
            System.setIn(originalInput);
            System.setOut(originalOutput);
        }

        String printed = printedBytes.toString(StandardCharsets.UTF_8);
        assertTrue(printed.contains("Hello! I'm your favourite chatbot Cookie."));
        assertTrue(printed.contains("Ok. I've added this task:"));
        assertTrue(printed.contains("1. [T][ ] buy milk"));
        assertTrue(printed.contains("Bye. I'm going to sleep."));
    }

    private void blockStorageDirectory(Path storageDirectory, Path dataFile) throws IOException {
        Files.delete(dataFile);
        Files.delete(storageDirectory);
        Files.writeString(storageDirectory, "existing data");
    }
}
