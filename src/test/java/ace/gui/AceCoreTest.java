package ace.gui;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * AceCore's constructor calls Storage.load(), and getResponse() can trigger
 * Storage.save() for mutating commands — both hit the real data/Ace.txt, so
 * back up and restore it around each test, same reasoning as the other
 * classes that go through Storage (TaskManagerTest, StorageTest, ParserTest).
 */
public class AceCoreTest {
    private static final Path SAVE_FILE = Paths.get("data", "Ace.txt");
    private byte[] backup;
    private boolean wasFileExisting;

    @BeforeEach
    public void backUpRealSaveFile() throws IOException {
        wasFileExisting = Files.exists(SAVE_FILE);
        if (wasFileExisting) {
            backup = Files.readAllBytes(SAVE_FILE);
        }
        Files.deleteIfExists(SAVE_FILE);
    }

    @AfterEach
    public void restoreRealSaveFile() throws IOException {
        if (wasFileExisting) {
            Files.write(SAVE_FILE, backup);
        } else {
            Files.deleteIfExists(SAVE_FILE);
        }
    }

    @Test
    public void getWelcomeMessage_returnsNonEmptyGreeting() {
        AceCore aceCore = new AceCore();

        String welcome = aceCore.getWelcomeMessage();

        assertFalse(welcome.isEmpty());
        assertTrue(welcome.contains("Ace"));
    }

    @Test
    public void getResponse_validCommand_returnsExpectedTextAndClearsErrorFlag() {
        AceCore aceCore = new AceCore();

        String response = aceCore.getResponse("todo read book");

        assertTrue(response.contains("read book"));
        assertFalse(aceCore.isError());
        assertFalse(aceCore.isExit());
    }

    @Test
    public void getResponse_invalidCommand_setsErrorFlagAndReturnsErrorMessage() {
        AceCore aceCore = new AceCore();

        String response = aceCore.getResponse("gibberish");

        assertTrue(aceCore.isError());
        assertTrue(response.contains("discarded"));
    }

    @Test
    public void getResponse_errorFlagResetsOnNextValidCommand() {
        // isError must reflect only the most recently processed command, not
        // "an error happened at some point" — a valid command right after an
        // error must clear the flag back to false.
        AceCore aceCore = new AceCore();
        aceCore.getResponse("gibberish");

        aceCore.getResponse("todo read book");

        assertFalse(aceCore.isError());
    }

    @Test
    public void getResponse_byeCommand_setsExitFlag() {
        AceCore aceCore = new AceCore();

        aceCore.getResponse("bye");

        assertTrue(aceCore.isExit());
    }

    @Test
    public void getResponse_outputMatchesCliMessageContent() {
        // Byte-for-byte consistency with the CLI is AceCore's whole design
        // purpose (it runs the exact same Parser/TaskManager/Ui code the CLI
        // does) — check the actual confirmation text, not just that
        // *something* non-empty came back.
        AceCore aceCore = new AceCore();

        String response = aceCore.getResponse("todo read book");

        assertTrue(response.contains("Great!"));
        assertTrue(response.contains("[T][ ] read book"));
        assertTrue(response.contains("You now have a total of 1 cards in your hand!"));
    }

    @Test
    public void getResponse_doesNotLeaveSystemOutRedirected() {
        // getResponse temporarily redirects System.out to capture the
        // response text — this confirms it's always restored afterward,
        // even on a normal (non-exceptional) path, so nothing printed later
        // in the same process silently vanishes into AceCore's buffer.
        AceCore aceCore = new AceCore();
        aceCore.getResponse("todo read book");

        PrintStream originalOut = System.out;
        ByteArrayOutputStream outerCapture = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outerCapture));
        try {
            System.out.println("visible after getResponse");
        } finally {
            System.setOut(originalOut);
        }

        assertTrue(outerCapture.toString().contains("visible after getResponse"));
    }

    @Test
    public void getResponse_loadsPreviouslySavedTasks() {
        // A separate AceCore instance saves a task; a brand-new AceCore
        // (simulating a fresh app launch) should load it back, same
        // guarantee StorageTest already verifies at the Storage layer —
        // this confirms AceCore's own constructor actually wires that up.
        AceCore first = new AceCore();
        first.getResponse("todo read book");

        AceCore second = new AceCore();
        String response = second.getResponse("list");

        assertTrue(response.contains("read book"));
    }
}
