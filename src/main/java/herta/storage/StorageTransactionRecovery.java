package herta.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import herta.exception.HertaException;

/**
 * Validates and recovers interrupted active/archive storage transactions.
 */
final class StorageTransactionRecovery {
    private static final Logger LOGGER = Logger.getLogger(StorageTransactionRecovery.class.getName());

    private StorageTransactionRecovery() {
        // Utility class; do not instantiate.
    }

    /** Recovers an interrupted transaction while excluding cooperating writers. */
    static void recoverPendingTransaction(Path activeFile, Path archiveFile) throws HertaException {
        try (StorageFileLock ignored = StorageFileLock.acquire(activeFile, archiveFile)) {
            recoverWhileLocked(activeFile, archiveFile);
        } catch (HertaException e) {
            throw e;
        } catch (IOException | RuntimeException e) {
            LOGGER.log(Level.SEVERE, "Unable to inspect the Herta transaction journal.", e);
            throw createRecoveryFailureException();
        }
    }

    /** Performs recovery only after the journal has been found and validated. */
    private static void recoverWhileLocked(Path activeFile, Path archiveFile) throws HertaException {
        Path journalPath = StorageTransactionJournal.getJournalPath(activeFile);
        if (isJournalMissing(journalPath)) {
            cleanStaleTemporaryFiles(activeFile);
            return;
        }
        try {
            Properties properties = loadProperties(journalPath);
            StorageTransactionJournal.Phase phase = validateOwnership(properties, activeFile,
                    archiveFile);
            if (phase == StorageTransactionJournal.Phase.COMMITTED) {
                deleteBackupFiles(properties);
            } else {
                restorePath(properties, StorageTransactionJournal.ACTIVE_PATH_KEY,
                        StorageTransactionJournal.ACTIVE_PRESENT_KEY,
                        StorageTransactionJournal.ACTIVE_BACKUP_KEY);
                restorePath(properties, StorageTransactionJournal.ARCHIVE_PATH_KEY,
                        StorageTransactionJournal.ARCHIVE_PRESENT_KEY,
                        StorageTransactionJournal.ARCHIVE_BACKUP_KEY);
                deleteBackupFiles(properties);
            }
            StorageTransactionJournal.deleteOwnedFile(journalPath);
            cleanStaleTemporaryFiles(activeFile);
        } catch (IOException | RuntimeException e) {
            LOGGER.log(Level.SEVERE, "Unable to validate the Herta transaction journal.", e);
            throw createRecoveryFailureException();
        }
    }

    /** Deletes only the validated backup paths recorded in a journal. */
    private static void deleteBackupFiles(Properties properties) {
        StorageTransactionJournal.deleteOwnedFile(Path.of(properties.getProperty(
                StorageTransactionJournal.ACTIVE_BACKUP_KEY)));
        StorageTransactionJournal.deleteOwnedFile(Path.of(properties.getProperty(
                StorageTransactionJournal.ARCHIVE_BACKUP_KEY)));
    }

    /** Loads a journal after rejecting duplicate and unknown properties. */
    private static Properties loadProperties(Path journalPath) throws IOException {
        validatePropertyKeys(journalPath);
        Properties properties = new Properties();
        try (var reader = Files.newBufferedReader(journalPath, StandardCharsets.UTF_8)) {
            properties.load(reader);
        }
        if (!properties.stringPropertyNames().equals(StorageTransactionJournal.JOURNAL_KEYS)) {
            throw new IOException("Storage transaction journal schema is invalid.");
        }
        return properties;
    }

    /** Rejects duplicate or unknown property declarations before Properties resolves them. */
    private static void validatePropertyKeys(Path journalPath) throws IOException {
        Set<String> declaredKeys = new HashSet<>();
        try (var reader = Files.newBufferedReader(journalPath, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String key = extractPropertyKey(line);
                if (!key.isEmpty() && (!StorageTransactionJournal.JOURNAL_KEYS.contains(key)
                        || !declaredKeys.add(key))) {
                    throw new IOException("Storage transaction journal schema is invalid.");
                }
            }
        }
    }

    /** Returns the simple key in one generated journal property line. */
    private static String extractPropertyKey(String line) {
        String trimmedLine = line.trim();
        if (trimmedLine.isEmpty() || trimmedLine.startsWith("#") || trimmedLine.startsWith("!")) {
            return "";
        }
        int separatorIndex = trimmedLine.indexOf('=');
        if (separatorIndex < 0) {
            separatorIndex = trimmedLine.indexOf(':');
        }
        return separatorIndex < 0 ? trimmedLine : trimmedLine.substring(0, separatorIndex).trim();
    }

    /** Validates journal ownership and returns its validated phase. */
    private static StorageTransactionJournal.Phase validateOwnership(Properties properties,
                                                                       Path activeFile,
                                                                       Path archiveFile)
            throws IOException {
        Path normalizedActiveFile = StorageTransactionJournal.normalize(activeFile);
        Path normalizedArchiveFile = StorageTransactionJournal.normalize(archiveFile);
        validatePathProperty(properties, StorageTransactionJournal.ACTIVE_PATH_KEY,
                normalizedActiveFile);
        validatePathProperty(properties, StorageTransactionJournal.ARCHIVE_PATH_KEY,
                normalizedArchiveFile);
        boolean wasActivePresent = isBooleanPropertyTrue(properties,
                StorageTransactionJournal.ACTIVE_PRESENT_KEY);
        boolean wasArchivePresent = isBooleanPropertyTrue(properties,
                StorageTransactionJournal.ARCHIVE_PRESENT_KEY);
        StorageTransactionJournal.Phase phase = StorageTransactionJournal.Phase.fromSerialized(
                properties.getProperty(StorageTransactionJournal.PHASE_KEY));
        Path directory = normalizedActiveFile.getParent();
        validateBackupPath(properties.getProperty(StorageTransactionJournal.ACTIVE_BACKUP_KEY),
                directory, ".herta-active-", wasActivePresent);
        validateBackupPath(properties.getProperty(StorageTransactionJournal.ARCHIVE_BACKUP_KEY),
                directory, ".herta-archive-", wasArchivePresent);
        return phase;
    }

