package lebron;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import lebron.storage.Storage;

public class LeBronTest {
    @TempDir
    Path tempDir;

    private LeBron newLeBron() {
        return new LeBron(new Storage(tempDir.resolve("LeBron.txt")));
    }

    @Test
    public void constructor_noSaveFileYet_startsWithEmptyTaskList() {
        LeBron leBron = newLeBron();
        assertEquals("", leBron.getResponse("list"));
    }

    @Test
    public void constructor_corruptedSaveFile_fallsBackToEmptyTaskListInsteadOfCrashing() throws IOException {
        Path filePath = tempDir.resolve("LeBron.txt");
        Files.writeString(filePath, "X | 0 | mystery task\n");

        LeBron leBron = new LeBron(new Storage(filePath));

        assertEquals("", leBron.getResponse("list"));
    }

    @Test
    public void getResponse_validCommand_addsTaskAndReturnsConfirmation() {
        LeBron leBron = newLeBron();

        String response = leBron.getResponse("todo read book");

        assertTrue(response.contains("[T][ ] read book"));
    }

    @Test
    public void getResponse_validCommand_savesToStorage() {
        LeBron leBron = newLeBron();
        leBron.getResponse("todo read book");

        LeBron reloaded = newLeBron();

        assertEquals("1. [T][ ] read book\n", reloaded.getResponse("list"));
    }

    @Test
    public void getResponse_leBronException_returnsErrorMessageInsteadOfThrowing() {
        LeBron leBron = newLeBron();

        String response = leBron.getResponse("todo");

        assertEquals("Whatchu tryna to do?", response);
    }

    @Test
    public void getResponse_malformedDate_returnsDateErrorMessage() {
        LeBron leBron = newLeBron();

        String response = leBron.getResponse("deadline return book by not-a-date");

        assertTrue(response.contains("That date don't look right"));
    }

    @Test
    public void getResponse_saveFails_appendsSaveErrorToCommandsOwnReply() throws IOException {
        Path blockerFile = tempDir.resolve("blocker");
        Files.writeString(blockerFile, "not a directory");
        // Resolving a save path under a regular file (not a directory) makes
        // Storage.save()'s Files.createDirectories() fail, simulating a real
        // "can't write to disk" environment error without touching OS permissions.
        LeBron leBron = new LeBron(new Storage(blockerFile.resolve("nested").resolve("LeBron.txt")));

        String response = leBron.getResponse("todo read book");

        assertTrue(response.contains("[T][ ] read book"));
        assertTrue(response.contains("Couldn't save your grind list."));
    }

    @Test
    public void getResponse_byeCommand_isExitBecomesTrue() {
        LeBron leBron = newLeBron();
        assertFalse(leBron.isExit());

        leBron.getResponse("bye");

        assertTrue(leBron.isExit());
    }

    @Test
    public void getResponse_nonExitCommand_isExitStaysFalse() {
        LeBron leBron = newLeBron();

        leBron.getResponse("todo read book");

        assertFalse(leBron.isExit());
    }

    @Test
    public void getGreeting_returnsBanner() {
        LeBron leBron = newLeBron();
        assertEquals("What's up, I am LeBron.\nCan the king assist you in any way?", leBron.getGreeting());
    }
}
