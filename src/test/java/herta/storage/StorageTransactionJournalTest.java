package herta.storage;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.util.List;
import java.util.Properties;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import herta.exception.HertaException;

/**
 * Tests transaction journal creation, recovery, and cleanup.
 */
class StorageTransactionJournalTest {
    private static final String ORIGINAL_ACTIVE = "T | 0 | active";
    private static final String ORIGINAL_ARCHIVE = "D | 1 | archive | 2026-01-02";
    private static final String ACTIVE_PATH_KEY = "activePath";
    private static final String ARCHIVE_PATH_KEY = "archivePath";
    private static final String ACTIVE_PRESENT_KEY = "activePresent";
    private static final String ARCHIVE_PRESENT_KEY = "archivePresent";
    private static final String ACTIVE_BACKUP_KEY = "activeBackup";
    private static final String ARCHIVE_BACKUP_KEY = "archiveBackup";
    private static final String PHASE_KEY = "phase";
    private static final int OVERSIZED_BACKUP_BYTES = 5_000_001;
    private static final FileTime STALE_FILE_TIME =
            FileTime.from(Instant.parse("2000-01-01T00:00:00Z"));

    @TempDir
    private Path temporaryDirectory;

    @Test
    void recover_preparedTransaction_restoresBothOriginals() throws Exception {
        Path activeFile = temporaryDirectory.resolve("herta.txt");
        Path archiveFile = temporaryDirectory.resolve("archive.txt");
        StorageTransactionJournal journal = prepareExistingTransaction(activeFile, archiveFile);

        Files.writeString(activeFile, "new active", StandardCharsets.UTF_8);
        Files.writeString(archiveFile, "new archive", StandardCharsets.UTF_8);
        StorageTransactionJournal.recoverPendingTransaction(activeFile, archiveFile);

        assertEquals(ORIGINAL_ACTIVE, Files.readString(activeFile, StandardCharsets.UTF_8));
        assertEquals(ORIGINAL_ARCHIVE, Files.readString(archiveFile, StandardCharsets.UTF_8));
        assertNoTransactionArtifacts(journal);
    }

    @Test
    void recover_activeCommittedTransaction_restoresAbsenceAndBytes() throws Exception {
        Path activeFile = temporaryDirectory.resolve("herta.txt");
        Path archiveFile = temporaryDirectory.resolve("archive.txt");
        Files.writeString(archiveFile, ORIGINAL_ARCHIVE, StandardCharsets.UTF_8);
        StorageFileManager.FileSnapshot activeSnapshot = new StorageFileManager(activeFile)
                .captureSnapshot();
        StorageFileManager.FileSnapshot archiveSnapshot = new StorageFileManager(archiveFile)
                .captureSnapshot();
        StorageTransactionJournal journal = StorageTransactionJournal.prepare(activeFile, archiveFile,
                activeSnapshot, archiveSnapshot);

        Files.writeString(activeFile, "new active", StandardCharsets.UTF_8);
        Files.writeString(archiveFile, "new archive", StandardCharsets.UTF_8);
        journal.markActiveCommitted(activeFile, archiveFile, activeSnapshot, archiveSnapshot);
        StorageTransactionJournal.recoverPendingTransaction(activeFile, archiveFile);

        assertFalse(Files.exists(activeFile));
        assertEquals(ORIGINAL_ARCHIVE, Files.readString(archiveFile, StandardCharsets.UTF_8));
        assertNoTransactionArtifacts(journal);
    }

    @Test
    void recover_committedTransaction_retainsNewDataAndCleansArtifacts() throws Exception {
        Path activeFile = temporaryDirectory.resolve("herta.txt");
        Path archiveFile = temporaryDirectory.resolve("archive.txt");
        StorageTransactionJournal journal = prepareExistingTransaction(activeFile, archiveFile);
        String newActive = "new active";
        String newArchive = "new archive";

        Files.writeString(activeFile, newActive, StandardCharsets.UTF_8);
        Files.writeString(archiveFile, newArchive, StandardCharsets.UTF_8);
        StorageFileManager.FileSnapshot activeSnapshot = new StorageFileManager(activeFile)
                .captureSnapshot();
        StorageFileManager.FileSnapshot archiveSnapshot = new StorageFileManager(archiveFile)
                .captureSnapshot();
        journal.markCommitted(activeFile, archiveFile, activeSnapshot, archiveSnapshot);
        StorageTransactionJournal.recoverPendingTransaction(activeFile, archiveFile);

        assertEquals(newActive, Files.readString(activeFile, StandardCharsets.UTF_8));
        assertEquals(newArchive, Files.readString(archiveFile, StandardCharsets.UTF_8));
        assertNoTransactionArtifacts(journal);
    }

