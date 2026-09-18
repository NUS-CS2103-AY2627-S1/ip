package herta.storage;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.AccessDeniedException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Manages strict file reads and replace-style writes for one storage file.
 *
 * <p>The manager also captures and restores file state for multi-file rollback.</p>
 */
final class StorageFileManager {
    static final long MAX_STORAGE_FILE_BYTES = 5_000_000L;
    private static final int MAX_TEMPORARY_FILE_ATTEMPTS = 100;
    private static final int LINE_SEPARATOR_BYTE_COUNT = System.lineSeparator()
            .getBytes(StandardCharsets.UTF_8).length;
    private static final Logger LOGGER = Logger.getLogger(StorageFileManager.class.getName());

    /** Describes the safely observable state of the configured path. */
    enum PathStatus {
        MISSING,
        REGULAR_FILE,
        DIRECTORY,
        INACCESSIBLE,
        OTHER
    }

    private final Path dataFile;

    /**
     * Creates a manager for a storage file.
     *
     * @param dataFile the file to manage.
     */
    StorageFileManager(Path dataFile) {
        this.dataFile = dataFile;
    }

    /** Returns the path state without relying on ambiguous convenience predicates. */
    PathStatus getPathStatus() {
        try {
            BasicFileAttributes attributes = Files.readAttributes(dataFile,
                    BasicFileAttributes.class, LinkOption.NOFOLLOW_LINKS);
            if (attributes.isDirectory()) {
                return PathStatus.DIRECTORY;
            }
            if (attributes.isRegularFile()) {
                return PathStatus.REGULAR_FILE;
            }
            return PathStatus.OTHER;
        } catch (NoSuchFileException e) {
            return PathStatus.MISSING;
        } catch (IOException | SecurityException e) {
            return PathStatus.INACCESSIBLE;
        }
    }

    /**
     * Reads the data file as strict UTF-8 instead of silently replacing malformed bytes.
     *
     * @return the lines from the data file.
     * @throws IOException if the file cannot be read or is not valid UTF-8.
     */
    List<String> readLines() throws IOException {
        ensureReadableRegularFile();
        return StorageRecordReader.read(dataFile);
    }

