package friday.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import friday.exception.FridayException;
import friday.model.Deadline;
import friday.model.Event;
import friday.model.Task;
import friday.model.TaskList;
import friday.model.ToDo;

/**
 * Handles reading and writing Friday's task data file.
 */
public class Storage {
    /**
     * The path of the file managed by this storage instance.
     */
    private final Path dataFile;

    /**
     * Creates storage for a relative or absolute file path.
     *
     * @param filePath File path used for persistence.
     */
    public Storage(String filePath) {
        if (filePath == null || filePath.isBlank()) {
            throw new IllegalArgumentException("The task data file path cannot be empty.");
        }
        try {
            this.dataFile = Path.of(filePath);
        } catch (InvalidPathException e) {
            throw new IllegalArgumentException("The task data file path is invalid.", e);
        }
    }

    /**
     * Loads saved tasks and records any malformed lines as warnings.
     *
     * @return Loaded tasks together with warnings for skipped records.
     * @throws IOException If an existing file cannot be read.
     */
    public LoadResult load() throws IOException {
        ArrayList<Task> tasks = new ArrayList<>();
        ArrayList<String> warnings = new ArrayList<>();
        if (Files.notExists(dataFile)) {
            return new LoadResult(tasks, warnings);
        }

        List<String> taskLines;
        try {
            taskLines = Files.readAllLines(dataFile, StandardCharsets.UTF_8);
        } catch (SecurityException e) {
            throw new IOException("Access to the task data file was denied.", e);
        }
        for (int i = 0; i < taskLines.size(); i++) {
            try {
                Task task = parseSavedTask(taskLines.get(i));
                if (tasks.stream().anyMatch(existing -> existing.hasSameDetails(task))) {
                    warnings.add(String.format("I skipped duplicate task data on line %d.", i + 1));
                } else {
                    tasks.add(task);
                }
            } catch (FridayException e) {
                warnings.add(String.format("I skipped corrupted task data on line %d: %s", i + 1, e.getMessage()));
            }
        }
        return new LoadResult(tasks, warnings);
    }

    /**
     * Replaces the data file with the current task list.
     *
     * @param tasks Tasks to save.
     * @throws IOException If the data directory or file cannot be written.
     */
    public void save(TaskList tasks) throws IOException {
        Path parent = dataFile.getParent();
        try {
            if (parent != null) {
                Files.createDirectories(parent);
            }

            List<String> taskLines = tasks.asList().stream()
                    .map(Task::toFileString)
                    .toList();
            Files.write(dataFile, taskLines, StandardCharsets.UTF_8);
        } catch (SecurityException e) {
            throw new IOException("Access to the task data file was denied.", e);
        }
    }

    /**
     * Holds successfully loaded tasks and non-fatal record warnings.
     */
    public static final class LoadResult {
        private final List<Task> tasks;
        private final List<String> warnings;

        private LoadResult(List<Task> tasks, List<String> warnings) {
            this.tasks = Collections.unmodifiableList(new ArrayList<>(tasks));
            this.warnings = Collections.unmodifiableList(new ArrayList<>(warnings));
        }

        /**
         * Returns the successfully reconstructed tasks from the load operation.
         *
         * @return The successfully reconstructed tasks.
         */
        public List<Task> getTasks() {
            return tasks;
        }

        /**
         * Returns warnings for records that could not be reconstructed.
         *
         * @return Warnings describing skipped malformed records.
         */
        public List<String> getWarnings() {
            return warnings;
        }
    }

    /**
     * Reconstructs a task from one pipe-separated saved record.
     *
     * @param taskLine One saved record.
     * @return The reconstructed task.
     * @throws FridayException If the record is malformed.
     */
    private static Task parseSavedTask(String taskLine) throws FridayException {
        String[] fields = splitSavedTaskFields(taskLine);
        if (fields.length < 3) {
            throw new FridayException("expected a task type, status, and description.");
        }

        String taskType = fields[0];
        String status = fields[1];
        String description = fields[2];

        int expectedFieldCount;
        switch (taskType) {
            case "T":
                expectedFieldCount = 3;
                break;
            case "D":
                expectedFieldCount = 4;
                break;
            case "E":
                expectedFieldCount = 5;
                break;
            default:
                throw new FridayException("unknown task type '" + taskType + "'.");
        }

        if (fields.length != expectedFieldCount) {
            throw new FridayException("wrong number of fields for task type '" + taskType + "'.");
        }
        if (!status.equals("0") && !status.equals("1")) {
            throw new FridayException("completion status must be 0 or 1.");
        }
        if (description.isBlank()) {
            throw new FridayException("task description cannot be empty.");
        }

        Task task;
        switch (taskType) {
            case "T":
                task = new ToDo(description);
                break;
            case "D":
                if (fields[3].isBlank()) {
                    throw new FridayException("deadline date cannot be empty.");
                }
                task = new Deadline(description, parseSavedDate(fields[3]));
                break;
            case "E":
                if (fields[3].isBlank() || fields[4].isBlank()) {
                    throw new FridayException("event start and end times cannot be empty.");
                }
                LocalDateTime from = parseSavedDateTime(fields[3]);
                LocalDateTime to = parseSavedDateTime(fields[4]);
                if (!from.isBefore(to)) {
                    throw new FridayException("event start must be before event end.");
                }
                task = new Event(description, from, to);
                break;
            default:
                throw new AssertionError("Task type was validated earlier.");
        }

        if (status.equals("1")) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Parses a saved deadline date.
     *
     * @param value Saved ISO date text.
     * @return The parsed date.
     * @throws FridayException If the text is not a valid date.
     */
    private static LocalDate parseSavedDate(String value) throws FridayException {
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException e) {
            throw new FridayException("deadline date is not a valid yyyy-MM-dd date.");
        }
    }

    /**
     * Parses a saved event date-time.
     *
     * @param value Saved ISO date-time text.
     * @return The parsed date-time.
     * @throws FridayException If the text is not a valid date-time.
     */
    private static LocalDateTime parseSavedDateTime(String value) throws FridayException {
        try {
            return LocalDateTime.parse(value);
        } catch (DateTimeParseException e) {
            throw new FridayException("event time is not a valid saved date-time.");
        }
    }

    /**
     * Splits a saved record while honoring escaped pipe and backslash characters.
     *
     * @param taskLine One saved record.
     * @return The unescaped fields from the record.
     */
    private static String[] splitSavedTaskFields(String taskLine) {
        List<String> fields = new ArrayList<>();
        StringBuilder currentField = new StringBuilder();

        for (int i = 0; i < taskLine.length(); i++) {
            char currentCharacter = taskLine.charAt(i);
            if (currentCharacter == '\\' && i + 1 < taskLine.length()) {
                char nextCharacter = taskLine.charAt(i + 1);
                if (nextCharacter == '\\' || nextCharacter == '|') {
                    currentField.append(nextCharacter);
                    i++;
                    continue;
                }
            }

            boolean isSeparator = currentCharacter == ' '
                    && i + 2 < taskLine.length()
                    && taskLine.charAt(i + 1) == '|'
                    && taskLine.charAt(i + 2) == ' ';
            if (isSeparator) {
                fields.add(currentField.toString());
                currentField.setLength(0);
                i += 2;
            } else {
                currentField.append(currentCharacter);
            }
        }

        fields.add(currentField.toString());
        return fields.toArray(new String[0]);
    }
}
