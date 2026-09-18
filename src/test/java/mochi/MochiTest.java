package mochi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import mochi.ui.Ui;

class MochiTest {
    @TempDir
    Path tempDir;

    @Test
    void getResponse_commandsAcrossGuiRequests_preserveTaskState() {
        Mochi mochi = Mochi.forGui(tempDir.resolve("data").resolve("mochi.txt"));

        assertTrue(mochi.getResponse("todo read book").contains("[T][ ] read book"));
        assertEquals("Here are the matching tasks in your list:\n1.[T][ ] read book",
                mochi.getResponse("find book"));
        assertEquals("Rest well! Mochi will be here for your next little step.", mochi.getResponse("bye"));
    }
    @Test
    void sort_mixedTasks_preservesDetailsAndPersistsNewOrder() {
        Path file = tempDir.resolve("data").resolve("sorted.txt");
        Mochi mochi = Mochi.forGui(file);
        mochi.getResponse("todo Zebra");
        mochi.getResponse("deadline apple /by 2026-09-12");
        mochi.getResponse("event Apple /from noon /to evening");
        mochi.getResponse("mark 1");

        String sorted = mochi.getResponse("sort");
        assertTrue(sorted.indexOf("apple") < sorted.indexOf("Apple"));
        assertTrue(sorted.indexOf("Apple") < sorted.indexOf("Zebra"));
        assertTrue(sorted.contains("3.[T][X] Zebra"));
        assertEquals(sorted, Mochi.forGui(file).getResponse("list"));
        assertTrue(mochi.getResponse("delete 3").contains("[T][X] Zebra"));
    }

    @Test
    void sort_emptyListAndExtraArguments_handleWithoutChangingTasks() {
        Mochi mochi = Mochi.forGui(tempDir.resolve("data").resolve("empty.txt"));
        assertEquals("Here are the tasks in your list:\nNothing here yet. A little breathing room!",
                mochi.getResponse("sort"));
        mochi.getResponse("todo Zebra");
        mochi.getResponse("todo apple");
        String before = mochi.getResponse("list");
        assertEquals("Oops! The sort command does not take extra details.", mochi.getResponse("sort date"));
        assertEquals(before, mochi.getResponse("list"));
    }

    @Test
    void invalidCommands_returnErrorsWithoutChangingTasks() {
        Mochi mochi = Mochi.forGui(tempDir.resolve("data").resolve("errors.txt"));
        mochi.getResponse("todo keep this task");

        assertEquals("Oops! A todo needs a description.", mochi.getResponse("todo"));
        assertEquals("Oops! That task number is not in your list.", mochi.getResponse("delete 2"));
        assertEquals("Oops! The list command does not take extra details.", mochi.getResponse("list now"));
        assertEquals("Here are the tasks in your list:\n1.[T][ ] keep this task", mochi.getResponse("list"));
    }

    @Test
    void taskLifecycle_restartsWithCorrectStatusAndDeletion() {
        Path file = tempDir.resolve("lifecycle.txt");
        Mochi mochi = Mochi.forGui(file);
        mochi.getResponse("  todo   read book  ");
        assertTrue(mochi.getResponse("mark 1").contains("[T][X] read book"));
        mochi = Mochi.forGui(file);
        assertTrue(mochi.getResponse("list").contains("[T][X] read book"));
        assertTrue(mochi.getResponse("unmark 1").contains("[T][ ] read book"));
        mochi = Mochi.forGui(file);
        assertTrue(mochi.getResponse("list").contains("[T][ ] read book"));
        mochi.getResponse("delete 1");
        assertFalse(Mochi.forGui(file).getResponse("list").contains("read book"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"todo new task", "deadline report /by 2026-09-18",
        "event meeting /from noon /to evening", "mark 1", "unmark 2", "delete 1", "sort"})
    void failedSave_keepsTaskDetailsStatusAndOrder(String command) throws Exception {
        Path file = tempDir.resolve("tasks.txt");
        Mochi mochi = Mochi.forGui(file);
        mochi.getResponse("todo Zebra");
        mochi.getResponse("todo apple");
        mochi.getResponse("mark 2");
        String before = mochi.getResponse("list");
        // A directory in place of the file produces a portable write failure, even as root.
        Files.delete(file);
        Files.createDirectory(file);

        assertTrue(mochi.getResponse(command).startsWith("Oops! I couldn't save your tasks:"));
        assertTrue(mochi.hasResponseError());
        assertEquals(before, mochi.getResponse("list"));
        assertFalse(mochi.hasResponseError());
        Files.delete(file);
        assertFalse(mochi.getResponse(command).startsWith("Oops!"));
        assertEquals(mochi.getResponse("list"), Mochi.forGui(file).getResponse("list"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"todo new task", "deadline report /by 2026-09-18",
        "event meeting /from noon /to evening", "mark 1", "unmark 1", "delete 1", "sort"})
    void corruptStartup_preservesOriginalFileAndReportsProblem(String command) throws Exception {
        Path file = tempDir.resolve("corrupt.txt");
        String original = "T | 0 | keep me\nmalformed record\n";
        Files.writeString(file, original);
        Mochi mochi = Mochi.forGui(file);

        assertTrue(mochi.getStartupError().contains("line 2"));
        assertTrue(mochi.getResponse(command).contains("left untouched"));
        assertTrue(mochi.hasResponseError());
        assertEquals(original, Files.readString(file));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "nonsense", "bye now", "find", "list now", "sort date",
        "mark zero", "unmark 0", "delete -1", "deadline task /by 2026-02-30"})
    void rejectedRequest_setsErrorFlagAndAllowsNextCommand(String command) {
        Mochi mochi = Mochi.forGui(tempDir.resolve("errors.txt"));
        assertTrue(mochi.getResponse(command).startsWith("Oops!"));
        assertTrue(mochi.hasResponseError());
        assertTrue(mochi.getResponse("todo continue").contains("[T][ ] continue"));
        assertFalse(mochi.hasResponseError());
    }

    @Test
    void nullRequest_returnsFriendlyError() {
        Mochi mochi = Mochi.forGui(tempDir.resolve("null.txt"));
        assertTrue(mochi.getStartupError().isEmpty());
        assertTrue(mochi.getResponse(null).startsWith("Oops!"));
        assertTrue(mochi.hasResponseError());
    }

    @Test
    void consoleSession_errorsAndBye_continueThenStopReadingCommands() {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        Ui ui = new Ui(new Scanner("todo read book\nbye now\nmark 1\nbye\ntodo never added\n"),
                new PrintStream(buffer, true, StandardCharsets.UTF_8));
        Path file = tempDir.resolve("console.txt");
        new Mochi(file, ui).run();

        String output = buffer.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("Hello! I'm Mochi"));
        assertTrue(output.contains("Oops! The bye command does not take extra details."));
        assertTrue(output.contains("[T][X] read book"));
        assertTrue(output.contains("Rest well!"));
        assertFalse(Mochi.forGui(file).getResponse("list").contains("never added"));
    }

    @Test
    void consoleSession_endOfInput_exitsWithoutException() {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        Ui ui = new Ui(new Scanner("list\n"), new PrintStream(buffer, true, StandardCharsets.UTF_8));
        new Mochi(tempDir.resolve("eof.txt"), ui).run();
        assertTrue(buffer.toString(StandardCharsets.UTF_8).contains("Nothing here yet."));
    }
}
