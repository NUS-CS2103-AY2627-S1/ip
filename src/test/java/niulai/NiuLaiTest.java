package niulai;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import niulai.gui.GuiUi;

/** Tests session behavior around recoverable and unrecoverable storage failures. */
class NiuLaiTest {
    /** Supplies deterministic commands while retaining the real GUI-output adapter. */
    private static class ScriptedUi extends GuiUi {
        /** Commands that have not yet been read. */
        private final ArrayDeque<String> commands;

        /** Creates a UI that returns the supplied commands in order. */
        ScriptedUi(String... commands) {
            this.commands = new ArrayDeque<>(Arrays.asList(commands));
        }

        /** Returns whether another scripted command is available. */
        @Override
        public boolean hasNextLine() {
            return !commands.isEmpty();
        }

        /** Removes and returns the next scripted command. */
        @Override
        public String readCommand() {
            return commands.removeFirst();
        }
    }

    /** Temporary directory isolated from the application's real data file. */
    @TempDir
    Path temporaryDirectory;

    /** Verifies that a partly corrupt file reports skipped lines and retains valid tasks. */
    @Test
    void startSession_partlyCorruptFile_reportsSkippedLinesAndKeepsValidTasks()
            throws IOException {
        Path dataFile = temporaryDirectory.resolve("niulai.txt");
        Files.writeString(dataFile, "T | 0 | valid\nmalformed");
        GuiUi ui = new GuiUi();
        NiuLai chatbot = new NiuLai(dataFile.toString(), ui);

        chatbot.startSession();
        String startup = ui.consumeOutput();
        chatbot.processCommand("list");
        String listOutput = ui.consumeOutput();

        assertTrue(startup.contains("line 2"));
        assertTrue(startup.contains("backed up"));
        assertTrue(listOutput.contains("1.[T][ ] valid"));
    }

    /** Verifies that an unreadable path blocks mutations while read-only commands still work. */
    @Test
    void startSession_directoryPath_blocksMutationsButAllowsReadOnlyCommands() {
        GuiUi ui = new GuiUi();
        NiuLai chatbot = new NiuLai(temporaryDirectory.toString(), ui);

        chatbot.startSession();
        assertTrue(ui.consumeOutput().contains("changes are disabled"));

        chatbot.processCommand("todo new task");
        assertTrue(ui.consumeOutput().contains("changes are disabled"));
        chatbot.processCommand("list");
        String listOutput = ui.consumeOutput();

        assertTrue(listOutput.contains("Here are the tasks"));
        assertTrue(!listOutput.contains("new task"));
    }

    /** Verifies that an external file edit prevents a task command from overwriting it. */
    @Test
    void processCommand_externalFileChange_rollsBackMutationAndPreservesExternalBytes()
            throws IOException {
        Path dataFile = temporaryDirectory.resolve("niulai.txt");
        Files.writeString(dataFile, "T | 0 | original task");
        GuiUi ui = new GuiUi();
        NiuLai chatbot = new NiuLai(dataFile.toString(), ui);
        chatbot.startSession();
        ui.consumeOutput();
        byte[] externalBytes = "T | 0 | externally changed"
                .getBytes(StandardCharsets.UTF_8);
        Files.write(dataFile, externalBytes);

        chatbot.processCommand("todo new task");
        String addOutput = ui.consumeOutput();
        chatbot.processCommand("list");
        String listOutput = ui.consumeOutput();

        assertTrue(addOutput.contains("changed outside NiuLai"));
        assertTrue(!listOutput.contains("new task"));
        assertArrayEquals(externalBytes, Files.readAllBytes(dataFile));
    }

    /** Verifies the run loop processes commands only until the first exit command. */
    @Test
    void run_commandsThroughBye_stopsBeforeLaterInput() throws IOException {
        Path dataFile = temporaryDirectory.resolve("niulai.txt");
        ScriptedUi ui = new ScriptedUi("todo first task", "bye", "todo ignored task");

        new NiuLai(dataFile.toString(), ui).run();

        assertEquals("T | 0 | first task", Files.readString(dataFile).strip());
        String output = ui.consumeOutput();
        assertTrue(output.contains("I've added this task"));
        assertTrue(output.contains("Bye. Hope not to see you again."));
        assertFalse(output.contains("ignored task"));
    }

    /** Verifies the run loop returns normally when input ends without an exit command. */
    @Test
    void run_inputEndsWithoutBye_returnsAfterAvailableCommands() {
        ScriptedUi ui = new ScriptedUi("list");

        new NiuLai(temporaryDirectory.resolve("niulai.txt").toString(), ui).run();

        assertTrue(ui.consumeOutput().contains("Here are the tasks in your list"));
    }

    /** Verifies restarting a session clears undo history while reloading persisted tasks. */
    @Test
    void startSession_afterMutation_clearsUndoHistory() {
        GuiUi ui = new GuiUi();
        NiuLai chatbot = new NiuLai(
                temporaryDirectory.resolve("niulai.txt").toString(), ui);
        chatbot.startSession();
        ui.consumeOutput();
        chatbot.processCommand("todo saved task");
        ui.consumeOutput();

        chatbot.startSession();
        ui.consumeOutput();
        chatbot.processCommand("undo");

        assertTrue(ui.consumeOutput().contains("There is nothing to undo"));
    }

    /** Verifies a failed undo save restores in-memory state and reports the storage conflict. */
    @Test
    void processCommand_undoAfterExternalFileChange_restoresTaskInMemory() throws IOException {
        Path dataFile = temporaryDirectory.resolve("niulai.txt");
        GuiUi ui = new GuiUi();
        NiuLai chatbot = new NiuLai(dataFile.toString(), ui);
        chatbot.startSession();
        ui.consumeOutput();
        chatbot.processCommand("todo task to keep");
        ui.consumeOutput();
        Files.writeString(dataFile, "T | 0 | external task");

        assertFalse(chatbot.processCommand("undo"));
        assertTrue(ui.consumeOutput().contains("changed outside NiuLai"));
        chatbot.processCommand("list");

        assertTrue(ui.consumeOutput().contains("task to keep"));
        assertEquals("T | 0 | external task", Files.readString(dataFile));
    }

    /** Verifies constructor and command processing reject null collaborators or input. */
    @Test
    void chatbot_nullUiOrInput_exceptionThrown() {
        Path dataFile = temporaryDirectory.resolve("niulai.txt");

        assertThrows(NullPointerException.class,
                () -> new NiuLai(dataFile.toString(), null));
        NiuLai chatbot = new NiuLai(dataFile.toString(), new GuiUi());
        assertThrows(NullPointerException.class, () -> chatbot.processCommand(null));
    }
}
