package friedberg;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import datahandler.DataHandler;
import parser.CommandContext;

/**
 * Tests for processing one input through the shared Friedberg backend.
 */
class FriedbergTest {
    /** Temporary directory supplied by JUnit for safe storage testing. */
    @TempDir
    Path tempDir;

    @Test
    void processInput_successiveCommands_shareTaskState() throws Exception {
        Friedberg friedberg = createFriedbergWithTempStorage();

        CommandResult addResult = friedberg.processInput("todo read book");
        CommandResult listResult = friedberg.processInput("list");

        assertFalse(addResult.shouldExit());
        assertEquals(ResponseType.ADD, addResult.responseType());
        assertEquals("Got it. I've added this task:\n[T][ ] read book\nNow you have 1 tasks in the list.",
                addResult.message());
        assertEquals(ResponseType.DEFAULT, listResult.responseType());
        assertEquals("Here are the tasks in your list:\n1. [T][ ] read book", listResult.message());
    }

    @Test
    void processInput_findMarkUnmarkDelete_returnExpectedMessages() throws Exception {
        Friedberg friedberg = createFriedbergWithTempStorage();
        friedberg.processInput("todo read book");
        friedberg.processInput("todo write code");

        CommandResult findResult = friedberg.processInput("find read");
        CommandResult markResult = friedberg.processInput("mark 1");
        CommandResult unmarkResult = friedberg.processInput("unmark 1");
        CommandResult deleteResult = friedberg.processInput("delete 1");

        assertEquals(ResponseType.DEFAULT, findResult.responseType());
        assertEquals("Here are the matching tasks in your list:\n[T][ ] read book", findResult.message());
        assertEquals(ResponseType.STATUS, markResult.responseType());
        assertEquals("Nice! I've marked this task as done:\n[T][X] read book", markResult.message());
        assertEquals(ResponseType.STATUS, unmarkResult.responseType());
        assertEquals("OK, I've marked this task as not done yet:\n[T][ ] read book", unmarkResult.message());
        assertEquals(ResponseType.REMOVE, deleteResult.responseType());
        assertEquals("Noted. I've removed this task:\n[T][ ] read book\nNow you have 1 tasks in the list.",
                deleteResult.message());
    }

    @Test
    void processInput_archiveListUnarchive_returnExpectedMessages() throws Exception {
        Friedberg friedberg = createFriedbergWithTempStorage();
        friedberg.processInput("todo read book");
        friedberg.processInput("todo write code");

        CommandResult archiveResult = friedberg.processInput("archive 1");
        CommandResult listResult = friedberg.processInput("list");
        CommandResult archiveListResult = friedberg.processInput("alist");
        CommandResult unarchiveResult = friedberg.processInput("unarchive 1");

        assertEquals(ResponseType.REMOVE, archiveResult.responseType());
        assertEquals("Archived this task:\n[T][ ] read book\nNow you have 1 tasks in the list and 1 archived tasks.",
                archiveResult.message());
        assertEquals(ResponseType.DEFAULT, listResult.responseType());
        assertEquals("Here are the tasks in your list:\n1. [T][ ] write code", listResult.message());
        assertEquals(ResponseType.DEFAULT, archiveListResult.responseType());
        assertEquals("Here are the tasks in your archive:\n1. [T][ ] read book", archiveListResult.message());
        assertEquals(ResponseType.REMOVE, unarchiveResult.responseType());
        assertEquals("Unarchived this task:\n[T][ ] read book\nNow you have 2 tasks in the list and 0 archived tasks.",
                unarchiveResult.message());
        assertEquals("Here are the tasks in your list:\n1. [T][ ] write code\n2. [T][ ] read book",
                friedberg.processInput("list").message());
    }

