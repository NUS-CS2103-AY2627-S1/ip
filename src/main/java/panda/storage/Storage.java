package panda.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import panda.exception.DataLoadingException;
import panda.exception.DataSavingException;
import panda.exception.InvalidDateException;
import panda.exception.PandaException;
import panda.task.Deadline;
import panda.task.Event;
import panda.task.Task;
import panda.task.TaskStatus;
import panda.task.TaskType;
import panda.task.Todo;

/**
 * Loads and saves Panda tasks using the application's text-file format.
 * This class owns the storage path and keeps persistence details out of the
 * main application coordinator.
 */
public class Storage {
    private final Path dataFile;

    /**
     * Creates a storage service for the supplied file.
     *
     * @param filePath the task data file path.
     */
    public Storage(String filePath) {
        dataFile = Path.of(filePath);
    }

    /**
     * Loads every valid stored task and records errors for malformed lines.
     * A missing file represents a first run and produces an empty result.
     *
     * @return the valid tasks and any recoverable record errors.
     * @throws DataLoadingException if the data file cannot be read.
     */
    public LoadResult load() throws DataLoadingException {
        ArrayList<Task> loadedTasks = new ArrayList<>();
        ArrayList<PandaException> loadingErrors = new ArrayList<>();
        if (Files.notExists(dataFile)) {
            return new LoadResult(loadedTasks, loadingErrors);
        }

        try (Scanner fileScanner = new Scanner(dataFile, StandardCharsets.UTF_8)) {
            int lineNumber = 0;
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                lineNumber++;
                if (!line.isBlank()) {
                    try {
                        loadedTasks.add(parseStoredTask(line, lineNumber));
                    } catch (DataLoadingException | InvalidDateException exception) {
                        loadingErrors.add(exception);
                    }
                }
            }
        } catch (IOException exception) {
            throw new DataLoadingException(dataFile.toString(), exception);
        }
        return new LoadResult(loadedTasks, loadingErrors);
    }

    /**
     * Rewrites the data file so it exactly matches the supplied task list.
     *
     * @param tasks the current tasks in display order.
     * @throws DataSavingException if the destination cannot be created or written.
     */
    public void save(List<Task> tasks) throws DataSavingException {
        String storedData = tasks.stream()
                .map(Task::toDataString)
                .collect(Collectors.joining(System.lineSeparator()));

        try {
            Path parentDirectory = dataFile.getParent();
            if (parentDirectory != null) {
                Files.createDirectories(parentDirectory);
            }
            Files.writeString(dataFile, storedData, StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new DataSavingException(exception);
        }
    }

    /**
     * Converts one pipe-separated data record into its corresponding task.
     *
     * @param line one complete line from the data file.
     * @param lineNumber the one-based line number used in error messages.
     * @return the task represented by the stored record.
     * @throws DataLoadingException if the record does not follow the storage format.
     * @throws InvalidDateException if a stored date is invalid.
     */
    private Task parseStoredTask(String line, int lineNumber)
            throws DataLoadingException, InvalidDateException {
        String[] fields = splitStoredFields(line);
        if (fields.length < 3 || fields[2].isBlank()) {
            throw new DataLoadingException(lineNumber, "no task description.");
        }

        Task task = createStoredTask(fields, lineNumber);
        restoreCompletionStatus(task, fields[1], lineNumber);
        return task;
    }

    /**
     * Validates type-specific fields and creates the represented task subtype.
     */
    private Task createStoredTask(String[] fields, int lineNumber)
            throws DataLoadingException, InvalidDateException {
        TaskType taskType;
        try {
            taskType = TaskType.fromMarker(fields[0]);
        } catch (IllegalArgumentException exception) {
            throw new DataLoadingException(lineNumber,
                    "an invalid task type; expected T, D, or E.");
        }

        Task task;
        switch (taskType) {
            case TODO:
                ensureStoredFieldCount(fields, 3, lineNumber, "todo");
                task = new Todo(fields[2]);
                break;
            case DEADLINE:
                ensureStoredFieldCount(fields, 4, lineNumber, "deadline");
                ensureStoredValue(fields[3], lineNumber, "no deadline time.");
                task = new Deadline(fields[2], fields[3]);
                break;
            case EVENT:
                ensureStoredFieldCount(fields, 5, lineNumber, "event");
                ensureStoredValue(fields[3], lineNumber, "no event start time.");
                ensureStoredValue(fields[4], lineNumber, "no event end time.");
                task = new Event(fields[2], fields[3], fields[4]);
                break;
            default:
                throw new IllegalStateException("Unsupported task type: " + taskType);
        }
        return task;
    }

    /**
     * Restores a task's completion state from its stored status field.
     */
    private void restoreCompletionStatus(Task task, String status, int lineNumber)
            throws DataLoadingException {
        TaskStatus taskStatus;
        try {
            taskStatus = TaskStatus.fromStorageValue(status);
        } catch (IllegalArgumentException exception) {
            throw new DataLoadingException(lineNumber,
                    "an invalid completion status; expected 0 or 1.");
        }

        switch (taskStatus) {
            case DONE:
                task.mark();
                break;
            case NOT_DONE:
                break;
            default:
                throw new IllegalStateException("Unsupported task status: " + taskStatus);
        }
    }

    /**
     * Checks that a stored record has the field count required by its type.
     */
    private void ensureStoredFieldCount(String[] fields, int expectedCount,
            int lineNumber, String taskType) throws DataLoadingException {
        if (fields.length != expectedCount) {
            throw new DataLoadingException(lineNumber,
                    "an invalid " + taskType + " field count; expected "
                            + expectedCount + " fields.");
        }
    }

    /**
     * Checks that a required stored field contains a value.
     */
    private void ensureStoredValue(String value, int lineNumber, String errorMessage)
            throws DataLoadingException {
        if (value.isBlank()) {
            throw new DataLoadingException(lineNumber, errorMessage);
        }
    }

    /**
     * Splits a stored record while preserving escaped pipes and backslashes.
     */
    private String[] splitStoredFields(String line) {
        ArrayList<String> fields = new ArrayList<>();
        StringBuilder currentField = new StringBuilder();
        boolean isEscaping = false;

        for (int i = 0; i < line.length(); i++) {
            char character = line.charAt(i);
            if (isEscaping) {
                if (character != '\\' && character != '|') {
                    currentField.append('\\');
                }
                currentField.append(character);
                isEscaping = false;
            } else if (character == '\\') {
                isEscaping = true;
            } else if (character == '|') {
                fields.add(currentField.toString().trim());
                currentField.setLength(0);
            } else {
                currentField.append(character);
            }
        }
        if (isEscaping) {
            currentField.append('\\');
        }
        fields.add(currentField.toString().trim());
        return fields.toArray(String[]::new);
    }

    /**
     * Groups successfully loaded tasks with recoverable record errors.
     *
     * @param tasks the valid tasks loaded from the file.
     * @param errors the errors for individual records that were skipped.
     */
    public record LoadResult(List<Task> tasks, List<PandaException> errors) {
        /**
         * Prevents callers from changing the collections held by this result.
         */
        public LoadResult {
            tasks = List.copyOf(tasks);
            errors = List.copyOf(errors);
        }
    }
}
