package nori;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Runs automated end-to-end tests for Nori's disk storage and input behavior from the root test folder.
 */
public class StorageTest {
    private static final String DATA_DIRECTORY = "data";
    private static final String STORAGE_FILE = "nori.txt";
    private static final String BACKUP_FILE = "nori.txt.bak";
    private static final String CORRUPT_FILE = "nori.txt.corrupt";

    /**
     * Runs every storage regression test.
     *
     * @param args ignored command-line arguments.
     * @throws Exception if a test cannot create its isolated environment or Nori cannot run.
     */
    public static void main(String[] args) throws Exception {
        runTest("Creates missing data directory",
                StorageTest::saveTasks_missingDirectory_createsStorageFiles);
        runTest("Restores delimiter-containing task", StorageTest::loadTasks_encodedField_restoresTask);
        runTest("Restores a deadline date", StorageTest::loadTasks_deadline_restoresDate);
        runTest("Uses project-root storage from source directories",
                StorageTest::storagePath_usesProjectRoot);
        runTest("Keeps test storage isolated inside a project",
                StorageTest::storageOverride_isolatesInProjectLaunch);
        runTest("Handles end of input", StorageTest::endOfInput_exitsCleanly);
        runTest("Recovers corrupted storage from backup",
                StorageTest::loadTasks_corruptedFile_restoresBackup);
        runTest("Recovers malformed UTF-8 storage from backup",
                StorageTest::loadTasks_malformedUtf8_restoresBackup);
        runTest("Recovers an invalid event date from backup",
                StorageTest::loadTasks_invalidEventDate_restoresBackup);
        runTest("Preserves corruption without backup",
                StorageTest::loadTasks_noBackup_preservesCorruptedFile);
        runTest("Preserves corruption when the backup is unreadable",
                StorageTest::loadTasks_corruptBackup_preservesCorruptedFile);
        System.out.println("All storage tests passed.");
    }

    private static void saveTasks_missingDirectory_createsStorageFiles() throws Exception {
        Path testDirectory = Files.createTempDirectory("nori-storage-test-");
        try {
            runNori(testDirectory, "todo create storage\nbye\n");

            Path dataDirectory = testDirectory.resolve(DATA_DIRECTORY);
            assertTrue(Files.isDirectory(dataDirectory), "Expected the data directory to be created.");
            assertTrue(Files.isRegularFile(dataDirectory.resolve(STORAGE_FILE)),
                    "Expected nori.txt to exist.");
            assertTrue(Files.isRegularFile(dataDirectory.resolve(BACKUP_FILE)),
                    "Expected nori.txt.bak to exist.");
        } finally {
            deleteDirectory(testDirectory);
        }
    }

    private static void loadTasks_encodedField_restoresTask() throws Exception {
        Path testDirectory = Files.createTempDirectory("nori-storage-test-");
        try {
            runNori(testDirectory, "todo revise | annotate notes\nmark 1\nbye\n");
            String output = runNori(testDirectory, "list\nbye\n");

            assertContains(output, "1.[T][X] revise | annotate notes");
        } finally {
            deleteDirectory(testDirectory);
        }
    }

    private static void loadTasks_deadline_restoresDate() throws Exception {
        Path testDirectory = Files.createTempDirectory("nori-storage-test-");
        try {
            runNori(testDirectory, "deadline return book /by 2019-12-02\nbye\n");
            String output = runNori(testDirectory, "list\nbye\n");

            assertContains(output, "1.[D][ ] return book (by: Dec 02 2019)");
        } finally {
            deleteDirectory(testDirectory);
        }
    }

    private static void storagePath_usesProjectRoot() throws Exception {
        Path projectDirectory = Files.createTempDirectory("nori-project-root-test-");
        try {
            Path sourceDirectory = Files.createDirectories(projectDirectory.resolve("src").resolve("main")
                    .resolve("java"));
            Path packageDirectory = Files.createDirectories(sourceDirectory.resolve("nori"));
            Files.createFile(packageDirectory.resolve("Nori.java"));
            Path legacyDataDirectory = Files.createDirectories(sourceDirectory.resolve(DATA_DIRECTORY));
            Files.writeString(legacyDataDirectory.resolve(STORAGE_FILE),
                    "T | 0 | legacy task", StandardCharsets.UTF_8);

            String sourceOutput = runNoriUsingProjectStorage(sourceDirectory, "list\nbye\n");
            String rootOutput = runNoriUsingProjectStorage(projectDirectory, "list\nbye\n");

            assertContains(sourceOutput, "I've imported saved tasks from src/main/java/data");
            assertContains(sourceOutput, "1.[T][ ] legacy task");
            assertContains(rootOutput, "1.[T][ ] legacy task");
            assertTrue(Files.isRegularFile(projectDirectory.resolve(DATA_DIRECTORY).resolve(STORAGE_FILE)),
                    "Expected migrated storage in the project-root data directory.");
        } finally {
            deleteDirectory(projectDirectory);
        }
    }

