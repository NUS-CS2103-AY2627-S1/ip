package goop;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

/**
 * Runs black-box checks for saving, loading, and corrupted-file handling.
 */
public class StorageTest {
    /**
     * Launches Goop in an isolated temporary directory and verifies persistence
     * across separate application processes.
     *
     * @param args Command-line arguments, which are not used.
     * @throws Exception If a process or temporary-file operation fails.
     */
    public static void main(String[] args) throws Exception {
        Path temporaryDirectory = Files.createTempDirectory("goop-storage-test-");
        try {
            Path dataFile = temporaryDirectory.resolve(Path.of("data", "goop.txt"));

            check(Files.notExists(dataFile), "The test must begin without a data file.");
            runGoop(temporaryDirectory,
                    "todo read | book \\ notes\n"
                            + "deadline return book /by 6/6/2019 1800\n"
                            + "event project meeting /from Aug 6th 2pm /to 4pm\n"
                            + "mark 1\n"
                            + "bye\n");

            check(Files.isRegularFile(dataFile),
                    "Saving should create the missing data folder and file.");
            List<String> savedLines = Files.readAllLines(dataFile, StandardCharsets.UTF_8);
            check(savedLines.equals(List.of(
                    "T | 1 | read \\| book \\\\ notes",
                    "D | 0 | return book | 2019-06-06T18:00:00",
                    "E | 0 | project meeting | Aug 6th 2pm | 4pm")),
                    "The data file should contain every task and completion status.");

            String reloadedOutput = runGoop(temporaryDirectory, "list\nbye\n");
            check(reloadedOutput.contains(" 1.[T][X] read | book \\ notes\n"),
                    "A completed todo should reload with escaped text intact.");
            check(reloadedOutput.contains(
                    " 2.[D][ ] return book (by: Jun 6 2019, 6:00 PM)\n"),
                    "A deadline should reload with its parsed date and time.");
            check(reloadedOutput.contains(
                    " 3.[E][ ] project meeting (from: Aug 6th 2pm to: 4pm)\n"),
                    "An event should reload with its start and end text.");

            runGoop(temporaryDirectory, "unmark 1\ndelete 2\nbye\n");
            String changedOutput = runGoop(temporaryDirectory, "list\nbye\n");
            check(changedOutput.contains(" 1.[T][ ] read | book \\ notes\n"),
                    "An unmarked task should reload as incomplete.");
            check(changedOutput.contains(
                    " 2.[E][ ] project meeting (from: Aug 6th 2pm to: 4pm)\n"),
                    "Tasks remaining after deletion should reload and be renumbered.");
            check(!changedOutput.contains("return book"),
                    "A deleted task should not reappear after restarting.");

            Files.writeString(dataFile, "X | 0 | broken task\n", StandardCharsets.UTF_8);
            String corruptedOutput = runGoop(temporaryDirectory, "list\nbye\n");
            check(corruptedOutput.contains(
                    " WARNING: Saved task data is invalid at line 1 (unknown task type).\n"
                            + " Starting with an empty task list.\n"),
                    "A corrupt data file should produce a warning instead of a crash.");
            check(!corruptedOutput.contains(" 1.["),
                    "A corrupt data file should not load partial task data.");
        } finally {
            deleteRecursively(temporaryDirectory);
        }
    }

    /**
     * Starts one Goop process in the supplied working directory.
     */
    private static String runGoop(Path workingDirectory, String input)
            throws IOException, InterruptedException {
        Path classPath = Path.of("out", "production", "ip").toAbsolutePath();
        Path javaExecutable = Path.of(System.getProperty("java.home"), "bin", "java");
        Process process = new ProcessBuilder(
                javaExecutable.toString(), "-cp", classPath.toString(), "goop.Goop")
                .directory(workingDirectory.toFile())
                .redirectErrorStream(true)
                .start();

        process.getOutputStream().write(input.getBytes(StandardCharsets.UTF_8));
        process.getOutputStream().close();
        String output = new String(process.getInputStream().readAllBytes(),
                StandardCharsets.UTF_8);
        int exitCode = process.waitFor();
        check(exitCode == 0, "Goop should exit successfully. Output:\n" + output);
        return output;
    }

    /**
     * Throws an assertion error when an expected condition is false.
     */
    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    /**
     * Removes the isolated temporary directory after the checks finish.
     */
    private static void deleteRecursively(Path directory) throws IOException {
        try (Stream<Path> paths = Files.walk(directory)) {
            for (Path path : paths.sorted(Comparator.reverseOrder()).toList()) {
                Files.deleteIfExists(path);
            }
        }
    }
}
