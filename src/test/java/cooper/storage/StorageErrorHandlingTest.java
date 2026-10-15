package cooper.storage;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import cooper.exception.CooperException;
import cooper.task.ToDo;

/** Covers malformed task data and filesystem failures without depending on OS permissions. */
public class StorageErrorHandlingTest {
    @TempDir
    private Path directory;

    @Test
    public void malformedRecords_throwDomainExceptions() {
        Storage storage = new Storage(directory.resolve("tasks.txt").toString());
        for (String line : new String[] {
            "T | 0 | ", "D | 0 | date | not-a-date", "D | 0 | date | 2026-02-30T00:00",
            "E | 0 | event | 2026-10-01T00:00 | 2026-10-01T00:00",
            "V2 | T | 0 | @@@", "V2 | T | 0 | /w==", "V2 | T | 0 | Cg=="
        }) {
            assertEquals(Storage.UNAVAILABLE,
                    assertThrows(CooperException.class, () -> storage.decodeTask(line), line).getMessage());
        }
    }

    @Test
    public void invalidUtf8_preservesOriginalBytes() throws IOException {
        Path file = directory.resolve("tasks.txt");
        byte[] bytes = {(byte) 0xc3, (byte) 0x28};
        Files.write(file, bytes);
        assertThrows(CooperException.class, () -> new Storage(file.toString()).loadTasks());
        assertArrayEquals(bytes, Files.readAllBytes(file));
    }

    @Test
    public void inaccessiblePaths_reportLoadAndSaveFailures() throws IOException {
        Storage folder = new Storage(directory.toString());
        assertEquals(Storage.UNAVAILABLE, assertThrows(CooperException.class, folder::loadTasks).getMessage());
        Path parent = directory.resolve("file-not-folder");
        Files.writeString(parent, "keep");
        Storage blocked = new Storage(parent.resolve("tasks.txt").toString());
        assertEquals(Storage.UNAVAILABLE, assertThrows(CooperException.class, blocked::loadTasks).getMessage());
        assertEquals(Storage.SAVE_FAILED,
                assertThrows(CooperException.class, () -> blocked.saveTasks(List.of(new ToDo("new")))).getMessage());
        assertEquals("keep", Files.readString(parent));
    }

    @Test
    public void atomicMoveUnsupported_preservesOriginalAndCleansTemporaryFile() throws IOException {
        Path file = directory.resolve("tasks.txt");
        Files.writeString(file, "T | 0 | original\n");
        Storage storage = new Storage(file.toString()) {
            @Override
            protected void replaceFile(Path temporary, Path destination) throws IOException {
                throw new AtomicMoveNotSupportedException(temporary.toString(), destination.toString(), "test");
            }
        };
        assertEquals(Storage.SAVE_FAILED,
                assertThrows(CooperException.class, () -> storage.saveTasks(List.of(new ToDo("new")))).getMessage());
        assertEquals("T | 0 | original\n", Files.readString(file));
        try (var files = Files.list(directory)) {
            assertEquals(1, files.count());
        }
    }
}
