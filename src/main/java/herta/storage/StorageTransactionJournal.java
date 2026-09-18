package herta.storage;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.AccessDeniedException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import herta.exception.HertaException;

/**
 * Writes durable preparation and commit phases for a two-file transaction.
 */
final class StorageTransactionJournal {
    static final String JOURNAL_FILE_NAME = ".herta-transaction";
    static final String ACTIVE_PATH_KEY = "activePath";
    static final String ARCHIVE_PATH_KEY = "archivePath";
    static final String ACTIVE_PRESENT_KEY = "activePresent";
    static final String ARCHIVE_PRESENT_KEY = "archivePresent";
    static final String ACTIVE_BACKUP_KEY = "activeBackup";
    static final String ARCHIVE_BACKUP_KEY = "archiveBackup";
    static final String PHASE_KEY = "phase";
    static final String ABSENT_BACKUP = "ABSENT";
    static final Set<String> JOURNAL_KEYS = Set.of(ACTIVE_PATH_KEY, ARCHIVE_PATH_KEY,
            ACTIVE_PRESENT_KEY, ARCHIVE_PRESENT_KEY, ACTIVE_BACKUP_KEY, ARCHIVE_BACKUP_KEY,
            PHASE_KEY);
    static final Duration STALE_FILE_AGE = Duration.ofDays(1);
    private static final Logger LOGGER = Logger.getLogger(StorageTransactionJournal.class.getName());
    private static final Path ABSENT_BACKUP_PATH = Path.of(ABSENT_BACKUP);

    private final Path journalPath;
    private final Path activeBackup;
    private final Path archiveBackup;

    /** Describes the durable phases of a two-file storage transaction. */
    enum Phase {
        PREPARED,
        ACTIVE_COMMITTED,
        COMMITTED;

        /** Converts the serialized journal value to its state-machine phase. */
        static Phase fromSerialized(String value) throws IOException {
            for (Phase phase : values()) {
                if (phase.name().equals(value)) {
                    return phase;
                }
            }
            throw new IOException("Storage transaction journal phase is invalid.");
        }
    }

    private StorageTransactionJournal(Path journalPath, Path activeBackup, Path archiveBackup) {
        this.journalPath = journalPath;
        this.activeBackup = activeBackup;
        this.archiveBackup = archiveBackup;
    }

    /** Creates forced backups and records a prepared transaction. */
    static StorageTransactionJournal prepare(Path activeFile, Path archiveFile,
                                             StorageFileManager.FileSnapshot activeSnapshot,
                                             StorageFileManager.FileSnapshot archiveSnapshot)
            throws IOException {
        Path directory = getDataDirectory(activeFile);
        Files.createDirectories(directory);
        Path journalPath = directory.resolve(JOURNAL_FILE_NAME);
        Path activeBackup = ABSENT_BACKUP_PATH;
        Path archiveBackup = ABSENT_BACKUP_PATH;
        try {
            activeBackup = createBackup(directory, "active", activeSnapshot);
            archiveBackup = createBackup(directory, "archive", archiveSnapshot);
            StorageTransactionJournal journal = new StorageTransactionJournal(journalPath,
                    activeBackup, archiveBackup);
            journal.writePhase(activeFile, archiveFile, activeSnapshot, archiveSnapshot,
                    Phase.PREPARED);
            return journal;
        } catch (IOException | RuntimeException e) {
            deleteOwnedFile(activeBackup);
            deleteOwnedFile(archiveBackup);
            deleteOwnedFile(journalPath);
            throw e;
        }
    }

    /** Records the phase after the active file has been replaced. */
    void markActiveCommitted(Path activeFile, Path archiveFile,
                             StorageFileManager.FileSnapshot activeSnapshot,
                             StorageFileManager.FileSnapshot archiveSnapshot) throws IOException {
        writePhase(activeFile, archiveFile, activeSnapshot, archiveSnapshot, Phase.ACTIVE_COMMITTED);
    }

    /** Records the phase after both new files have been replaced. */
    void markCommitted(Path activeFile, Path archiveFile,
                       StorageFileManager.FileSnapshot activeSnapshot,
                       StorageFileManager.FileSnapshot archiveSnapshot) throws IOException {
        writePhase(activeFile, archiveFile, activeSnapshot, archiveSnapshot, Phase.COMMITTED);
    }

    /** Removes a completed transaction's journal and backups. */
    void cleanUp() {
        deleteOwnedFile(activeBackup);
        deleteOwnedFile(archiveBackup);
        deleteOwnedFile(journalPath);
    }

    /** Returns the recovery journal location for a user-facing recovery message. */
    String getRecoveryLocation() {
        return journalPath.toAbsolutePath().normalize().toString();
    }

