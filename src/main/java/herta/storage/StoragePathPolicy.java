package herta.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.Objects;

import herta.exception.HertaException;

/**
 * Applies the path rules shared by active and archived task storage.
 */
final class StoragePathPolicy {
    private static final String ARCHIVE_LOAD_PREFIX = "Failed to load archived tasks: ";

    private StoragePathPolicy() {
        // Utility class; do not instantiate.
    }

    /** Resolves the archive file beside the configured active file. */
    static Path resolveArchivePath(String activeFilePath) {
        Path activePath = parsePath(activeFilePath);
        Path parent = activePath.getParent();
        if (parent == null) {
            parent = Path.of(".");
        }
        return parent.resolve("archive.txt");
    }

    /** Rejects active and archive paths that identify the same file. */
    static void validateDistinctPaths(Path activeFile, Path archiveFile) throws HertaException {
        Objects.requireNonNull(activeFile, "The active data path cannot be null.");
        Objects.requireNonNull(archiveFile, "The archive data path cannot be null.");
        Path normalizedActivePath = activeFile.toAbsolutePath().normalize();
        Path normalizedArchivePath = archiveFile.toAbsolutePath().normalize();
        if (normalizedActivePath.equals(normalizedArchivePath)) {
            throw createConflictingPathsException();
        }
        try {
            boolean areBothPathsPresent = Files.exists(normalizedActivePath)
                    && Files.exists(normalizedArchivePath);
            if (areBothPathsPresent
                    && Files.isSameFile(normalizedActivePath, normalizedArchivePath)) {
                throw createConflictingPathsException();
            }
        } catch (IOException | SecurityException e) {
            throw new HertaException(ARCHIVE_LOAD_PREFIX
                    + "unable to compare active and archive paths.");
        }
    }

    /** Converts a configured path into the stable invalid-path exception. */
    private static Path parsePath(String filePath) {
        try {
            return Path.of(Objects.requireNonNull(filePath));
        } catch (InvalidPathException | NullPointerException e) {
            throw new IllegalArgumentException("Configured data path is invalid.", e);
        }
    }

    /** Creates the stable exception used for active/archive path conflicts. */
    private static HertaException createConflictingPathsException() {
        return new HertaException(ARCHIVE_LOAD_PREFIX
                + "active and archive paths must be different files.");
    }
}
