package victoria;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class VictoriaTest {
    private static final Path DATA_FILE = Path.of("data", "victoria.txt");

    private byte[] originalFile;

    @BeforeEach
    void isolateSavedTasks() throws IOException {
        originalFile = Files.exists(DATA_FILE) ? Files.readAllBytes(DATA_FILE) : null;
        Files.deleteIfExists(DATA_FILE);
    }

    @AfterEach
    void restoreSavedTasks() throws IOException {
        if (originalFile == null) {
            Files.deleteIfExists(DATA_FILE);
        } else {
            Files.write(DATA_FILE, originalFile);
        }
    }

    @Test
    void executeCommand_taskLifecycleAndQueries_returnsExpectedReplies() {
        Victoria victoria = new Victoria();

        assertTrue(victoria.executeCommand("todo buy milk").response().contains("[T][ ] buy milk"));
        assertTrue(victoria.executeCommand("deadline submit report /by 2026-09-12").response()
                .contains("[D][ ] submit report (by: Sep 12 2026)"));
        assertTrue(victoria.executeCommand("event conference /from 2026-09-11 /to 2026-09-13").response()
                .contains("[E][ ] conference (from: Sep 11 2026 to: Sep 13 2026)"));
        assertTrue(victoria.executeCommand("find MILK").response().contains("1.[T][ ] buy milk"));
        assertTrue(victoria.executeCommand("list on 2026-09-12").response().contains("2.[D][ ] submit report"));
        assertTrue(victoria.executeCommand("mark 1").response().contains("[T][X] buy milk"));
        assertTrue(victoria.executeCommand("unmark 1").response().contains("[T][ ] buy milk"));
        assertTrue(victoria.executeCommand("delete 2").response().contains("submit report"));
        assertTrue(victoria.executeCommand("list").response().contains("2.[E][ ] conference"));
    }

    @Test
    void executeCommand_invalidInputAndRepeatedStatus_reportsErrorsWithoutExiting() {
        Victoria victoria = new Victoria();
        victoria.executeCommand("todo buy milk");
        victoria.executeCommand("mark 1");

        assertTrue(victoria.executeCommand("mark 1").response().contains("already finished"));
        assertTrue(victoria.executeCommand("unmark 2").response().contains("can't find a task"));
        assertTrue(victoria.executeCommand("find ").response().contains("I didn't catch that command"));
        assertTrue(victoria.executeCommand("list on tomorrow").response().contains("yyyy-MM-dd"));
        assertTrue(victoria.executeCommand("deadline task").response().contains("Add /by <date>"));
        assertTrue(victoria.executeCommand("event task /from 2026-09-12").response().contains("Add /to <end date>"));
        assertTrue(victoria.executeCommand("unknown").response().contains("I didn't catch that command"));
        assertFalse(victoria.executeCommand("list").shouldExit());
    }

    @Test
    void executeCommand_bye_returnsFarewellAndEndsSession() {
        Victoria victoria = new Victoria();

        Victoria.CommandResult result = victoria.executeCommand("bye");

        assertTrue(result.shouldExit());
        assertTrue(result.response().contains("That's all for now"));
    }
}
