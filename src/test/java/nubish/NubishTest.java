package nubish;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests the non-GUI Nubish application facade.
 */
public class NubishTest {
    @TempDir
    private Path temporaryDirectory;

    /**
     * Verifies that startup returns the greeting for a clean save file.
     */
    @Test
    public void start_missingSaveFile_returnsGreeting() {
        Nubish nubish = new Nubish(temporaryDirectory.resolve("nubish.txt").toString());

        String response = nubish.start();

        assertTrue(response.contains("Wazzup! I'm Nubish."));
        assertFalse(nubish.isLastResponseError());
    }

    /**
     * Verifies that startup includes a load error when saved data is malformed.
     */
    @Test
    public void start_malformedSaveFile_returnsLoadErrorAndGreeting() throws IOException {
        Path saveFile = temporaryDirectory.resolve("nubish.txt");
        Files.writeString(saveFile, "T | 2 | read book\n");
        Nubish nubish = new Nubish(saveFile.toString());

        String response = nubish.start();

        assertTrue(response.contains("error loading your data"));
        assertTrue(response.contains("Wazzup! I'm Nubish."));
        assertTrue(nubish.isLastResponseError());
    }

    /**
     * Verifies that getResponse executes commands and exposes the latest response.
     */
    @Test
    public void getResponse_validTodo_updatesTaskList() {
        Nubish nubish = new Nubish(temporaryDirectory.resolve("nubish.txt").toString());
        nubish.start();

        String response = nubish.getResponse("todo read book");

        assertTrue(response.contains("todo task added"));
        assertEquals("1. [T][ ] read book\n\n", nubish.getTaskList());
        assertFalse(nubish.isLastResponseError());
    }

    /**
     * Verifies that reminders include tasks due today from the save file.
     */
    @Test
    public void remind_deadlineDueToday_returnsReminderMessage() throws IOException {
        Path saveFile = temporaryDirectory.resolve("nubish.txt");
        String today = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/uuuu"));
        Files.writeString(saveFile, "D | 0 | submit assignment | " + today + " 1800\n");
        Nubish nubish = new Nubish(saveFile.toString());
        nubish.start();

        String response = nubish.remind();

        assertTrue(response.contains("REMINDER! These tasks are due today"));
        assertTrue(response.contains("submit assignment"));
    }

    /**
     * Verifies that bye saves the current task list to disk.
     */
    @Test
    public void getResponse_byeCommand_savesTasks() throws IOException {
        Path saveFile = temporaryDirectory.resolve("nubish.txt");
        Nubish nubish = new Nubish(saveFile.toString());
        nubish.start();
        nubish.getResponse("todo read book");

        nubish.getResponse("bye");

        assertEquals("T | 0 | read book\n", Files.readString(saveFile));
    }

    /**
     * Verifies that explicit save writes the current task list to disk.
     */
    @Test
    public void save_afterAddingTask_writesTasks() throws IOException {
        Path saveFile = temporaryDirectory.resolve("nubish.txt");
        Nubish nubish = new Nubish(saveFile.toString());
        nubish.start();
        nubish.getResponse("todo read book");

        nubish.save();

        assertEquals("T | 0 | read book\n", Files.readString(saveFile));
    }
}