    private static void storageOverride_isolatesInProjectLaunch() throws Exception {
        Path projectDirectory = Files.createTempDirectory("nori-isolated-project-test-");
        Path isolatedStorageDirectory = Files.createTempDirectory("nori-isolated-storage-test-");
        try {
            Path sourceDirectory = Files.createDirectories(projectDirectory.resolve("src").resolve("main")
                    .resolve("java"));
            Path packageDirectory = Files.createDirectories(sourceDirectory.resolve("nori"));
            Files.createFile(packageDirectory.resolve("Nori.java"));
            Path projectDataDirectory = Files.createDirectories(projectDirectory.resolve(DATA_DIRECTORY));
            Path projectStorageFile = projectDataDirectory.resolve(STORAGE_FILE);
            String protectedContent = "T | 0 | protected saved task";
            Files.writeString(projectStorageFile, protectedContent, StandardCharsets.UTF_8);

            String output = runNori(sourceDirectory, isolatedStorageDirectory,
                    "todo isolated test task\nbye\n");

            assertContains(output, "The iceberg now holds 1 task(s).");
            assertEquals(protectedContent, Files.readString(projectStorageFile, StandardCharsets.UTF_8));
            assertContains(Files.readString(isolatedStorageDirectory.resolve(STORAGE_FILE),
                    StandardCharsets.UTF_8),
                    "b64:aXNvbGF0ZWQgdGVzdCB0YXNr");
        } finally {
            deleteDirectory(projectDirectory);
            deleteDirectory(isolatedStorageDirectory);
        }
    }

    private static void endOfInput_exitsCleanly() throws Exception {
        Path testDirectory = Files.createTempDirectory("nori-storage-test-");
        try {
            String output = runNori(testDirectory, "todo end cleanly\n");

            assertContains(output, "[T][ ] end cleanly");
            assertContains(output, "Noot noot! Time to waddle off. Stay frosty!");
        } finally {
            deleteDirectory(testDirectory);
        }
    }

    private static void loadTasks_corruptedFile_restoresBackup() throws Exception {
        Path testDirectory = Files.createTempDirectory("nori-storage-test-");
        try {
            runNori(testDirectory, "todo recover this task\nbye\n");

            Path dataDirectory = testDirectory.resolve(DATA_DIRECTORY);
            Path storageFile = dataDirectory.resolve(STORAGE_FILE);
            String corruptContent = "this is not a Nori task record";
            Files.writeString(storageFile, corruptContent, StandardCharsets.UTF_8);

            String output = runNori(testDirectory, "list\nbye\n");

            assertContains(output, "Your saved tasks were corrupted. I've restored the backup");
            assertContains(output, "1.[T][ ] recover this task");
            assertEquals(corruptContent,
                    Files.readString(dataDirectory.resolve(CORRUPT_FILE), StandardCharsets.UTF_8));
            assertContains(Files.readString(storageFile, StandardCharsets.UTF_8),
                    "b64:cmVjb3ZlciB0aGlzIHRhc2s=");
        } finally {
            deleteDirectory(testDirectory);
        }
    }

    private static void loadTasks_noBackup_preservesCorruptedFile() throws Exception {
        Path testDirectory = Files.createTempDirectory("nori-storage-test-");
        try {
            Path dataDirectory = Files.createDirectory(testDirectory.resolve(DATA_DIRECTORY));
            Path storageFile = dataDirectory.resolve(STORAGE_FILE);
            String corruptContent = "T | invalid-status | corrupted task";
            Files.writeString(storageFile, corruptContent, StandardCharsets.UTF_8);

            String output = runNori(testDirectory, "list\nbye\n");

            assertContains(output, "Your saved tasks in data/nori.txt are corrupted"
                    + " and I have no backup to waddle back to.");
            assertContains(output, "Repair that file or move it out of the data folder");
            assertEquals(corruptContent, Files.readString(storageFile, StandardCharsets.UTF_8));
            assertTrue(Files.notExists(dataDirectory.resolve(CORRUPT_FILE)),
                    "Expected no corrupt copy without a recoverable backup.");
        } finally {
            deleteDirectory(testDirectory);
        }
    }