    @Test
    void processInput_help_returnsDefaultCommandSummary() throws Exception {
        Friedberg friedberg = createFriedbergWithTempStorage();

        CommandResult helpResult = friedberg.processInput("help");

        assertFalse(helpResult.shouldExit());
        assertEquals(ResponseType.DEFAULT, helpResult.responseType());
        assertTrue(helpResult.message().startsWith("Commands available in Friedberg:"));
        assertTrue(helpResult.message().contains("todo DESCRIPTION"));
        assertTrue(helpResult.message().contains("bye - Exit Friedberg."));
    }

    @Test
    void processInput_errorAndBye_returnExpectedFlags() throws Exception {
        Friedberg friedberg = createFriedbergWithTempStorage();

        CommandResult errorResult = friedberg.processInput("unknown");
        CommandResult byeResult = friedberg.processInput("bye");

        assertFalse(errorResult.shouldExit());
        assertEquals(ResponseType.ERROR, errorResult.responseType());
        assertTrue(errorResult.message().startsWith("User Error using Friedberg:"));
        assertTrue(byeResult.shouldExit());
        assertEquals(ResponseType.DEFAULT, byeResult.responseType());
        assertEquals("Bye bye, see you again next time.", byeResult.message());
    }

    @Test
    void processInput_invalidWhitespace_returnsErrorsWithoutChangingTasks() throws Exception {
        Friedberg friedberg = createFriedbergWithTempStorage();
        String[] inputs = {" todo read book", "todo read book ", "todo  read book", "todo\tread book"};

        for (String input : inputs) {
            CommandResult result = friedberg.processInput(input);
            assertEquals(ResponseType.ERROR, result.responseType(), input);
            assertTrue(result.message().startsWith("User Error using Friedberg:"), input);
        }
        assertEquals("Here are the tasks in your list:", friedberg.processInput("list").message());
    }

    @Test
    void processInput_validCommand_doesNotPrintToStdout() throws Exception {
        Friedberg friedberg = createFriedbergWithTempStorage();
        PrintStream originalOut = System.out;
        ByteArrayOutputStream stdout = new ByteArrayOutputStream();
        System.setOut(new PrintStream(stdout));
        try {
            friedberg.processInput("todo quiet task");
        } finally {
            System.setOut(originalOut);
        }

        assertEquals("", stdout.toString());
    }

    @Test
    void processInput_successfulCommands_returnDisplayableResponses() throws Exception {
        Friedberg friedberg = createFriedbergWithTempStorage();
        String[] inputs = {
            "help", "list", "find missing", "todo read book", "deadline work /by 2026-09-09",
            "event meeting /from 2026-09-09 /to 2026-09-10",
            "mark 1", "unmark 1", "archive 1", "alist", "unarchive 1", "find book", "list", "delete 1", "bye"
        };
        for (String input : inputs) {
            CommandResult result = friedberg.processInput(input);
            assertNotNull(result.message(), input);
            assertFalse(result.message().isBlank(), input);
            assertFalse(result.message().startsWith("User Error using Friedberg:"), input);
        }
    }

    @Test
    void processInput_invalidInputs_returnErrorsRatherThanAssertionFailures() throws Exception {
        Friedberg friedberg = createFriedbergWithTempStorage();
        String[] inputs = {
            "unknown", "mark 1", "unmark -1", "delete 1", "archive 1", "unarchive 1",
            "deadline work /by invalid", "event meeting",
            "event meeting /from invalid /to 2026-09-10"
        };
        for (String input : inputs) {
            CommandResult result = friedberg.processInput(input);
            assertTrue(result.message().startsWith("User Error using Friedberg:"), input);
            assertEquals(ResponseType.ERROR, result.responseType(), input);
            assertFalse(result.shouldExit(), input);
        }
    }

    private Friedberg createFriedbergWithTempStorage() throws Exception {
        Path dataFolderPath = tempDir.resolve("data");
        Path dataFilePath = dataFolderPath.resolve("friedberg.txt");
        DataHandler dataHandler = new DataHandler(dataFolderPath, dataFilePath);
        return new Friedberg(new CommandContext(dataHandler));
    }
}
