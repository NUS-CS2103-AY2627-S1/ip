package kafka.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import kafka.exception.KafkaException;
import kafka.task.TaskList;
import kafka.task.Todo;

/**
 * Checks that one running session owns a task file even across processes and atomic saves.
 */
class InstanceLockTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void acquire_runningInstance_rejectsSecondAndAllowsRestartAfterClose() throws Exception {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        try (InstanceLock first = InstanceLock.acquire(dataFile)) {
            KafkaException exception = assertThrows(KafkaException.class, () -> InstanceLock.acquire(dataFile));
            assertTrue(exception.getMessage().contains("Another Kafka window is already running"));
            assertThrows(KafkaException.class, () -> InstanceLock.acquire(dataFile));
        }
        try (InstanceLock restarted = InstanceLock.acquire(dataFile)) {
            assertThrows(KafkaException.class, () -> InstanceLock.acquire(dataFile));
        }
    }

    @Test
    void acquire_staleLockFile_allowsStartupWithoutChangingSavedTasks() throws Exception {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(dataFile, "T | 0 | keep me\n");
        Files.writeString(temporaryDirectory.resolve("tasks.txt.lock"), "");

        try (InstanceLock instance = InstanceLock.acquire(dataFile)) {
            assertEquals("T | 0 | keep me\n", Files.readString(dataFile));
        }
    }

    @Test
    void acquire_replacedTaskFile_keepsSessionLocked() throws Exception {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        TaskStorage storage = new TaskStorage(dataFile);
        TaskList tasks = new TaskList();
        tasks.addTask(new Todo("keep me"));

        try (InstanceLock instance = InstanceLock.acquire(dataFile)) {
            storage.save(tasks);
            storage.save(tasks);
            assertThrows(KafkaException.class, () -> InstanceLock.acquire(dataFile));
            assertEquals("T | 0 | keep me", storage.load().getTasks().getFirst().toDataString());
        }
    }

    @Test
    void acquire_anotherProcess_cannotStartUntilFirstProcessReleasesLock() throws Exception {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        try (InstanceLock instance = InstanceLock.acquire(dataFile)) {
            assertChildProcessExit(dataFile, 2);
        }
        assertChildProcessExit(dataFile, 0);
    }

    private void assertChildProcessExit(Path dataFile, int expectedExit) throws Exception {
        String classpath = Path.of(InstanceLock.class.getProtectionDomain().getCodeSource().getLocation().toURI())
                + File.pathSeparator
                + Path.of(InstanceLockTest.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        Path javaExecutable = Path.of(System.getProperty("java.home"), "bin", "java");
        Process process = new ProcessBuilder(javaExecutable.toString(), "-cp", classpath,
                LockAttempt.class.getName(), dataFile.toString()).redirectErrorStream(true).start();
        try {
            assertTrue(process.waitFor(10, TimeUnit.SECONDS), "The second process must not wait for the lock");
            assertEquals(expectedExit, process.exitValue(), new String(process.getInputStream().readAllBytes()));
        } finally {
            process.destroyForcibly();
        }
    }

    /**
     * Attempts a lock in a separate JVM, using an exit code to report contention.
     */
    public static class LockAttempt {
        /**
         * Exits with code two when another process owns the supplied task file.
         */
        public static void main(String[] args) throws IOException {
            try (InstanceLock instance = InstanceLock.acquire(Path.of(args[0]))) {
                System.out.println("Lock acquired");
            } catch (KafkaException exception) {
                System.out.println(exception.getMessage());
                System.exit(2);
            }
        }
    }
}
