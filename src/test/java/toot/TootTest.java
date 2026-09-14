package toot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests the response API shared by the graphical interface.
 */
public class TootTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void getResponse_sequenceOfCommands_updatesStateAndFormatsResponses() {
        Toot toot = new Toot(temporaryDirectory.resolve("toot.txt"));

        assertEquals("Toot addeded:\n"
                + "  [T][ ] read book\n"
                + "Toot has 1 task in the list now! (｡•̀ᴗ-)✧",
                toot.getResponse("todo read book"));
        assertEquals("Here are the tasks in your list:\n"
                + "1.[T][ ] read book",
                toot.getResponse("list"));
        assertEquals("Otay bye-bye! Toot go eepy now... zZz (｡-ω-)ﾉ",
                toot.getResponse("bye"));
    }
    @Test
    public void getResponse_help_preservesTasksAndDoesNotCreateStorage() {
        Path dataFile = temporaryDirectory.resolve("help.txt");
        Toot toot = new Toot(dataFile);
        assertTrue(toot.getResponse("help").startsWith("Toot command guide:"));
        assertFalse(Files.exists(dataFile));
        toot.getResponse("todo read book");
        String beforeHelp = toot.getResponse("list");
        toot.getResponse("help");
        assertEquals(beforeHelp, toot.getResponse("list"));
        assertTrue(toot.getResponse("help extra").startsWith("Oh crumbs!"));
    }

    @Test
    public void getResponse_invalidSavedData_doesNotOverwriteOriginalFile() throws Exception {
        Path dataFile = temporaryDirectory.resolve("invalid.txt");
        String originalData = "D | 0 | broken | invalid-date\n";
        Files.writeString(dataFile, originalData);
        Toot toot = new Toot(dataFile);

        assertTrue(toot.getStartupMessage().contains("Saved task data is invalid on line 1."));
        assertEquals("Oh crumbs! Saved tasks could not be loaded. Fix or back up the data file and restart "
                + "Toot before changing tasks.", toot.getResponse("todo read book"));
        assertEquals(originalData, Files.readString(dataFile));
    }
}
