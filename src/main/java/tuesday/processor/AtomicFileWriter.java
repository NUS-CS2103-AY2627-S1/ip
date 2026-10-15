package tuesday.processor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * Writes a sibling temporary file before atomically replacing the destination.
 */
final class AtomicFileWriter {
    private final FileOperations operations;

    AtomicFileWriter() {
        this(new FileOperations() {
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
        });
    }

    /**
     * Allows tests to fail individual file operations without changing global state.
     */
    AtomicFileWriter(FileOperations operations) {
        this.operations = operations;
    }

    /**
     * Replaces the file only after its complete contents have been written and closed.
     * Unsupported atomic replacement is reported to the caller without a non-atomic fallback.
     */
    void write(Path target, byte[] content) throws IOException {
        Path destination = target.toAbsolutePath();
        Path parent = destination.getParent();
        Files.createDirectories(parent);
        Path temporaryFile = Files.createTempFile(parent, ".tuesday-", ".tmp");
        try {
            operations.write(temporaryFile, content);
            operations.replace(temporaryFile, destination);
        } catch (IOException | RuntimeException exception) {
            try {
                operations.delete(temporaryFile);
            } catch (IOException | RuntimeException cleanupException) {
                exception.addSuppressed(cleanupException);
            }
            throw exception;
        }
    }

    /**
     * Provides the fallible operations used to write, commit, and clean up a temporary file.
     */
    interface FileOperations {
        /**
         * Writes and closes a temporary file before returning.
         *
         * @param file the temporary file.
         * @param content the complete replacement bytes.
         * @throws IOException if writing or closing fails.
         */
        void write(Path file, byte[] content) throws IOException;

        /**
         * Atomically replaces the destination, with no fallible work after committing.
         *
         * @param source the complete temporary file.
         * @param target the destination.
         * @throws IOException if atomic replacement fails.
         */
        void replace(Path source, Path target) throws IOException;

        /**
         * Removes an uncommitted temporary file.
         *
         * @param file the temporary file created by this attempt.
         * @throws IOException if cleanup fails.
         */
        void delete(Path file) throws IOException;
    }
}
