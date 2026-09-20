package fifi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests the chatbot's public run method with controlled input and output.
 */
public class FifiTest {
    private final PrintStream originalOut = System.out;

    @TempDir
    private Path temporaryDirectory;

    @AfterEach
    public void restoreOutput() {
        System.setOut(originalOut);
    }

    @Test
    public void run_byeCommand_welcomeAndExitMessagesPrinted() {
        System.setIn(new ByteArrayInputStream("bye\n".getBytes(StandardCharsets.UTF_8)));
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
        Fifi fifi = new Fifi(temporaryDirectory.resolve("fifi.txt").toString());

        fifi.run();

        String printedText = output.toString(StandardCharsets.UTF_8);
        assertTrue(printedText.contains("Hello! My name is Fifi ^^"));
        assertTrue(printedText.contains("BaiBai! Hope to see you soon ^^"));
    }

    @Test
    public void getResponse_todoCommand_taskAddedResponseReturned() {
        Fifi fifi = new Fifi(temporaryDirectory.resolve("fifi.txt").toString());

        String response = fifi.getResponse("todo read book");

        assertEquals(normalizeLineEndings("""
                Got it. I've added this task:
                [T][ ] read book
                Now you have 1 tasks in the list."""),
                normalizeLineEndings(response));
    }

    @Test
    public void getResponse_invalidCommand_errorResponseReturned() {
        Fifi fifi = new Fifi(temporaryDirectory.resolve("fifi.txt").toString());

        String response = fifi.getResponse("blah");

        assertEquals("""
                UhOh, this command is invalid, please enter a valid one!
                Valid commands include "list, todo, event, deadline, mark, unmark, delete, show, find, stats\"""",
                response);
    }

    @Test
    public void getResponse_statsCommand_multilineStatisticsReturned() {
        Fifi fifi = new Fifi(temporaryDirectory.resolve("fifi.txt").toString());
        fifi.getResponse("todo read book");
        fifi.getResponse("mark 1");

        String response = fifi.getResponse("stats");

        assertTrue(response.contains("Task statistics for all tasks:"));
        assertTrue(response.contains("Total tasks: 1"));
        assertTrue(response.contains("Completed tasks: 1"));
        assertTrue(normalizeLineEndings(response).contains("Todos:\nCompleted: 1"));
    }

    @Test
    public void getResponse_malformedTaskNumbers_statePreservedAndCommandsContinue() throws Exception {
        Path dataFile = temporaryDirectory.resolve("fifi.txt");
        Fifi fifi = new Fifi(dataFile.toString());
        fifi.getResponse("todo read book");
        assertTrue(Files.readString(dataFile).startsWith("T | 0 | read book | "));

        String[] commands = {"mark", "unmark", "delete"};
        for (String command : commands) {
            String savedTasks = Files.readString(dataFile);
            String displayedTasks = fifi.getResponse("list");

            assertEquals("Oops! Please enter a task number after " + command + ", e.g. " + command + " 1.",
                    fifi.getResponse(command + " 1 read book"));
            assertEquals(savedTasks, Files.readString(dataFile));
            assertEquals(displayedTasks, fifi.getResponse("list"));

            fifi.getResponse(command + " 1");
            String savedData = Files.readString(dataFile);
            if (command.equals("delete")) {
                assertEquals("", savedData);
            } else {
                String expectedStatus = command.equals("mark") ? "1" : "0";
                assertTrue(savedData.startsWith("T | " + expectedStatus + " | read book | "));
            }
        }
        assertEquals("BaiBai! Hope to see you soon ^^", fifi.getResponse("bye"));
        assertTrue(fifi.isExit());
    }

    @Test
    public void getChatResponse_invalidThenValidCommands_statusAndTaskStateRemainIndependent() throws Exception {
        Path dataFile = temporaryDirectory.resolve("fifi.txt");
        Fifi fifi = new Fifi(dataFile.toString());
        ChatResponse addedTask = fifi.getChatResponse("todo Oops! read book");
        assertFalse(addedTask.isError());
        String savedTasks = Files.readString(dataFile);

        String[] invalidCommands = {"blah", "todo", "show 2025-02-30", "mark 0", "unmark -1", "delete 99",
            "mark -2147483648", "stats since"};
        for (String invalidCommand : invalidCommands) {
            ChatResponse error = fifi.getChatResponse(invalidCommand);
            assertTrue(error.isError(), invalidCommand);
            assertFalse(error.getMessage().isBlank());
            assertFalse(fifi.isExit());
            assertEquals(savedTasks, Files.readString(dataFile));

            ChatResponse success = fifi.getChatResponse("list");
            assertFalse(success.isError());
            assertTrue(success.getMessage().contains("1. [T][ ] Oops! read book"));
            assertTrue(error.isError());
            assertFalse(addedTask.isError());
        }

        ChatResponse exit = fifi.getChatResponse("bye");
        assertFalse(exit.isError());
        assertTrue(fifi.isExit());
    }

    @Test
    public void run_inputEndsWithoutBye_exitsCleanlyAfterProcessingCommands() {
        System.setIn(new ByteArrayInputStream("todo read book\nlist\n".getBytes(StandardCharsets.UTF_8)));
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
        Fifi fifi = new Fifi(temporaryDirectory.resolve("fifi.txt").toString());

        fifi.run();

        assertTrue(fifi.isExit());
        assertTrue(output.toString(StandardCharsets.UTF_8).contains("1. [T][ ] read book"));
    }

