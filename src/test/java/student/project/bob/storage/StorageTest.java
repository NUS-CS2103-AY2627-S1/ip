package student.project.bob.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import student.project.bob.model.Deadline;
import student.project.bob.model.Event;
import student.project.bob.model.Task;
import student.project.bob.model.Todo;

/**
 * Tests saving and loading tasks from isolated storage files.
 */
public class StorageTest {
    @TempDir
    private Path temporaryDirectory;

    private String dataFilePath = Path.of("data", "Bob.txt").toString();

    /**
     * Verifies that all task types and completion states survive a storage round trip.
     */
    @Test
    public void saveAndLoad_mixedTasks_preservesTaskData() throws Exception {
        String output = runStorageProbe("roundTrip");

        assertEquals(
                String.join(
                        System.lineSeparator(),
                        encodeUtf8("[T][ ] buy café") + "|false",
                        encodeUtf8("[D][X] submit report (by: Tue, Oct 15 2019)") + "|true",
                        encodeUtf8("[E][ ] team sync (from: Wed, Oct 16 2019 09:00 to: Wed, Oct 16 2019 10:00)")
                                + "|false"),
                output);
    }

    /**
     * Verifies that loading a missing data file returns an empty list.
     */
    @Test
    public void loadTasks_missingFile_returnsEmptyList() throws Exception {
        assertEquals("0", runStorageProbe("missingFile"));
    }

    /**
     * Verifies that malformed saved data is reported as a storage exception.
     */
    @Test
    public void loadTasks_malformedFile_throwsStorageException() throws Exception {
        String output = runStorageProbe("malformedFile");

        assertTrue(output.startsWith("StorageException: Could not load tasks from " + dataFilePath + "."));
    }

    /**
     * Verifies that saving replaces the previous file contents using UTF-8.
     */
    @Test
    public void saveTasks_existingFile_replacesContents() throws Exception {
        assertEquals(encodeUtf8("[T][ ] new café" + System.lineSeparator()), runStorageProbe("overwriteFile"));
    }

    /**
     * Verifies that an unwritable task-file path is reported as a storage exception.
     */
    @Test
    public void saveTasks_directoryAtFilePath_throwsStorageException() throws Exception {
        String output = runStorageProbe("saveFailure");

        assertTrue(output.startsWith("StorageException: Could not save tasks to " + dataFilePath + "."));
    }

    /**
     * Encodes text as Base64 so child-process output remains independent of the console encoding.
     */
    private String encodeUtf8(String text) {
        return Base64.getEncoder().encodeToString(text.getBytes(StandardCharsets.UTF_8));
    }

    private String runStorageProbe(String scenario) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder(
                javaExecutable(), "-cp", absoluteClassPath(), StorageProbe.class.getName(), scenario);
        processBuilder.directory(temporaryDirectory.toFile());
        processBuilder.redirectErrorStream(true);

        Process process = processBuilder.start();
        String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8).trim();
        int exitCode = process.waitFor();

        assertEquals(0, exitCode, output);
        return output;
    }

    private String javaExecutable() {
        return Path.of(System.getProperty("java.home"), "bin", "java").toString();
    }

    private String absoluteClassPath() {
        return Arrays.stream(System.getProperty("java.class.path").split(File.pathSeparator))
                .map(path -> Path.of(path).toAbsolutePath().normalize().toString())
                .collect(Collectors.joining(File.pathSeparator));
    }

    /**
     * Runs isolated storage scenarios for the JUnit process.
     */
    public static class StorageProbe {
        /**
         * Executes the selected storage scenario.
         *
         * @param args scenario name
         * @throws Exception if the scenario cannot access its temporary file
         */
        public static void main(String[] args) throws Exception {
            switch (args[0]) {
                case "roundTrip" -> roundTrip();
                case "missingFile" -> System.out.print(Storage.loadTasks().size());
                case "malformedFile" -> malformedFile();
                case "overwriteFile" -> overwriteFile();
                case "saveFailure" -> saveFailure();
                default -> throw new IllegalArgumentException("Unknown scenario: " + args[0]);
            }
        }

        private static void roundTrip() throws StorageException {
            Task completedDeadline = new Deadline("submit report", "2019-10-15");
            completedDeadline.setDone();
            List<Task> tasks = List.of(
                    new Todo("buy café"),
                    completedDeadline,
                    new Event("team sync", "2019-10-16T09:00", "2019-10-16T10:00"));

            Storage.saveTasks(tasks);
            for (Task task : Storage.loadTasks()) {
                System.out.println(
                        Base64.getEncoder().encodeToString(task.toString().getBytes(StandardCharsets.UTF_8))
                                + "|"
                                + task.isDone());
            }
        }

        private static void malformedFile() throws IOException {
            Files.createDirectories(Path.of("data"));
            Files.writeString(Path.of("data", "Bob.txt"), "not a valid task line", StandardCharsets.UTF_8);

            try {
                Storage.loadTasks();
            } catch (StorageException exception) {
                System.out.print("StorageException: " + exception.getMessage());
            }
        }

        private static void overwriteFile() throws StorageException, IOException {
            Files.createDirectories(Path.of("data"));
            Files.writeString(Path.of("data", "Bob.txt"), "old contents", StandardCharsets.UTF_8);

            Storage.saveTasks(List.of(new Todo("new café")));
            System.out.print(Base64.getEncoder().encodeToString(Files.readAllBytes(Path.of("data", "Bob.txt"))));
        }

        private static void saveFailure() throws StorageException, IOException {
            Files.createDirectories(Path.of("data"));
            Files.createDirectory(Path.of("data", "Bob.txt"));

            try {
                Storage.saveTasks(List.of(new Todo("task")));
            } catch (StorageException exception) {
                System.out.print("StorageException: " + exception.getMessage());
            }
        }
    }
}