    private static void loadTasks_corruptBackup_preservesCorruptedFile() throws Exception {
        Path testDirectory = Files.createTempDirectory("nori-storage-test-");
        try {
            Path dataDirectory = Files.createDirectory(testDirectory.resolve(DATA_DIRECTORY));
            Path storageFile = dataDirectory.resolve(STORAGE_FILE);
            Path backupFile = dataDirectory.resolve(BACKUP_FILE);
            String corruptContent = "T | invalid-status | corrupted task";
            Files.writeString(storageFile, corruptContent, StandardCharsets.UTF_8);
            Files.writeString(backupFile, "D | 0 | broken backup", StandardCharsets.UTF_8);

            String output = runNori(testDirectory, "list\nbye\n");

            assertContains(output,
                    "Your saved tasks in data/nori.txt are corrupted"
                            + " and the backup wouldn't come back either.");
            assertEquals(corruptContent, Files.readString(storageFile, StandardCharsets.UTF_8));
        } finally {
            deleteDirectory(testDirectory);
        }
    }

    /**
     * Runs one test case and reports it as passed, letting a failure stop the suite.
     *
     * @param testName the name to report for this test.
     * @param testCase the test to run.
     */
    private static void runTest(String testName, StorageTestCase testCase) throws Exception {
        testCase.run();
        System.out.println("PASS: " + testName);
    }

    /**
     * Runs Nori in the given directory with one complete standard-input session.
     *
     * @param workingDirectory the isolated working directory for this Nori launch.
     * @param input the standard-input content to send to Nori.
     * @return Nori's complete console output.
     * @throws IOException if the child process cannot be started or communicated with.
     * @throws InterruptedException if the current thread is interrupted while Nori is running.
     */
    private static String runNori(Path workingDirectory, String input)
            throws IOException, InterruptedException {
        return runNori(workingDirectory, workingDirectory.resolve(DATA_DIRECTORY), input);
    }

