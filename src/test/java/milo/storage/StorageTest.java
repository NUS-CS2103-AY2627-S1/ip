package milo.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import milo.exception.MiloException;
import milo.task.TaskList;
import milo.task.Todo;

/** Tests save replacement retries without relying on timing or operating-system locks. */
public class StorageTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void save_temporaryAccessDenial_retriesAndPersistsTasks() throws Exception {
        Path taskFile = temporaryDirectory.resolve("tasks.txt");
        FailingStorage storage = new FailingStorage(taskFile, 2, new AccessDeniedException(taskFile.toString()));
        TaskList tasks = new TaskList(List.of(new Todo("new task")));

        storage.save(tasks);

        assertEquals(3, storage.attempts);
        assertEquals("[T][ ] new task", new Storage(taskFile.toString()).load().getFirst().toString());
        assertFalse(Files.exists(temporaryDirectory.resolve("tasks.txt.tmp")));
    }

    @Test
    public void save_persistentAccessDenial_stopsAndPreservesOriginalFile() throws Exception {
        Path taskFile = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(taskFile, "[T][X] original task\n");
        AccessDeniedException failure = new AccessDeniedException(taskFile.toString());
        FailingStorage storage = new FailingStorage(taskFile, Integer.MAX_VALUE, failure);

        MiloException exception = assertThrows(MiloException.class,
                () -> storage.save(new TaskList(List.of(new Todo("replacement")))));

        assertEquals(5, storage.attempts);
        assertSame(failure, exception.getCause());
        assertEquals("[T][X] original task\n", Files.readString(taskFile));
        assertFalse(Files.exists(temporaryDirectory.resolve("tasks.txt.tmp")));
    }

    @Test
    public void save_otherIoError_doesNotRetry() {
        Path taskFile = temporaryDirectory.resolve("tasks.txt");
        IOException failure = new IOException("Disk unavailable");
        FailingStorage storage = new FailingStorage(taskFile, Integer.MAX_VALUE, failure);

        MiloException exception = assertThrows(MiloException.class, () -> storage.save(new TaskList()));

        assertEquals(1, storage.attempts);
        assertSame(failure, exception.getCause());
        assertFalse(Files.exists(taskFile));
    }

    @Test
    public void save_interruptedRetry_stopsAndRestoresInterruptFlag() {
        Path taskFile = temporaryDirectory.resolve("tasks.txt");
        FailingStorage storage = new FailingStorage(taskFile, 1, new AccessDeniedException(taskFile.toString()));
        storage.shouldInterrupt = true;
        try {
            MiloException exception = assertThrows(MiloException.class, () -> storage.save(new TaskList()));

            assertEquals(1, storage.attempts);
            assertInstanceOf(InterruptedException.class, exception.getCause().getCause());
            assertTrue(Thread.currentThread().isInterrupted());
        } finally {
            Thread.interrupted();
        }
    }

    /** Simulates failures at the replacement boundary, leaving file writing and cleanup real. */
    private static class FailingStorage extends Storage {
        private final int failureCount;
        private final IOException failure;
        private int attempts;
        private boolean shouldInterrupt;

        FailingStorage(Path taskFile, int failureCount, IOException failure) {
            super(taskFile.toString());
            this.failureCount = failureCount;
            this.failure = failure;
        }

        @Override
        void replaceFile(Path temporaryFile) throws IOException {
            attempts++;
            if (attempts <= failureCount) {
                if (shouldInterrupt) {
                    Thread.currentThread().interrupt();
                }
                throw failure;
            }
            super.replaceFile(temporaryFile);
        }
    }
}