    @Test
    void cleanUp_ownedArtifactsPreservesUnrelatedFiles() throws Exception {
        Path activeFile = temporaryDirectory.resolve("herta.txt");
        Path archiveFile = temporaryDirectory.resolve("archive.txt");
        StorageTransactionJournal journal = prepareExistingTransaction(activeFile, archiveFile);
        Path unrelatedFile = temporaryDirectory.resolve("unrelated.txt");
        Files.writeString(unrelatedFile, "keep", StandardCharsets.UTF_8);

        journal.cleanUp();
        journal.cleanUp();

        assertTrue(Files.exists(unrelatedFile));
        assertNoTransactionArtifacts(journal);
    }

    @Test
    void recover_invalidJournalSchema_rejectsWithoutChangingData() throws Exception {
        Path activeFile = temporaryDirectory.resolve("herta.txt");
        Path archiveFile = temporaryDirectory.resolve("archive.txt");
        StorageTransactionJournal journal = prepareExistingTransaction(activeFile, archiveFile);
        Path journalPath = Path.of(journal.getRecoveryLocation());
        String validJournalContent = Files.readString(journalPath, StandardCharsets.UTF_8);
        Properties properties = readProperties(journalPath);

        assertInvalidJournal(journalPath, validJournalContent + "unknown=value\n",
                activeFile, archiveFile);
        assertInvalidJournal(journalPath, validJournalContent + ACTIVE_PATH_KEY + "=duplicate\n",
                activeFile, archiveFile);
        for (String missingPropertyName : List.of(ACTIVE_PATH_KEY, ARCHIVE_PATH_KEY,
                ACTIVE_PRESENT_KEY, ARCHIVE_PRESENT_KEY, ACTIVE_BACKUP_KEY,
                ARCHIVE_BACKUP_KEY, PHASE_KEY)) {
            assertInvalidJournal(journalPath, serializeWithout(properties, missingPropertyName),
                    activeFile, archiveFile);
        }
        assertInvalidJournal(journalPath, serializeWith(properties, ACTIVE_PATH_KEY,
                temporaryDirectory.resolve("other.txt").toAbsolutePath().normalize().toString()),
                activeFile, archiveFile);
        assertInvalidJournal(journalPath, serializeWith(properties, ACTIVE_PRESENT_KEY, "maybe"),
                activeFile, archiveFile);
        assertInvalidJournal(journalPath, serializeWith(properties, PHASE_KEY, "UNKNOWN"),
                activeFile, archiveFile);
    }

    @Test
    void recover_invalidBackup_rejectsWithoutChangingData() throws Exception {
        Path activeFile = temporaryDirectory.resolve("herta.txt");
        Path archiveFile = temporaryDirectory.resolve("archive.txt");
        StorageTransactionJournal journal = prepareExistingTransaction(activeFile, archiveFile);
        Path journalPath = Path.of(journal.getRecoveryLocation());
        Properties properties = readProperties(journalPath);
        Path missingBackup = temporaryDirectory.resolve(".herta-active-missing.bak");
        Path oversizedBackup = temporaryDirectory.resolve(".herta-active-oversized.bak");
        Path escapedBackup = temporaryDirectory.getParent().resolve(".herta-active-escaped.bak");

        assertInvalidJournal(journalPath, serializeWith(properties, ACTIVE_BACKUP_KEY,
                missingBackup.toAbsolutePath().normalize().toString()), activeFile, archiveFile);
        Files.write(oversizedBackup, new byte[OVERSIZED_BACKUP_BYTES]);
        assertInvalidJournal(journalPath, serializeWith(properties, ACTIVE_BACKUP_KEY,
                oversizedBackup.toAbsolutePath().normalize().toString()), activeFile, archiveFile);
        assertInvalidJournal(journalPath, serializeWith(properties, ACTIVE_BACKUP_KEY,
                escapedBackup.toAbsolutePath().normalize().toString()), activeFile, archiveFile);
    }

