package shan;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ShanTest {
    @TempDir
    private Path dataDirectory;

    @Test
    void executeCommand_validAddAndList_returnsCommandResponses() {
        Shan shan = new Shan(this.dataDirectory.resolve("shan.txt"));
        shan.initialize();

        CommandResult addResult = shan.executeCommand("todo read book");
        assertTrue(addResult.message().contains("I've added this"));
        assertFalse(addResult.shouldExit());

        CommandResult listResult = shan.executeCommand("list");
        assertTrue(listResult.message().contains("read book"));
        assertFalse(listResult.shouldExit());
    }

    @Test
    void executeCommand_unknownCommand_returnsErrorWithoutExit() {
        Shan shan = new Shan(this.dataDirectory.resolve("shan.txt"));

        CommandResult result = shan.executeCommand("unknown");

        assertEquals("I don't recognize that command.", result.message());
        assertFalse(result.shouldExit());
    }

    @Test
    void executeCommand_bye_returnsFarewellAndExitFlag() {
        Shan shan = new Shan(this.dataDirectory.resolve("shan.txt"));

        CommandResult result = shan.executeCommand("bye");
        assertEquals("Bye! See you soon.", result.message());
        assertTrue(result.shouldExit());
    }

    @Test
    void executeCommand_undoAfterAdd_removesTaskAndPersistsChange() {
        Path dataFile = this.dataDirectory.resolve("shan.txt");
        Shan shan = new Shan(dataFile);
        shan.initialize();

        shan.executeCommand("todo read book");
        shan.executeCommand("list");
        CommandResult undoResult = shan.executeCommand("undo");

        assertEquals("Got it. I've undone your last command.", undoResult.message());
        assertFalse(undoResult.shouldExit());
        assertFalse(shan.executeCommand("list").message().contains("read book"));

        CommandResult secondUndoResult = shan.executeCommand("undo");
        assertEquals("There is no command to undo.", secondUndoResult.message());

        Shan reloadedShan = new Shan(dataFile);
        reloadedShan.initialize();
        assertFalse(reloadedShan.executeCommand("list").message().contains("read book"));
    }

    @Test
    void executeCommand_undoAfterMark_restoresIncompleteState() {
        Shan shan = new Shan(this.dataDirectory.resolve("shan.txt"));
        shan.initialize();

        shan.executeCommand("todo read book");
        shan.executeCommand("mark 1");
        shan.executeCommand("undo");

        assertTrue(shan.executeCommand("list").message().contains("[T][ ] read book"));
    }

    @Test
    void executeCommand_undoWithoutHistory_returnsErrorWithoutExit() {
        Shan shan = new Shan(this.dataDirectory.resolve("shan.txt"));

        CommandResult result = shan.executeCommand("undo");

        assertEquals("There is no command to undo.", result.message());
        assertFalse(result.shouldExit());
    }
}
