package bibi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests the entry points the GUI uses, which return what Bibi would have said
 * instead of printing it. The console loop is covered by the scripted UI tests
 * in test/ui-test-plan.md, which can drive standard input; these cannot.
 */
public class BibiTest {

    @Test
    public void getResponse_addTodo_confirmsAndCounts(@TempDir Path tempDir) {
        Bibi bibi = new Bibi(tempDir.resolve("bibi.txt"));

        String response = bibi.getResponse("todo borrow book");

        assertTrue(response.contains("Got it. I've added this task:"));
        assertTrue(response.contains("[T][ ] borrow book"));
        assertTrue(response.contains("Now you have 1 tasks in the list."));
    }

    @Test
    public void getResponse_reply_carriesNoSpeakerPrefix(@TempDir Path tempDir) {
        Bibi bibi = new Bibi(tempDir.resolve("bibi.txt"));

        // The window shows Bibi's picture beside every reply, so the console's
        // "Bibi: " prefix would only be noise repeated down the transcript.
        assertFalse(bibi.getResponse("list").contains("Bibi:"));
    }

    @Test
    public void getResponse_unknownCommand_explainsRatherThanThrowing(@TempDir Path tempDir) {
        Bibi bibi = new Bibi(tempDir.resolve("bibi.txt"));

        // The GUI has nowhere useful to send an exception, so a rejected command
        // has to come back as ordinary words.
        assertTrue(bibi.getResponse("blah").contains("I don't understand that command."));
    }

    @Test
    public void getResponse_consecutiveCommands_eachReplyStandsAlone(@TempDir Path tempDir) {
        Bibi bibi = new Bibi(tempDir.resolve("bibi.txt"));
        bibi.getResponse("todo borrow book");

        String response = bibi.getResponse("list");

        // Each call starts a fresh capture, so nothing from the first reply may
        // leak into the second.
        assertFalse(response.contains("Got it."));
        assertTrue(response.contains("1. [T][ ] borrow book"));
    }

    @Test
    public void getResponse_changingCommand_writesTheSaveFile(@TempDir Path tempDir)
            throws IOException {
        Path saveFile = tempDir.resolve("bibi.txt");

        new Bibi(saveFile).getResponse("todo borrow book");

        assertEquals("T | 0 | borrow book", Files.readString(saveFile).strip());
    }

    @Test
    public void isExitRequested_beforeAndAfterBye_reportsTheSession(@TempDir Path tempDir) {
        Bibi bibi = new Bibi(tempDir.resolve("bibi.txt"));
        bibi.getResponse("list");

        assertFalse(bibi.isExitRequested());

        String farewell = bibi.getResponse("bye");

        assertTrue(farewell.contains("Goodbye!"));
        assertTrue(bibi.isExitRequested());
    }

    @Test
    public void getGreeting_noSaveFile_welcomesWithoutTheBanner(@TempDir Path tempDir) {
        Bibi bibi = new Bibi(tempDir.resolve("bibi.txt"));

        String greeting = bibi.getGreeting();

        assertTrue(greeting.contains("Enter todo <description>"));
        // The banner is drawn out of spaced letters and only lines up in a
        // fixed-width font, so the GUI leaves it to the console.
        assertFalse(greeting.contains("B B B B"));
        assertFalse(greeting.contains("Loaded"));
    }

    @Test
    public void getGreeting_existingSaveFile_reportsWhatWasRestored(@TempDir Path tempDir)
            throws IOException {
        Path saveFile = tempDir.resolve("bibi.txt");
        Files.writeString(saveFile, "T | 0 | borrow book\nT | 1 | return book\n");

        String greeting = new Bibi(saveFile).getGreeting();

        assertTrue(greeting.contains("Loaded 2 saved task(s)."));
    }

    @Test
    public void getGreeting_damagedSaveFile_warnsAndKeepsGoing(@TempDir Path tempDir)
            throws IOException {
        Path saveFile = tempDir.resolve("bibi.txt");
        Files.writeString(saveFile, "T | 0 | borrow book\nnonsense\n");

        Bibi bibi = new Bibi(saveFile);
        String greeting = bibi.getGreeting();

        assertTrue(greeting.contains("I had trouble reading part of your save file:"));
        assertTrue(bibi.getResponse("list").contains("1. [T][ ] borrow book"));
    }
}
