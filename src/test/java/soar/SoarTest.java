package soar;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import soar.storage.Storage;

/**
 * Tests the command-response boundary shared by the JavaFX and console interfaces.
 */
public class SoarTest {
    private Path testDirectory;

    /** Creates an isolated relative storage directory. */
    @BeforeEach
    public void setUp() throws IOException {
        Path testRoot = Path.of("build", "junit-soar");
        Files.createDirectories(testRoot);
        testDirectory = Files.createTempDirectory(testRoot, "case-");
    }

    /** Removes generated task data. */
    @AfterEach
    public void tearDown() throws IOException {
        if (testDirectory == null || !Files.exists(testDirectory)) {
            return;
        }
        try (var paths = Files.walk(testDirectory)) {
            for (Path path : paths.sorted(Comparator.reverseOrder()).toList()) {
                Files.deleteIfExists(path);
            }
        }
    }

    /** Verifies GUI responses use command behavior without console separators. */
    @Test
    public void getResponse_commandsAndErrors_returnsUnframedMessages() {
        Soar soar = createSoar();

        assertEquals("Got it. I've added this task:\n"
                + "  [T][ ] read book\n"
                + "Now you have 1 tasks in the list.", soar.getResponse("todo read book"));
        assertEquals("Here are the tasks in your list:\n"
                + "1.[T][ ] read book", soar.getResponse("list"));
        assertEquals("That command is on an unfamiliar flight path. Try list, find, date, todo, deadline, "
                + "event, mark, unmark, delete, edit, or bye to keep flying high!",
                soar.getResponse("unknown"));
    }

    /** Verifies edit responses, completion preservation, no-op handling, and date matching. */
    @Test
    public void getResponse_editEvent_returnsExactMessagesAndUpdatesDateQuery() {
        Soar soar = createSoar();
        soar.getResponse("event meeting /from 2pm /to 4pm");
        soar.getResponse("mark 1");

        assertEquals("I've updated this task:\n"
                + "  Before: [E][X] meeting (from: 2pm to: 4pm)\n"
                + "  After:  [E][X] workshop (from: 2pm to: 2026-09-20 17:00)",
                soar.getResponse("edit 1 /to 2026-09-20 17:00 /description workshop"));
        assertEquals("Here are the deadlines and events on Sep 20 2026:\n"
                + "1.[E][X] workshop (from: 2pm to: 2026-09-20 17:00)",
                soar.getResponse("date 2026-09-20"));
        assertEquals("That task already has those details:\n"
                + "  [E][X] workshop (from: 2pm to: 2026-09-20 17:00)",
                soar.getResponse("edit 1 /description workshop"));
    }

    /** Verifies GUI commands persist state for a later Soar session. */
    @Test
    public void getResponse_mutatingCommands_persistsForNextSession() {
        Soar firstSession = createSoar();
        firstSession.getResponse("todo persistent task");
        firstSession.getResponse("mark 1");
        firstSession.getResponse("edit 1 /description edited persistent task");

        Soar nextSession = createSoar();

        assertEquals("Here are the tasks in your list:\n"
                + "1.[T][X] edited persistent task", nextSession.getResponse("list"));
    }

    /** Creates a Soar session using this test's isolated data file. */
    private Soar createSoar() {
        return new Soar(new Storage(testDirectory.resolve("tasks.txt")));
    }
}
