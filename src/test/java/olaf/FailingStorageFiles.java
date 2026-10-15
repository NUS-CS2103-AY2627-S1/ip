package olaf;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/** Simulates storage failures while keeping all actual file operations inside each test's temporary directory. */
final class FailingStorageFiles extends StorageFiles {
    /** Selects the operation that should fail on the next save or load. */
    enum Failure {
        NONE,
        READ,
        WRITE,
        INTERRUPTED_WRITE,
        REPLACE,
        UNSUPPORTED_MOVE
    }

    Failure failure = Failure.NONE;
    boolean shouldFailCleanup;
    boolean wasReplacementAttempted;
    Path lastTemporaryFile;

    @Override
    List<String> readRecords(Path path) throws IOException {
        if (failure == Failure.READ) {
            throw new AccessDeniedException(path.toString());
        }
        return super.readRecords(path);
    }

    @Override
    void writeRecords(Path path, List<String> records) throws IOException {
        lastTemporaryFile = path;
        if (failure == Failure.WRITE || failure == Failure.INTERRUPTED_WRITE) {
            Files.writeString(path, "incomplete snapshot");
            if (failure == Failure.INTERRUPTED_WRITE) {
                throw new InterruptedIOException("Snapshot write interrupted");
            }
            throw new IOException("No space left for the snapshot");
        }
        super.writeRecords(path, records);
    }

    @Override
    void replace(Path temporaryFile, Path destination) throws IOException {
        wasReplacementAttempted = true;
        if (failure == Failure.REPLACE) {
            throw new AccessDeniedException(destination.toString());
        }
        if (failure == Failure.UNSUPPORTED_MOVE) {
            throw new AtomicMoveNotSupportedException(temporaryFile.toString(), destination.toString(),
                    "Simulated unsupported atomic move");
        }
        super.replace(temporaryFile, destination);
    }

    @Override
    void deleteIfExists(Path path) throws IOException {
        if (shouldFailCleanup) {
            throw new IOException("Temporary file cleanup failed");
        }
        super.deleteIfExists(path);
    }
}
