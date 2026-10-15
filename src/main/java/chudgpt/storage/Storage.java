package chudgpt.storage;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import chudgpt.exception.ChudException;
import chudgpt.parser.Parser;
import chudgpt.task.Deadline;
import chudgpt.task.Event;
import chudgpt.task.Priority;
import chudgpt.task.Task;
import chudgpt.task.TaskList;
import chudgpt.task.ToDo;

/** Saves tasks to and loads tasks from the application's data file. */
public class Storage {
    private final Path saveFile;
    private boolean shouldBackUpBeforeSave;

    /**
     * Creates storage backed by the specified file.
     *
     * @param filePath the file path used to store tasks.
     */
    public Storage(String filePath) {
        assert filePath != null : "Save file path should not be null";
        assert !filePath.isBlank() : "Save file path should not be blank";
        saveFile = Path.of(filePath).toAbsolutePath().normalize();
    }

    /**
     * Saves the current task list using a temporary file and atomic replacement when available.
     *
     * @param tasks the tasks to save.
     * @throws ChudException if the task list cannot be saved safely.
     */
    public void save(TaskList tasks) throws ChudException {
        assert tasks != null : "Task list to save should not be null";
        Path parentDirectory = saveFile.getParent();
        if (parentDirectory == null) {
            throw new ChudException("Could not determine the parent directory for " + saveFile + ".");
        }
        Path temporaryFile = null;
        try {
            Files.createDirectories(parentDirectory);
            backUpSaveFileIfRequired();
            temporaryFile = createTemporarySaveFile(parentDirectory);
            Files.writeString(temporaryFile, tasks.toFileFormat(), StandardCharsets.UTF_8);
            replaceSaveFile(temporaryFile);
            temporaryFile = null;
            shouldBackUpBeforeSave = false;
        } catch (IOException | SecurityException e) {
            throw new ChudException("Could not save tasks to " + saveFile
                    + ". Your changes are still available in this session.", e);
        } finally {
            deleteTemporaryFile(temporaryFile);
        }
    }

    /** Creates a temporary file with a prefix accepted by {@link Files#createTempFile}. */
    private Path createTemporarySaveFile(Path parentDirectory) throws IOException {
        String fileName = saveFile.getFileName().toString();
        String prefix = fileName.length() >= 3 ? fileName : (fileName + "___").substring(0, 3);
        return Files.createTempFile(parentDirectory, prefix, ".tmp");
    }

    /** Creates a recovery copy before replacing a save file that contained malformed records. */
    private void backUpSaveFileIfRequired() throws IOException {
        if (!shouldBackUpBeforeSave || !Files.exists(saveFile)) {
            return;
        }
        Path backupFile = saveFile.resolveSibling(saveFile.getFileName() + ".bak");
        Files.copy(saveFile, backupFile, StandardCopyOption.REPLACE_EXISTING);
    }

