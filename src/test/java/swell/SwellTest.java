package swell;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import swell.storage.Storage;

public class SwellTest {
    @TempDir
    private Path tempDir;

    @Test
    public void getResponse_addTodoThenList_listsAddedTodo() {
        Swell swell = new Swell(new Storage(getDataFile()));

        String addResponse = swell.getResponse("todo read book");
        String listResponse = swell.getResponse("list");

        assertTrue(addResponse.contains("[T][ ] read book"));
        assertTrue(listResponse.contains("1. [T][ ] read book"));
    }

    @Test
    public void getResponse_invalidCommand_returnsFormattedError() {
        Swell swell = new Swell(new Storage(getDataFile()));

        String response = swell.getResponse("hello");

        assertTrue(response.startsWith("Oops!"));
        assertTrue(response.contains("Try todo, deadline, event, list"));
    }

    @Test
    public void getResponse_commandWithLeadingAndTrailingSpaces_processesCommand() {
        Swell swell = new Swell(new Storage(getDataFile()));

        String response = swell.getResponse("   todo read book   ");

        assertTrue(response.contains("[T][ ] read book"));
    }

    @Test
    public void getResponse_byeCommand_setsExit() {
        Swell swell = new Swell(new Storage(getDataFile()));

        assertFalse(swell.isExit());
        String response = swell.getResponse("bye");

        assertTrue(swell.isExit());
        assertTrue(response.contains("Docking for now."));
    }

    private Path getDataFile() {
        return tempDir.resolve("data").resolve("swell.txt");
    }
}
