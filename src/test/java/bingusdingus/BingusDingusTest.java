package bingusdingus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Tests command execution and one-step undo behavior. */
class BingusDingusTest {
    private static final Path STORAGE_PATH = Path.of(".", "data", "bingusdingus.txt");
    private byte[] originalStorage;
    private boolean wasStoragePresent;

    @BeforeEach
    void isolateStorage() throws IOException {
        wasStoragePresent = Files.exists(STORAGE_PATH);
        originalStorage = wasStoragePresent ? Files.readAllBytes(STORAGE_PATH) : null;
        Files.deleteIfExists(STORAGE_PATH);
    }

    @AfterEach
    void restoreStorage() throws IOException {
        if (wasStoragePresent) {
            Files.createDirectories(STORAGE_PATH.getParent());
            Files.write(STORAGE_PATH, originalStorage);
        } else {
            Files.deleteIfExists(STORAGE_PATH);
        }
    }

    @Test
    void undo_afterAdd_removesTheAddedTaskAndClearsHistory() throws IOException {
        BingusDingus bingusDingus = new BingusDingus();

        bingusDingus.getResponse("todo buy milk");

        assertEquals("I've undone your last command.", bingusDingus.getResponse("undo"));
        assertEquals("Here are the tasks in your list:", bingusDingus.getResponse("list"));
        assertEquals("There is nothing to undo.", bingusDingus.getResponse("undo"));
        assertEquals(List.of(), Files.readAllLines(STORAGE_PATH, StandardCharsets.UTF_8));
    }

    @Test
    void undo_afterDelete_restoresTheTaskAtItsOriginalPosition() {
        BingusDingus bingusDingus = new BingusDingus();

        bingusDingus.getResponse("todo first task");
        bingusDingus.getResponse("todo second task");
        bingusDingus.getResponse("delete 1");
        bingusDingus.getResponse("undo");

        assertEquals("Here are the tasks in your list:\n"
                        + "1. [T][ ] first task\n"
                        + "2. [T][ ] second task",
                bingusDingus.getResponse("list"));
    }

    @Test
    void undo_afterMark_restoresThePreviousCompletionState() {
        BingusDingus bingusDingus = new BingusDingus();

        bingusDingus.getResponse("todo buy milk");
        bingusDingus.getResponse("mark 1");
        bingusDingus.getResponse("undo");

        assertEquals("Here are the tasks in your list:\n1. [T][ ] buy milk",
                bingusDingus.getResponse("list"));
    }

    @Test
    void undo_afterUnmark_restoresThePreviousCompletionState() {
        BingusDingus bingusDingus = new BingusDingus();

        bingusDingus.getResponse("todo buy milk");
        bingusDingus.getResponse("mark 1");
        bingusDingus.getResponse("unmark 1");
        bingusDingus.getResponse("undo");

        assertEquals("Here are the tasks in your list:\n1. [T][X] buy milk",
                bingusDingus.getResponse("list"));
    }

    @Test
    void failedCommand_doesNotReplacePreviousUndoAction() {
        BingusDingus bingusDingus = new BingusDingus();

        bingusDingus.getResponse("todo buy milk");
        bingusDingus.getResponse("unknown command");

        assertEquals("I've undone your last command.", bingusDingus.getResponse("undo"));
        assertEquals("Here are the tasks in your list:", bingusDingus.getResponse("list"));
    }

    @Test
    void byeCommand_returnsGoodbyeResponseAndIsRecognizedAsExitCommand() {
        BingusDingus bingusDingus = new BingusDingus();

        assertEquals("Goodbye!", bingusDingus.getResponse("bye"));
        assertTrue(bingusDingus.isByeCommand("bye"));
    }
}