    /** Verifies that a journal path is exactly the normalized owned path. */
    private static void validatePathProperty(Properties properties, String key, Path expectedPath)
            throws IOException {
        String pathText = properties.getProperty(key);
        if (pathText == null || !StorageTransactionJournal.normalize(Path.of(pathText))
                .toString().equals(pathText) || !expectedPath.toString().equals(pathText)) {
            throw new IOException("Storage transaction ownership mismatch.");
        }
    }

    /** Verifies that a backup stays beside the data file and remains a regular file. */
    private static void validateBackupPath(String backupText, Path directory, String prefix,
                                           boolean wasPresent) throws IOException {
        if (!wasPresent && StorageTransactionJournal.ABSENT_BACKUP.equals(backupText)) {
            return;
        }
        if (!wasPresent || backupText == null
                || StorageTransactionJournal.ABSENT_BACKUP.equals(backupText)) {
            throw new IOException("Storage transaction backup is missing.");
        }
        Path backup = StorageTransactionJournal.normalize(Path.of(backupText));
        String fileName = backup.getFileName().toString();
        boolean isBesideDataFile = Objects.equals(backup.getParent(), directory);
        boolean hasExpectedPrefix = fileName.startsWith(prefix);
        boolean hasExpectedSuffix = fileName.endsWith(".bak");
        boolean isNormalizedPath = backup.toString().equals(backupText);
        if (!isBesideDataFile || !hasExpectedPrefix || !hasExpectedSuffix || !isNormalizedPath) {
            throw new IOException("Storage transaction backup ownership mismatch.");
        }
        BasicFileAttributes attributes = Files.readAttributes(backup,
                BasicFileAttributes.class, LinkOption.NOFOLLOW_LINKS);
        if (!attributes.isRegularFile()
                || attributes.size() > StorageFileManager.MAX_STORAGE_FILE_BYTES) {
            throw new IOException("Storage transaction backup is not readable.");
        }
    }

    /** Returns whether a journal boolean property is exactly the literal {@code true}. */
    private static boolean isBooleanPropertyTrue(Properties properties, String key)
            throws IOException {
        String text = properties.getProperty(key);
        if ("true".equals(text)) {
            return true;
        }
        if ("false".equals(text)) {
            return false;
        }
        throw new IOException("Storage transaction journal boolean is invalid.");
    }

    /** Restores one snapshot from its owned backup or restores its recorded absence. */
    private static void restorePath(Properties properties, String pathKey, String presentKey,
                                    String backupKey) throws IOException {
        Path path = Path.of(properties.getProperty(pathKey));
        boolean wasPresent = isBooleanPropertyTrue(properties, presentKey);
        String backupText = properties.getProperty(backupKey);
        if (!wasPresent || StorageTransactionJournal.ABSENT_BACKUP.equals(backupText)) {
            Files.deleteIfExists(path);
            return;
        }
        Files.copy(Path.of(backupText), path, StandardCopyOption.REPLACE_EXISTING);
        StorageTransactionJournal.forcePath(path);
    }

    /** Removes old Herta temporary files after an otherwise clean startup. */
    private static void cleanStaleTemporaryFiles(Path activeFile) {
        Path directory = StorageTransactionJournal.getDataDirectory(activeFile);
        cleanStaleFiles(directory, ".herta-*.tmp");
        cleanStaleFiles(directory, ".herta-*.bak");
    }

    /** Removes old files that match an owned Herta temporary-file pattern. */
    private static void cleanStaleFiles(Path directory, String pattern) {
        try (DirectoryStream<Path> paths = Files.newDirectoryStream(directory, pattern)) {
            Instant cutoff = Instant.now().minus(StorageTransactionJournal.STALE_FILE_AGE);
            for (Path path : paths) {
                if (Files.getLastModifiedTime(path).toInstant().isBefore(cutoff)) {
                    StorageTransactionJournal.deleteOwnedFile(path);
                }
            }
        } catch (IOException | SecurityException e) {
            LOGGER.log(Level.WARNING, "Unable to inspect Herta temporary files.", e);
        }
    }

    /** Distinguishes a missing journal from one that cannot safely be inspected. */
    private static boolean isJournalMissing(Path journalPath) throws HertaException {
        try {
            BasicFileAttributes attributes = Files.readAttributes(journalPath,
                    BasicFileAttributes.class);
            if (!attributes.isRegularFile()) {
                throw createRecoveryFailureException();
            }
            return false;
        } catch (NoSuchFileException e) {
            return true;
        } catch (IOException | SecurityException e) {
            throw createRecoveryFailureException();
        }
    }

    /** Creates the stable message used when recovery cannot prove ownership or state. */
    private static HertaException createRecoveryFailureException() {
        return new HertaException("Failed to recover interrupted storage transaction safely.");
    }
}
