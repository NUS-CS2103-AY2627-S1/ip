package tuesday.processor;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Exercises atomic saves and failures without relying on filesystem permissions.
 */
public class AtomicFileWriterTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void write_newAndExistingDestination_commitsCompleteBytes() throws IOException {
        Path target = temporaryDirectory.resolve("nested/tasks.txt");
        AtomicFileWriter writer = new AtomicFileWriter();
        writer.write(target, "first".getBytes(StandardCharsets.UTF_8));
        assertEquals("first", Files.readString(target));
        writer.write(target, "replacement".getBytes(StandardCharsets.UTF_8));
        assertEquals("replacement", Files.readString(target));
        try (Stream<Path> files = Files.list(target.getParent())) {
            assertEquals(1, files.count());
        }
    }

    @Test
    public void write_partialTemporaryWrite_keepsOriginalAndCleansTemporaryFile() throws IOException {
        Path target = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(target, "original");
        IOException failure = new IOException("partial write");
        AtomicFileWriter writer = new AtomicFileWriter(new DiskOperations() {
            @Override
            public void write(Path file, byte[] content) throws IOException {
                Files.writeString(file, "partial");
                throw failure;
            }
        });
        assertSame(failure, assertThrows(IOException.class, () -> writer.write(target, new byte[0])));
        assertEquals("original", Files.readString(target));
        assertOnlyOriginalRemains();
    }

    @Test
    public void write_unsupportedAtomicMove_keepsOriginalWithoutFallback() throws IOException {
        Path target = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(target, "original");
        AtomicFileWriter writer = new AtomicFileWriter(new DiskOperations() {
            @Override
            public void replace(Path source, Path destination) throws IOException {
                throw new AtomicMoveNotSupportedException(source.toString(), destination.toString(), "test");
            }
        });
        assertThrows(AtomicMoveNotSupportedException.class, () ->
                writer.write(target, "replacement".getBytes(StandardCharsets.UTF_8)));
        assertEquals("original", Files.readString(target));
        assertOnlyOriginalRemains();
    }

    @Test
    public void write_failedReplacementOfNewFile_leavesDestinationAbsent() {
        Path target = temporaryDirectory.resolve("tasks.txt");
        AtomicFileWriter writer = new AtomicFileWriter(new DiskOperations() {
            @Override
            public void replace(Path source, Path destination) throws IOException {
                throw new IOException("replacement failed");
            }
        });
        assertThrows(IOException.class, () -> writer.write(target, new byte[0]));
        assertFalse(Files.exists(target));
    }

    @Test
    public void write_cleanupFailure_preservesPrimaryException() throws IOException {
        Path target = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(target, "original");
        IOException failure = new IOException("replacement failed");
        IOException cleanupFailure = new IOException("cleanup failed");
        AtomicFileWriter writer = new AtomicFileWriter(new DiskOperations() {
            @Override
            public void replace(Path source, Path destination) throws IOException {
                throw failure;
            }

            @Override
            public void delete(Path file) throws IOException {
                throw cleanupFailure;
            }
        });
        assertSame(failure, assertThrows(IOException.class, () -> writer.write(target, new byte[0])));
        assertArrayEquals(new Throwable[] {cleanupFailure}, failure.getSuppressed());
        assertEquals("original", Files.readString(target));
    }

    /**
     * Verifies the failed attempt has removed its own temporary file.
     */
    private void assertOnlyOriginalRemains() throws IOException {
        try (Stream<Path> files = Files.list(temporaryDirectory)) {
            assertEquals(1, files.count());
        }
    }

    /**
     * Supplies real operations so individual tests override only the failure being exercised.
     */
    private static class DiskOperations implements AtomicFileWriter.FileOperations {
        @Override
        public void write(Path file, byte[] content) throws IOException {
            Files.write(file, content);
        }

        @Override
        public void replace(Path source, Path target) throws IOException {
            Files.move(source, target, StandardCopyOption.ATOMIC_MOVE);
        }

        @Override
        public void delete(Path file) throws IOException {
            Files.deleteIfExists(file);
        }
    }
}