    @Test
    public void getChatResponse_emptyTaskList_invalidTaskNumbersDoNotPreventAddingTasks() {
        Fifi fifi = new Fifi(temporaryDirectory.resolve("fifi.txt").toString());
        ChatResponse error = fifi.getChatResponse("delete 1");
        assertTrue(error.isError());
        assertEquals("Oops! Your task list is empty. Add a task first.", error.getMessage());

        ChatResponse success = fifi.getChatResponse("todo read book");
        assertFalse(success.isError());
        assertTrue(success.getMessage().contains("Now you have 1 tasks in the list."));
    }

    @Test
    public void getChatResponse_saveFailure_errorReturnedAndReadCommandsStillWork() throws Exception {
        Path blockedDirectory = temporaryDirectory.resolve("blocked");
        Fifi fifi = new Fifi(blockedDirectory.resolve("fifi.txt").toString());
        Files.writeString(blockedDirectory, "This file prevents creating a directory.");

        ChatResponse error = fifi.getChatResponse("todo read book");

        assertTrue(error.isError());
        assertEquals("Oops! I could not save your tasks to the hard disk. "
                + "Your task list has not been changed. Check file access and disk space, then try again.",
                error.getMessage());
        assertFalse(fifi.getChatResponse("list").isError());
        assertEquals("Here are the tasks in your list:", fifi.getResponse("list"));
        assertEquals("This file prevents creating a directory.", Files.readString(blockedDirectory));
        Files.delete(blockedDirectory);
        assertFalse(fifi.getChatResponse("todo read book").isError());
        assertTrue(Files.readString(blockedDirectory.resolve("fifi.txt")).contains("T | 0 | read book | "));
    }

    @Test
    public void getChatResponse_duplicateTask_savedDataUnchangedAndDeletionAllowsAddingAgain() throws Exception {
        Path dataFile = temporaryDirectory.resolve("fifi.txt");
        Fifi fifi = new Fifi(dataFile.toString());
        assertFalse(fifi.getChatResponse("todo read book").isError());
        assertFalse(fifi.getChatResponse("mark 1").isError());
        String savedData = Files.readString(dataFile);

        ChatResponse duplicate = fifi.getChatResponse("todo read book");
        assertTrue(duplicate.isError());
        assertEquals("Oops! A task with the same details already exists.", duplicate.getMessage());
        assertEquals(savedData, Files.readString(dataFile));
        assertTrue(fifi.getResponse("list").contains("1. [T][X] read book"));
        assertFalse(fifi.getChatResponse("delete 1").isError());
        assertFalse(fifi.getChatResponse("todo read book").isError());
    }

    @Test
    public void startup_corruptedData_reportedAndAllChangesBlockedUntilRestart() throws Exception {
        Path dataFile = temporaryDirectory.resolve("fifi.txt");
        String corruptedData = "T | 0 | valid task\nT";
        Files.writeString(dataFile, corruptedData);
        Fifi fifi = new Fifi(dataFile.toString());
        ChatResponse startupError = fifi.getStartupError().orElseThrow();
        assertTrue(startupError.isError());
        assertTrue(startupError.getMessage().contains("line 2"));
        assertTrue(startupError.getMessage().contains("Your saved file has not been changed."));

        String[] commands = {"todo new task", "deadline work /by 2026-09-20",
            "event work /from 2026-09-20 /to 2026-09-20", "mark 1", "unmark 1", "delete 1"};
        for (String command : commands) {
            ChatResponse error = fifi.getChatResponse(command);
            assertTrue(error.isError(), command);
            assertTrue(error.getMessage().contains("Tasks cannot be changed"));
            assertEquals(corruptedData, Files.readString(dataFile));
            assertFalse(fifi.getChatResponse("list").isError());
            assertFalse(fifi.getChatResponse("stats").isError());
        }
        Files.writeString(dataFile, "T | 0 | valid task");
        assertTrue(fifi.getChatResponse("todo new task").isError());
        Fifi restartedFifi = new Fifi(dataFile.toString());
        assertTrue(restartedFifi.getStartupError().isEmpty());
        assertTrue(restartedFifi.getResponse("list").contains("valid task"));
        assertFalse(restartedFifi.getChatResponse("todo new task").isError());
    }

    @Test
    public void startup_unreadableFile_reportedWithoutReplacingIt() throws Exception {
        Path dataFile = Files.createDirectory(temporaryDirectory.resolve("fifi.txt"));
        Fifi fifi = new Fifi(dataFile.toString());

        assertTrue(fifi.getStartupError().orElseThrow().isError());
        assertTrue(fifi.getChatResponse("todo work").isError());
        assertFalse(fifi.getChatResponse("list").isError());
        assertTrue(Files.isDirectory(dataFile));
    }

    @Test
    public void getChatResponse_creationTimeAfterClock_markRejectedWithoutCorruptingSavedData() throws Exception {
        Path dataFile = temporaryDirectory.resolve("fifi.txt");
        LocalDateTime futureTime = LocalDateTime.now().plusYears(1);
        String originalData = "T | 0 | future task | " + futureTime + " | -";
        Files.writeString(dataFile, originalData);
        Fifi fifi = new Fifi(dataFile.toString());
        assertTrue(fifi.getStartupError().isEmpty());

        ChatResponse error = fifi.getChatResponse("mark 1");
        assertTrue(error.isError());
        assertTrue(error.getMessage().contains("Check your system clock"));
        assertEquals(originalData, Files.readString(dataFile));
        assertTrue(fifi.getResponse("list").contains("[T][ ] future task"));
        assertFalse(fifi.getChatResponse("todo ordinary task").isError());
        assertTrue(new Storage(dataFile.toString()).loadTasks().get(0).getLastMarkedAt().isEmpty());
    }

    private String normalizeLineEndings(String text) {
        return text.replace("\r\n", "\n");
    }
}