    /** Replaces the save file atomically when supported by the current file system. */
    private void replaceSaveFile(Path temporaryFile) throws IOException {
        try {
            Files.move(temporaryFile, saveFile, StandardCopyOption.ATOMIC_MOVE,
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(temporaryFile, saveFile, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /** Removes an unused temporary save file after a failed save attempt. */
    private void deleteTemporaryFile(Path temporaryFile) {
        if (temporaryFile == null) {
            return;
        }
        try {
            Files.deleteIfExists(temporaryFile);
        } catch (IOException | SecurityException e) {
            System.err.println("Could not remove temporary save file: " + temporaryFile);
        }
    }

    /**
     * Reads the save file and returns all valid saved tasks.
     *
     * @return valid tasks in save-file order.
     * @throws ChudException if the save file cannot be read.
     */
    public List<Task> load() throws ChudException {
        return loadWithWarnings().tasks();
    }

    /**
     * Reads valid tasks while collecting line-specific warnings for malformed records.
     *
     * @return valid tasks and any warnings produced while loading.
     * @throws ChudException if the save file cannot be read.
     */
    public LoadResult loadWithWarnings() throws ChudException {
        if (Files.notExists(saveFile)) {
            return new LoadResult(List.of(), List.of());
        }
        if (!Files.exists(saveFile)) {
            shouldBackUpBeforeSave = true;
            throw new ChudException("The task data path cannot be accessed: " + saveFile);
        }
        if (!Files.isRegularFile(saveFile)) {
            shouldBackUpBeforeSave = true;
            throw new ChudException("The task data path is not a regular file: " + saveFile);
        }

        List<Task> tasks = new ArrayList<>();
        List<Integer> sourceLineNumbers = new ArrayList<>();
        List<String> warnings = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(saveFile, StandardCharsets.UTF_8)) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                loadRecord(line, lineNumber, tasks, sourceLineNumbers, warnings);
            }
        } catch (IOException | SecurityException e) {
            shouldBackUpBeforeSave = true;
            throw new ChudException("Could not read tasks from " + saveFile + ".", e);
        }

        shouldBackUpBeforeSave = !warnings.isEmpty();
        return new LoadResult(tasks, warnings);
    }

    /** Adds one valid, nonduplicate saved record or records a warning for its line. */
    private void loadRecord(String line, int lineNumber, List<Task> tasks, List<Integer> sourceLineNumbers,
                            List<String> warnings) {
        try {
            Task task = parseSavedTask(line);
            int duplicateIndex = findDuplicateIndex(tasks, task);
            if (duplicateIndex >= 0) {
                warnings.add("Line " + lineNumber + " duplicates the task on line "
                        + sourceLineNumbers.get(duplicateIndex) + " and was skipped.");
                return;
            }
            tasks.add(task);
            sourceLineNumbers.add(lineNumber);
        } catch (ChudException e) {
            warnings.add("Line " + lineNumber + " was skipped: " + e.getMessage());
        }
    }

    /** Returns the index of a saved task with the same details, or {@code -1} when none exists. */
    private int findDuplicateIndex(List<Task> tasks, Task candidate) {
        for (int index = 0; index < tasks.size(); index++) {
            if (tasks.get(index).hasSameDetails(candidate)) {
                return index;
            }
        }
        return -1;
    }

    /** Returns the task represented by a saved task record. */
    private Task parseSavedTask(String line) throws ChudException {
        String[] parts = line.split("\\s*\\|\\s*", -1);
        if (parts.length < 3) {
            throw new ChudException("The record does not contain enough fields.");
        }

        boolean isCompleted = parseCompletionStatus(parts[1]);
        int legacyFieldCount = getLegacyFieldCount(parts[0]);
        if (parts.length != legacyFieldCount && parts.length != legacyFieldCount + 1) {
            throw new ChudException("The record has an unexpected number of fields.");
        }

        Priority priority = Priority.NONE;
        if (parts.length == legacyFieldCount + 1) {
            try {
                priority = Priority.valueOf(parts[2]);
            } catch (IllegalArgumentException e) {
                throw new ChudException("The priority value is invalid.", e);
            }

            String[] legacyParts = new String[legacyFieldCount];
            System.arraycopy(parts, 0, legacyParts, 0, 2);
            System.arraycopy(parts, 3, legacyParts, 2, legacyFieldCount - 2);
            parts = legacyParts;
        }

        Task task = createTask(parts);
        task.setCompleted(isCompleted);
        task.setPriority(priority);
        return task;
    }

    /** Returns the completion status represented by a saved value. */
    private boolean parseCompletionStatus(String value) throws ChudException {
        return switch (value) {
            case "0" -> false;
            case "1" -> true;
            default -> throw new ChudException("The completion status must be 0 or 1.");
        };
    }

    /** Returns the number of fields used by a legacy record of the supplied type. */
    private int getLegacyFieldCount(String taskType) throws ChudException {
        return switch (taskType) {
            case "T" -> 3;
            case "D" -> 4;
            case "E" -> 5;
            default -> throw new ChudException("The task type is invalid.");
        };
    }

    /** Returns the task represented by validated legacy-format fields. */
    private Task createTask(String[] parts) throws ChudException {
        return switch (parts[0]) {
            case "T" -> createToDo(parts);
            case "D" -> createDeadline(parts);
            case "E" -> createEvent(parts);
            default -> throw new ChudException("The task type is invalid.");
        };
    }

    /** Returns a to-do task after validating its saved description. */
    private Task createToDo(String[] parts) throws ChudException {
        return new ToDo(Parser.validateDescription(parts[2]));
    }

    /** Returns a deadline task after validating its saved fields. */
    private Task createDeadline(String[] parts) throws ChudException {
        String description = Parser.validateDescription(parts[2]);
        if (parts[3].isBlank()) {
            throw new ChudException("Deadline date cannot be empty.");
        }
        return new Deadline(description, Parser.parseDate(parts[3]));
    }

    /** Returns an event task after validating its saved fields. */
    private Task createEvent(String[] parts) throws ChudException {
        String description = Parser.validateDescription(parts[2]);
        if (parts[3].isBlank() || parts[4].isBlank()) {
            throw new ChudException("Event start and end dates cannot be empty.");
        }
        LocalDate start = Parser.parseDate(parts[3]);
        LocalDate end = Parser.parseDate(parts[4]);
        Parser.validateEventDates(start, end);
        return new Event(description, start, end);
    }

    /**
     * Contains valid loaded tasks and line-specific warnings for skipped records.
     *
     * @param tasks valid tasks read from the save file.
     * @param warnings descriptions of records that were skipped.
     */
    public record LoadResult(List<Task> tasks, List<String> warnings) {
        /**
         * Creates an immutable load result.
         *
         * @param tasks valid tasks read from the save file.
         * @param warnings descriptions of records that were skipped.
         */
        public LoadResult {
            tasks = List.copyOf(tasks);
            warnings = List.copyOf(warnings);
        }
    }
}
