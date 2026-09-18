package bobby;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import bobby.task.TaskList;

/**
 * Tests Bobby's command handling that is shared by the console and GUI front ends.
 */
class BobbyTest {
    private static final Path DATA_FILE = Path.of("data", "bobby.txt");

    @TempDir
    private Path temporaryFolder;

    private Path backupFile;
    private boolean hasOriginalDataFile;

    @BeforeEach
    void backUpDataFile() throws IOException {
        Files.createDirectories(DATA_FILE.getParent());
        hasOriginalDataFile = Files.exists(DATA_FILE);
        if (hasOriginalDataFile) {
            backupFile = temporaryFolder.resolve("bobby-backup.txt");
            Files.copy(DATA_FILE, backupFile, StandardCopyOption.REPLACE_EXISTING);
        }
        Files.deleteIfExists(DATA_FILE);
    }

    @AfterEach
    void restoreDataFile() throws IOException {
        if (hasOriginalDataFile) {
            Files.copy(backupFile, DATA_FILE, StandardCopyOption.REPLACE_EXISTING);
        } else {
            Files.deleteIfExists(DATA_FILE);
        }
    }

    @Test
    void getWelcomeMessage_validSaveFile_returnsGreetingOnly() {
        Bobby bobby = new Bobby();

        assertEquals("Greetings, young one.\nI am Bobby.\n"
                + "Share your task, and we shall bring order to the day.", bobby.getWelcomeMessage());
    }

    @Test
    void getWelcomeMessage_invalidSaveFile_returnsLoadingErrorAndGreeting() throws IOException {
        Files.writeString(DATA_FILE, "bad saved row");

        Bobby bobby = new Bobby();

        assertEquals("The old scroll is clouded: The saved task file contains an invalid task.\n"
                + "Greetings, young one.\nI am Bobby.\n"
                + "Share your task, and we shall bring order to the day.", bobby.getWelcomeMessage());
    }

    @Test
    void isExitCommand_spacesAroundBye_returnsTrue() {
        Bobby bobby = new Bobby();

        assertTrue(bobby.isExitCommand("  bye  "));
        assertFalse(bobby.isExitCommand("goodbye"));
    }

    @Test
    void getResponse_invalidCommand_returnsCommandError() {
        Bobby bobby = new Bobby();

        assertEquals("The path is misty: I don't know what that means yet.",
                bobby.getResponse("nonsense"));
    }

    @Test
    void getResponse_byeCommand_returnsGoodbye() {
        Bobby bobby = new Bobby();

        assertEquals("The pond grows still. Until our paths meet again...", bobby.getResponse(" bye "));
    }

    @Test
    void handleCommand_allSupportedMutatingCommands_returnsResponsesAndSaves() throws BobbyException, IOException {
        TaskList tasks = new TaskList(new ArrayList<>());

        assertEquals("The seed has been planted:\n  [T][ ] read book\nNow 1 tasks grow in the grove.",
                Bobby.handleCommand("todo read book", tasks));
        assertEquals("The seed has been planted:\n  [D][ ] submit report (by: Dec 6 2019)"
                + "\nNow 2 tasks grow in the grove.",
                Bobby.handleCommand("deadline submit report /by 2019-12-06", tasks));
        assertEquals("The seed has been planted:\n  [E][ ] meeting (from: Dec 2 2019, 2:00pm to: "
                + "Dec 2 2019, 4:00pm)\nNow 3 tasks grow in the grove.",
                Bobby.handleCommand("event meeting /from 2019-12-02 1400 /to 2019-12-02 1600", tasks));
        assertEquals("Peace. This task now rests complete:\n  [T][X] read book",
                Bobby.handleCommand("mark 1", tasks));
        assertEquals("Patience. This task returns to the path:\n  [T][ ] read book",
                Bobby.handleCommand("unmark 1", tasks));
        assertEquals("A small mark of meaning is tied to this task:\n"
                + "  [D][ ] submit report (by: Dec 6 2019) #urgent",
                Bobby.handleCommand("tag 2 #urgent", tasks));
        assertEquals("The leaf is released:\n  [T][ ] read book\nNow 2 tasks remain on the branch.",
                Bobby.handleCommand("delete 1", tasks));

        assertEquals(List.of(
                "D | 0 | submit report | 2019-12-06T00:00 | #urgent",
                "E | 0 | meeting | 2019-12-02T14:00 | 2019-12-02T16:00"),
                Files.readAllLines(DATA_FILE));
    }

    @Test
    void handleCommand_listAndFindCommands_returnsFormattedResults() throws BobbyException {
        TaskList tasks = new TaskList(new ArrayList<>());
        Bobby.handleCommand("todo read book", tasks);
        Bobby.handleCommand("todo buy milk", tasks);

        assertEquals("These are the stones upon your path:\n1.[T][ ] read book\n2.[T][ ] buy milk",
                Bobby.handleCommand("list", tasks));
        assertEquals("The pond reflects these matching ripples:\n1.[T][ ] read book",
                Bobby.handleCommand("find book", tasks));
        assertEquals("The pond reflects these matching ripples:",
                Bobby.handleCommand("find chocolate", tasks));
    }
}