    /**
     * Writes validated lines to a temporary file beside the target.
     *
     * @param lines the records to stage.
     * @return the temporary file path.
     * @throws IOException if the directory or temporary file cannot be created.
     */
    Path writeTemporaryFile(List<String> lines) throws IOException {
        Path dataDirectory = prepareDataDirectory();
        ensureSerializedSize(lines);
        Path temporaryFile = null;
        try {
            temporaryFile = createOwnedTemporaryFile(dataDirectory, ".herta-", ".tmp");
            Files.write(temporaryFile, lines, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            forceFile(temporaryFile);
            return temporaryFile;
        } catch (IOException | SecurityException e) {
            deleteTemporaryFile(temporaryFile);
            throw e;
        }
    }

    /** Rejects an oversized replacement before creating a temporary data file. */
    private void ensureSerializedSize(List<String> lines) throws IOException {
        long serializedBytes = 0;
        for (String line : lines) {
            if (line == null) {
                throw new IOException("storage file contains a null record");
            }
            serializedBytes += line.getBytes(StandardCharsets.UTF_8).length
                    + LINE_SEPARATOR_BYTE_COUNT;
            if (serializedBytes > MAX_STORAGE_FILE_BYTES) {
                throw new IOException("storage file exceeds the size limit");
            }
        }
    }

    /**
     * Creates the data directory and verifies that the target can be replaced.
     *
     * @return the directory containing the data file.
     * @throws IOException if the target is not a regular file or the directory cannot be created.
     */
    private Path prepareDataDirectory() throws IOException {
        Path dataDirectory = dataFile.getParent();
        if (dataDirectory == null) {
            dataDirectory = Path.of(".");
        }
        Files.createDirectories(dataDirectory);
        PathStatus pathStatus = getPathStatus();
        if (pathStatus == PathStatus.DIRECTORY || pathStatus == PathStatus.OTHER) {
            throw new IOException("data path is not a regular file");
        }
        if (pathStatus == PathStatus.INACCESSIBLE) {
            throw new AccessDeniedException(dataFile.toString());
        }
        return dataDirectory;
    }

    /**
     * Replaces the target with a temporary file, falling back when atomic replacement is unsupported.
     *
     * @param temporaryFile the temporary file containing the new records.
     * @throws IOException if the replacement fails.
     */
    void replaceDataFile(Path temporaryFile) throws IOException {
        try {
            Files.move(temporaryFile, dataFile,
                    StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException | FileAlreadyExistsException e) {
            // Fall back only when atomic replacement is unsupported or rejected for the target.
            Files.move(temporaryFile, dataFile, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /**
     * Removes a temporary file after a save attempt.
     *
     * @param temporaryFile the temporary file to remove, if one was created.
     */
    void deleteTemporaryFile(Path temporaryFile) {
        if (temporaryFile != null) {
            try {
                Files.deleteIfExists(temporaryFile);
            } catch (IOException | SecurityException e) {
                LOGGER.log(Level.WARNING, "Unable to clean up Herta temporary file: "
                        + temporaryFile, e);
            }
        }
    }

    /**
     * Captures the exact bytes and existence state of the target file.
     *
     * @return the captured file state.
     * @throws IOException if the existing file cannot be read.
     */
    FileSnapshot captureSnapshot() throws IOException {
        PathStatus pathStatus = getPathStatus();
        if (pathStatus == PathStatus.MISSING) {
            return new FileSnapshot(dataFile, false, new byte[0]);
        }
        if (pathStatus != PathStatus.REGULAR_FILE) {
            throw new IOException("data path is not a readable regular file");
        }
        ensureFileSizeWithinLimit();
        byte[] contents = Files.readAllBytes(dataFile);
        return new FileSnapshot(dataFile, true, contents);
    }

    /** Compares the current file bytes with a snapshot captured before a command. */
    boolean hasSameContents(FileSnapshot snapshot) throws IOException {
        FileSnapshot currentSnapshot = captureSnapshot();
        return snapshot.wasPresent() == currentSnapshot.wasPresent()
                && Arrays.equals(snapshot.contents(), currentSnapshot.contents());
    }

    /**
     * Restores a target file to its captured contents or captured absence.
     *
     * @param snapshot the target's original state.
     * @throws IOException if restoration fails.
     */
    void restoreSnapshot(FileSnapshot snapshot) throws IOException {
        if (snapshot == null) {
            return;
        }
        if (!snapshot.wasPresent()) {
            Files.deleteIfExists(snapshot.path());
            return;
        }
        Files.write(snapshot.path(), snapshot.contents(), StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
        forceFile(snapshot.path());
    }

    /** Ensures the configured path can be read before opening a decoder. */
    private void ensureReadableRegularFile() throws IOException {
        PathStatus pathStatus = getPathStatus();
        if (pathStatus == PathStatus.MISSING) {
            throw new NoSuchFileException(dataFile.toString());
        }
        if (pathStatus == PathStatus.DIRECTORY || pathStatus == PathStatus.OTHER) {
            throw new IOException("data path is not a regular file");
        }
        if (pathStatus == PathStatus.INACCESSIBLE) {
            throw new AccessDeniedException(dataFile.toString());
        }
        ensureFileSizeWithinLimit();
    }

    /** Rejects an oversized file before readAllBytes or line accumulation allocates memory. */
    private void ensureFileSizeWithinLimit() throws IOException {
        long fileSize = Files.size(dataFile);
        if (fileSize > MAX_STORAGE_FILE_BYTES) {
            throw new IOException("storage file exceeds the size limit");
        }
    }

    /** Forces staged bytes to disk before they become eligible for replacement. */
    private void forceFile(Path file) throws IOException {
        try (FileChannel channel = FileChannel.open(file, StandardOpenOption.WRITE)) {
            channel.force(true);
        }
    }

    /** Creates an owned temporary file without shared mutable state. */
    static Path createOwnedTemporaryFile(Path directory, String prefix, String suffix)
            throws IOException {
        for (int attempt = 0; attempt < MAX_TEMPORARY_FILE_ATTEMPTS; attempt++) {
            String fileName = prefix + Long.toUnsignedString(System.nanoTime())
                    + "-" + attempt + suffix;
            Path candidate = directory.resolve(fileName);
            try {
                return Files.createFile(candidate);
            } catch (FileAlreadyExistsException e) {
                // A collision is harmless; try the next monotonic candidate.
            }
        }
        throw new IOException("unable to allocate a Herta temporary file");
    }

    /** Captures the state needed to restore one file after a failed multi-file save. */
    record FileSnapshot(Path path, boolean wasPresent, byte[] contents) {
        FileSnapshot(Path path, boolean wasPresent, byte[] contents) {
            this.path = Objects.requireNonNull(path, "A file snapshot needs a path.");
            this.wasPresent = wasPresent;
            this.contents = Objects.requireNonNull(contents,
                    "A file snapshot needs file contents.").clone();
        }

        @Override
        public byte[] contents() {
            return contents.clone();
        }
    }
}