    @Test
    void recover_staleTemporaryFiles_removesOldFilesAndKeepsRecentFiles() throws Exception {
        Path activeFile = temporaryDirectory.resolve("herta.txt");
        Path oldTemporary = temporaryDirectory.resolve(".herta-old.tmp");
        Path recentTemporary = temporaryDirectory.resolve(".herta-recent.tmp");
        Path oldBackup = temporaryDirectory.resolve(".herta-old.bak");
        Path recentBackup = temporaryDirectory.resolve(".herta-recent.bak");
        Files.writeString(oldTemporary, "old", StandardCharsets.UTF_8);
        Files.writeString(recentTemporary, "recent", StandardCharsets.UTF_8);
        Files.writeString(oldBackup, "old", StandardCharsets.UTF_8);
        Files.writeString(recentBackup, "recent", StandardCharsets.UTF_8);
        Files.setLastModifiedTime(oldTemporary, STALE_FILE_TIME);
        Files.setLastModifiedTime(oldBackup, STALE_FILE_TIME);

        StorageTransactionJournal.recoverPendingTransaction(activeFile,
                temporaryDirectory.resolve("archive.txt"));

        assertFalse(Files.exists(oldTemporary));
        assertFalse(Files.exists(oldBackup));
        assertTrue(Files.exists(recentTemporary));
        assertTrue(Files.exists(recentBackup));
    }

    private StorageTransactionJournal prepareExistingTransaction(Path activeFile, Path archiveFile)
            throws IOException {
        Files.writeString(activeFile, ORIGINAL_ACTIVE, StandardCharsets.UTF_8);
        Files.writeString(archiveFile, ORIGINAL_ARCHIVE, StandardCharsets.UTF_8);
        StorageFileManager.FileSnapshot activeSnapshot = new StorageFileManager(activeFile)
                .captureSnapshot();
        StorageFileManager.FileSnapshot archiveSnapshot = new StorageFileManager(archiveFile)
                .captureSnapshot();
        return StorageTransactionJournal.prepare(activeFile, archiveFile, activeSnapshot, archiveSnapshot);
    }

    private void assertInvalidJournal(Path journalPath, String journalText, Path activeFile,
                                      Path archiveFile) throws Exception {
        Files.writeString(journalPath, journalText, StandardCharsets.UTF_8);

        assertThrows(HertaException.class, () ->
                StorageTransactionJournal.recoverPendingTransaction(activeFile, archiveFile));
        assertArrayEquals(ORIGINAL_ACTIVE.getBytes(StandardCharsets.UTF_8), Files.readAllBytes(activeFile));
        assertArrayEquals(ORIGINAL_ARCHIVE.getBytes(StandardCharsets.UTF_8), Files.readAllBytes(archiveFile));
    }

    private Properties readProperties(Path journalPath) throws IOException {
        Properties properties = new Properties();
        try (var reader = Files.newBufferedReader(journalPath, StandardCharsets.UTF_8)) {
            properties.load(reader);
        }
        return properties;
    }

    private String serializeWithout(Properties sourceProperties, String removedPropertyName)
            throws IOException {
        Properties properties = copyProperties(sourceProperties);
        properties.remove(removedPropertyName);
        return serialize(properties);
    }

    private String serializeWith(Properties sourceProperties, String propertyName,
                                 String propertyValue) throws IOException {
        Properties properties = copyProperties(sourceProperties);
        properties.setProperty(propertyName, propertyValue);
        return serialize(properties);
    }

    private Properties copyProperties(Properties sourceProperties) {
        Properties copiedProperties = new Properties();
        copiedProperties.putAll(sourceProperties);
        return copiedProperties;
    }

    private String serialize(Properties properties) throws IOException {
        try (StringWriter writer = new StringWriter()) {
            properties.store(writer, "Herta transaction journal");
            return writer.toString();
        }
    }

    private void assertNoTransactionArtifacts(StorageTransactionJournal journal) throws IOException {
        assertFalse(Files.exists(Path.of(journal.getRecoveryLocation())));
        try (var paths = Files.list(temporaryDirectory)) {
            assertEquals(List.of(), paths.filter(path -> {
                String fileName = path.getFileName().toString();
                return fileName.startsWith(".herta-transaction")
                        || fileName.startsWith(".herta-active-")
                        || fileName.startsWith(".herta-archive-");
            }).toList());
        }
    }
}
