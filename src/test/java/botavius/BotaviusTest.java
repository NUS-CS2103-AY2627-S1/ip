package botavius;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import botavius.tasklist.TaskList;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests the application coordinator's startup and session lifecycle. */
class BotaviusTest {
    private final PrintStream originalOut = System.out;
    private final InputStream originalIn = System.in;

    @AfterEach
    void restoreStandardStreams() {
        System.setOut(originalOut);
        System.setIn(originalIn);
    }

    /** Verifies that startup loads persisted tasks without losing their contents. */
    @Test
    void constructor_existingSaveFile_loadsPersistedTask(@TempDir Path temporaryDirectory)
            throws Exception {
        Path saveFile = temporaryDirectory.resolve("save.txt");
        Files.writeString(saveFile, "[T][ ] buy milk\n", StandardCharsets.UTF_8);

        new Botavius(saveFile.toString());

        assertTrue(TaskList.getTaskStrings().contains("buy milk"));
    }

    /** Verifies that malformed persisted data starts a clean session. */
    @Test
    void constructor_invalidSaveFile_startsWithEmptyTaskList(@TempDir Path temporaryDirectory)
            throws Exception {
        Path saveFile = temporaryDirectory.resolve("save.txt");
        Files.writeString(saveFile, "[D][ ] submit report by:\n", StandardCharsets.UTF_8);

        new Botavius(saveFile.toString());

        assertEquals("", TaskList.getTaskStrings());
    }

    /** Verifies that a session containing only bye exits and prints a goodbye message. */
    @Test
    void run_byeCommand_endsSessionWithGoodbye(@TempDir Path temporaryDirectory) throws Exception {
        Path saveFile = temporaryDirectory.resolve("save.txt");
        Files.writeString(saveFile, "[T][ ] buy milk\n", StandardCharsets.UTF_8);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
        System.setIn(new ByteArrayInputStream("bye\n".getBytes(StandardCharsets.UTF_8)));

        new Botavius(saveFile.toString());
        Botavius.run();

        assertTrue(output.toString(StandardCharsets.UTF_8)
                .contains("Bye. Hope to see you again soon!"));
    }
}
