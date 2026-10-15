package bern.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.junit.jupiter.api.parallel.Resources;

import bern.task.Task;

/**
 * Verifies that invalid saved records are reported without preventing later tasks from loading.
 */
@ResourceLock(Resources.SYSTEM_PROPERTIES)
@ResourceLock(Resources.SYSTEM_OUT)
public class SaveDataControllerTest {
    @Test
    public void readTaskData_invalidDatesAndTimes_loadsFollowingTask(@TempDir Path workingDirectory)
            throws IOException {
        List<String> invalidRecords = List.of(
                String.join(SaveDataController.SEPARATOR, "D", "0", "Invalid date", "not-a-date"),
                String.join(SaveDataController.SEPARATOR, "E", "0", "Invalid time",
                        "15/9/2026 @ 09:00", "15/9/2026 @ 25:00"));

        assertSkipsInvalidRecords(workingDirectory, invalidRecords);
    }

    @Test
    public void readTaskData_malformedFields_loadsFollowingTask(@TempDir Path workingDirectory) throws IOException {
        List<String> invalidRecords = List.of(
                String.join(SaveDataController.SEPARATOR, "unknown", "0", "Invalid type"),
                String.join(SaveDataController.SEPARATOR, "T", "invalid", "Invalid completion"),
                String.join(SaveDataController.SEPARATOR, "D", "0", "Missing deadline"));

        assertSkipsInvalidRecords(workingDirectory, invalidRecords);
    }

    /**
     * Verifies that the save-file reader skips and reports invalid records while loading a following valid task.
     * Restores the working-directory property and console output after using an isolated save directory.
     *
     * @param workingDirectory The temporary working directory in which to create the save file.
     * @param invalidRecords The malformed records to place before the valid task.
     * @throws IOException If creating the temporary save directory or writing the test records fails.
     */
    private void assertSkipsInvalidRecords(Path workingDirectory, List<String> invalidRecords) throws IOException {
        Path saveDirectory = Files.createDirectories(workingDirectory.resolve("data"));
        List<String> records = new ArrayList<>(invalidRecords);
        records.add(String.join(SaveDataController.SEPARATOR, "T", "1", "Following task"));
        Files.write(saveDirectory.resolve("tasks.txt"), records);

        String originalDirectory = System.getProperty("user.dir");
        PrintStream originalOutput = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (PrintStream capturedOutput = new PrintStream(output, true, StandardCharsets.UTF_8)) {
            System.setProperty("user.dir", workingDirectory.toString());
            System.setOut(capturedOutput);

            List<Task> tasks = SaveDataController.readTaskData();
            assertEquals(1, tasks.size());
            assertEquals("Following task", tasks.getFirst().getName());
            assertTrue(tasks.getFirst().isDone());
            assertTrue(output.toString(StandardCharsets.UTF_8).contains("Some tasks were unable to be retrieved."),
                    "The reader must report the invalid records instead of silently discarding them");
        } finally {
            System.setProperty("user.dir", originalDirectory);
            System.setOut(originalOutput);
        }
    }
}