    /** Recovers an interrupted transaction belonging to the supplied data paths. */
    static void recoverPendingTransaction(Path activeFile, Path archiveFile) throws HertaException {
        StorageTransactionRecovery.recoverPendingTransaction(activeFile, archiveFile);
    }

    /** Writes a journal phase and forces it before replacement continues. */
    private void writePhase(Path activeFile, Path archiveFile,
                            StorageFileManager.FileSnapshot activeSnapshot,
                            StorageFileManager.FileSnapshot archiveSnapshot, Phase phase)
            throws IOException {
        Properties properties = createProperties(activeFile, archiveFile, activeSnapshot,
                archiveSnapshot, phase);
        String serializedProperties = serializeProperties(properties);
        Path temporaryJournal = null;
        try {
            temporaryJournal = StorageFileManager.createOwnedTemporaryFile(
                    journalPath.getParent(), ".herta-transaction-", ".tmp");
            Files.writeString(temporaryJournal, serializedProperties, StandardCharsets.UTF_8,
                    StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
            forcePath(temporaryJournal);
            replaceJournal(temporaryJournal);
            temporaryJournal = null;
        } finally {
            deleteOwnedFile(temporaryJournal);
        }
    }

    /** Builds the complete schema written for every journal phase. */
    private Properties createProperties(Path activeFile, Path archiveFile,
                                        StorageFileManager.FileSnapshot activeSnapshot,
                                        StorageFileManager.FileSnapshot archiveSnapshot,
                                        Phase phase) {
        Properties properties = new Properties();
        properties.setProperty(ACTIVE_PATH_KEY, normalize(activeFile).toString());
        properties.setProperty(ARCHIVE_PATH_KEY, normalize(archiveFile).toString());
        properties.setProperty(ACTIVE_PRESENT_KEY, Boolean.toString(activeSnapshot.wasPresent()));
        properties.setProperty(ARCHIVE_PRESENT_KEY, Boolean.toString(archiveSnapshot.wasPresent()));
        properties.setProperty(ACTIVE_BACKUP_KEY, activeBackup.toString());
        properties.setProperty(ARCHIVE_BACKUP_KEY, archiveBackup.toString());
        properties.setProperty(PHASE_KEY, phase.name());
        return properties;
    }

    /** Replaces the journal atomically when the file system supports it. */
    private void replaceJournal(Path temporaryJournal) throws IOException {
        try {
            Files.move(temporaryJournal, journalPath, StandardCopyOption.ATOMIC_MOVE,
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException | FileAlreadyExistsException
                 | AccessDeniedException e) {
            Files.move(temporaryJournal, journalPath, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /** Creates and forces one owned backup, or records the original file's absence. */
    private static Path createBackup(Path directory, String fileRole,
                                     StorageFileManager.FileSnapshot snapshot) throws IOException {
        if (!snapshot.wasPresent()) {
            return ABSENT_BACKUP_PATH;
        }
        Path backup = StorageFileManager.createOwnedTemporaryFile(directory,
                ".herta-" + fileRole + "-", ".bak");
        Files.write(backup, snapshot.contents(), StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE);
        forcePath(backup);
        return backup;
    }

    /** Forces a path so a journal phase or backup survives a power loss. */
    static void forcePath(Path path) throws IOException {
        try (FileChannel channel = FileChannel.open(path, StandardOpenOption.WRITE)) {
            channel.force(true);
        }
    }

    /** Deletes a transaction-owned path while keeping cleanup failures non-fatal. */
    static void deleteOwnedFile(Path path) {
        if (path == null || ABSENT_BACKUP.equals(path.toString())) {
            return;
        }
        try {
            Files.deleteIfExists(path);
        } catch (IOException | SecurityException e) {
            LOGGER.log(Level.WARNING, "Unable to clean up Herta transaction file: " + path, e);
        }
    }

    /** Returns the directory beside a data file. */
    static Path getDataDirectory(Path dataFile) {
        Path directory = normalize(dataFile).getParent();
        return directory == null ? Path.of(".").toAbsolutePath().normalize() : directory;
    }

    /** Returns the journal beside the active file. */
    static Path getJournalPath(Path activeFile) {
        return getDataDirectory(activeFile).resolve(JOURNAL_FILE_NAME);
    }

    /** Returns a normalized absolute path for journal ownership comparisons. */
    static Path normalize(Path path) {
        return path.toAbsolutePath().normalize();
    }

    /** Serializes journal properties before they are forced to disk. */
    private static String serializeProperties(Properties properties) throws IOException {
        try (StringWriter output = new StringWriter()) {
            properties.store(output, "Herta transaction journal");
            return output.toString();
        }
    }
}
