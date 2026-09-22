package oz.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import oz.exception.OzException;
import oz.task.Task;

/**
 * Handles loading tasks from and saving tasks to a storage file.
 */
public class Storage {
    /** Visual divider line for console output. */
    private static final String DIVIDER =
            "____________________________________________________________\n";

    /** Optional Unicode marker ignored when reading stored lines. */
    private static final String BYTE_ORDER_MARK = "\uFEFF";

    /** Error shown when an existing storage file cannot be overwritten. */
    private static final String READ_ONLY_FILE_MESSAGE =
            "Access denied: storage file is read-only or write-protected.";

    /** Prefix used when writing the task file fails. */
    private static final String SAVE_FAILURE_MESSAGE_PREFIX =
            "Could not save tasks to storage file: ";

    /** Fallback detail for I/O failures without a platform message. */
    private static final String UNKNOWN_IO_FAILURE_MESSAGE = "permission denied.";

    /** Path to the task storage file on disk. */
    private final Path filePath;

    /**
     * Constructs a storage manager for the specified file path.
     *
     * @param filePath Path to the storage file.
     */
    public Storage(String filePath) {
        this.filePath = Path.of(filePath);
    }

    /**
     * Loads valid tasks, skipping and reporting corrupted records.
     *
     * @return Tasks loaded from storage, or an empty list if reading fails.
     */
    public ArrayList<Task> load() {
        if (!Files.exists(this.filePath)) {
            return new ArrayList<>();
        }

        try {
            return parseTaskRecords(Files.readAllLines(this.filePath));
        } catch (IOException exception) {
            reportReadFailure(exception);
            return new ArrayList<>();
        }
    }

    /**
     * Saves tasks to the configured storage file.
     *
     * @param tasks Tasks to save.
     * @throws OzException If the file cannot be written.
     */
    public void save(List<Task> tasks) throws OzException {
        assert tasks != null : "The task collection to save must be non-null";
        assert tasks.stream().noneMatch(task -> task == null)
                : "The task collection to save must not contain null tasks";
        if (Files.exists(this.filePath) && !Files.isWritable(this.filePath)) {
            throw new OzException(READ_ONLY_FILE_MESSAGE);
        }

        try {
            createParentDirectory();
            Files.write(this.filePath, serializeTasks(tasks));
        } catch (IOException exception) {
            String failureDetail = exception.getMessage() == null
                    ? UNKNOWN_IO_FAILURE_MESSAGE
                    : exception.getMessage();
            throw new OzException(SAVE_FAILURE_MESSAGE_PREFIX + failureDetail);
        }
    }

    /**
     * Parses all non-blank records while skipping and reporting corrupted ones.
     *
     * @param storedLines Raw lines read from the storage file.
     * @return Successfully restored tasks.
     */
    private ArrayList<Task> parseTaskRecords(List<String> storedLines) {
        ArrayList<Task> tasks = new ArrayList<>();
        for (int lineIndex = 0; lineIndex < storedLines.size(); lineIndex++) {
            String normalizedLine = normalizeStoredLine(storedLines.get(lineIndex));
            if (normalizedLine.isEmpty()) {
                continue;
            }

            try {
                tasks.add(TaskRecordParser.parse(normalizedLine));
            } catch (OzException exception) {
                reportCorruptedRecord(lineIndex + 1, exception);
            }
        }
        return tasks;
    }

    /**
     * Removes surrounding whitespace and an optional byte-order mark.
     *
     * @param storedLine Raw line read from storage.
     * @return Normalized record, or an empty string for a blank line.
     */
    private String normalizeStoredLine(String storedLine) {
        String normalizedLine = storedLine.trim();
        if (normalizedLine.startsWith(BYTE_ORDER_MARK)) {
            return normalizedLine.substring(BYTE_ORDER_MARK.length()).trim();
        }
        return normalizedLine;
    }

    /**
     * Reports one corrupted record without preventing later records from loading.
     *
     * @param lineNumber One-based storage line number.
     * @param exception Parsing failure for the record.
     */
    private void reportCorruptedRecord(int lineNumber, OzException exception) {
        System.out.println(DIVIDER + "WARNING: Skipping corrupted task entry at line "
                + lineNumber + ": " + exception.getMessage() + "\n" + DIVIDER);
    }

    /**
     * Reports a failure to read the storage file.
     *
     * @param exception File-reading failure.
     */
    private void reportReadFailure(IOException exception) {
        System.out.println(DIVIDER + "OOPS! Could not read tasks from file: "
                + exception.getMessage() + "\n" + DIVIDER);
    }

    /**
     * Creates the storage directory when the configured path has a parent.
     *
     * @throws IOException If the directory cannot be created.
     */
    private void createParentDirectory() throws IOException {
        Path parentDirectory = this.filePath.getParent();
        if (parentDirectory != null) {
            Files.createDirectories(parentDirectory);
        }
    }

    /**
     * Converts tasks to records in their existing order.
     *
     * @param tasks Tasks to serialize.
     * @return Persisted task records.
     */
    private List<String> serializeTasks(List<Task> tasks) {
        List<String> storedLines = new ArrayList<>();
        for (Task task : tasks) {
            storedLines.add(task.toFileFormat());
        }
        return storedLines;
    }
}
