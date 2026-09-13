package nelson;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import nelson.exception.NelsonException;
import nelson.storage.Storage;
import nelson.ui.Ui;

/** Tests Nelson's command execution without using the real storage file. */
public class NelsonTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void processCommand_addAndListTasks_persistsAndDisplaysTasks() throws Exception {
        Path taskFile = temporaryDirectory.resolve("tasks.txt");
        RecordingUi ui = new RecordingUi();
        Nelson nelson = new Nelson(ui, new Storage(taskFile));

        nelson.processCommand("todo study openings");
        nelson.processCommand("deadline submit iP /by 2026-09-18");
        nelson.processCommand("list");

        assertEquals(List.of(
                "T | 0 | study openings",
                "D | 0 | submit iP | 2026-09-18"), Files.readAllLines(taskFile));
        assertEquals("1.[T][ ] study openings", ui.messages.get(7));
        assertEquals("2.[D][ ] submit iP (by: Sep 18 2026)", ui.messages.get(8));
    }

    @Test
    public void processCommand_taskLifecycle_updatesStoredState() throws Exception {
        Path taskFile = temporaryDirectory.resolve("tasks.txt");
        Nelson nelson = new Nelson(new RecordingUi(), new Storage(taskFile));

        nelson.processCommand("todo calculate variations");
        nelson.processCommand("mark 1");
        assertEquals(List.of("T | 1 | calculate variations"), Files.readAllLines(taskFile));

        nelson.processCommand("unmark 1");
        assertEquals(List.of("T | 0 | calculate variations"), Files.readAllLines(taskFile));

        nelson.processCommand("delete 1");
        assertEquals(List.of(), Files.readAllLines(taskFile));
    }

    @Test
    public void processCommand_invalidCommand_throwsNelsonException() {
        Nelson nelson = new Nelson(
                new RecordingUi(), new Storage(temporaryDirectory.resolve("tasks.txt")));

        assertThrows(NelsonException.class, () -> nelson.processCommand("castle queenside"));
    }

    /** Records normal Nelson output for assertions. */
    private static class RecordingUi extends Ui {
        private final ArrayList<String> messages = new ArrayList<>();

        @Override
        public void show(String... output) {
            messages.addAll(List.of(output));
        }
    }
}