    /**
     * Runs Nori with its storage redirected to an explicit directory.
     *
     * @param workingDirectory the isolated working directory for this Nori launch.
     * @param storageDirectory the directory Nori must store its tasks in.
     * @param input the standard-input content to send to Nori.
     * @return Nori's complete console output.
     * @throws IOException if the child process cannot be started or communicated with.
     * @throws InterruptedException if the current thread is interrupted while Nori is running.
     */
    private static String runNori(Path workingDirectory, Path storageDirectory, String input)
            throws IOException, InterruptedException {
        Process process = new ProcessBuilder("java", "-ea",
                "-Dnori.storage.dir=" + storageDirectory.toAbsolutePath(),
                "-cp", getAbsoluteClassPath(), "nori.Nori")
                .directory(workingDirectory.toFile())
                .redirectErrorStream(true)
                .start();
        process.getOutputStream().write(input.getBytes(StandardCharsets.UTF_8));
        process.getOutputStream().close();

        String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new AssertionError("Nori exited with status " + exitCode + ":\n" + output);
        }
        return output;
    }

    /**
     * Runs Nori without the storage override, so it selects its own project-root storage.
     *
     * @param workingDirectory the isolated working directory for this Nori launch.
     * @param input the standard-input content to send to Nori.
     * @return Nori's complete console output.
     * @throws IOException if the child process cannot be started or communicated with.
     * @throws InterruptedException if the current thread is interrupted while Nori is running.
     */
    private static String runNoriUsingProjectStorage(Path workingDirectory, String input)
            throws IOException, InterruptedException {
        Process process = new ProcessBuilder("java", "-ea",
                "-cp", getAbsoluteClassPath(), "nori.Nori")
                .directory(workingDirectory.toFile())
                .redirectErrorStream(true)
                .start();
        process.getOutputStream().write(input.getBytes(StandardCharsets.UTF_8));
        process.getOutputStream().close();

        String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new AssertionError("Nori exited with status " + exitCode + ":\n" + output);
        }
        return output;
    }

    private static void loadTasks_malformedUtf8_restoresBackup() throws Exception {
        Path testDirectory = Files.createTempDirectory("nori-storage-test-");
        try {
            runNori(testDirectory, "todo restore valid text\nbye\n");

            Path dataDirectory = testDirectory.resolve(DATA_DIRECTORY);
            Path storageFile = dataDirectory.resolve(STORAGE_FILE);
            String corruptContent = "T | 0 | b64:/w==";
            Files.writeString(storageFile, corruptContent, StandardCharsets.UTF_8);

            String output = runNori(testDirectory, "list\nbye\n");

            assertContains(output, "Your saved tasks were corrupted. I've restored the backup");
            assertContains(output, "1.[T][ ] restore valid text");
            assertEquals(corruptContent,
                    Files.readString(dataDirectory.resolve(CORRUPT_FILE), StandardCharsets.UTF_8));
        } finally {
            deleteDirectory(testDirectory);
        }
    }

    private static void loadTasks_invalidEventDate_restoresBackup() throws Exception {
        Path testDirectory = Files.createTempDirectory("nori-storage-test-");
        try {
            runNori(testDirectory, "todo keep this task\nbye\n");

            Path dataDirectory = testDirectory.resolve(DATA_DIRECTORY);
            Path storageFile = dataDirectory.resolve(STORAGE_FILE);
            String corruptContent = "E | 0 | invalid event | 2019-02-31 1400 | 2019-02-31 1500";
            Files.writeString(storageFile, corruptContent, StandardCharsets.UTF_8);

            String output = runNori(testDirectory, "list\nbye\n");

            assertContains(output, "Your saved tasks were corrupted. I've restored the backup");
            assertContains(output, "1.[T][ ] keep this task");
            assertEquals(corruptContent,
                    Files.readString(dataDirectory.resolve(CORRUPT_FILE), StandardCharsets.UTF_8));
        } finally {
            deleteDirectory(testDirectory);
        }
    }

    /**
     * Returns the test classpath as an absolute path, so a child Nori process
     * can use it from a temporary directory.
     *
     * @return the absolute classpath for the compiled test classes.
     */
    private static String getAbsoluteClassPath() {
        String[] classPathEntries = System.getProperty("java.class.path").split(File.pathSeparator);
        return Arrays.stream(classPathEntries)
                .map(entry -> Path.of(entry).toAbsolutePath().toString())
                .collect(Collectors.joining(File.pathSeparator));
    }

    /**
     * Deletes an isolated test directory and all of its contents.
     *
     * @param directory the directory to delete.
     * @throws IOException if the directory tree cannot be read.
     */
    private static void deleteDirectory(Path directory) throws IOException {
        try (Stream<Path> paths = Files.walk(directory)) {
            paths.sorted(Comparator.reverseOrder()).forEach(StorageTest::deletePath);
        }
    }

    /**
     * Deletes one file or directory while cleaning up a test directory.
     *
     * @param path the path to delete.
     */
    private static void deletePath(Path path) {
        try {
            Files.delete(path);
        } catch (IOException exception) {
            throw new AssertionError("Could not clean up test path: " + path, exception);
        }
    }

    /**
     * Fails the current test unless the actual text contains the expected text.
     *
     * @param actual the text to inspect.
     * @param expected the text that must be present.
     */
    private static void assertContains(String actual, String expected) {
        if (!actual.contains(expected)) {
            throw new AssertionError("Expected output to contain \"" + expected + "\" but was:\n" + actual);
        }
    }

    /**
     * Fails the current test unless the actual text equals the expected text.
     *
     * @param expected the required text.
     * @param actual the text produced by the code under test.
     */
    private static void assertEquals(String expected, String actual) {
        if (!expected.equals(actual)) {
            throw new AssertionError("Expected \"" + expected + "\" but was \"" + actual + "\".");
        }
    }

    /**
     * Fails the current test unless the condition holds.
     *
     * @param condition the condition that must hold.
     * @param message the failure message to report.
     */
    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    /**
     * Represents one storage test that can report a checked failure.
     */
    @FunctionalInterface
    private interface StorageTestCase {
        /**
         * Runs this test case.
         *
         * @throws Exception if the test fails or its environment cannot be prepared.
         */
        void run() throws Exception;
    }
}
