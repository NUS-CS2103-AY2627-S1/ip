package tony.storage;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import tony.task.Deadline;
import tony.task.Event;
import tony.task.Task;
import tony.task.TaskList;
import tony.task.Todo;

/**
 * Loads and saves the current task list using a text file.
 */
public class Storage {
    /** Largest data file read automatically, to avoid exhausting memory on a corrupt file. */
    private static final long MAX_DATA_FILE_SIZE_BYTES = 10 * 1024 * 1024;

    /**
     * Contains the valid tasks loaded from disk and the number of invalid lines skipped.
     */
    public static class LoadResult {
        /** Valid tasks reconstructed from the data file. */
        private final ArrayList<Task> tasks;

        /** Number of non-blank lines that could not be parsed. */
        private final int skippedLineCount;

        private LoadResult(ArrayList<Task> tasks, int skippedLineCount) {
            assert tasks != null : "A load result must contain a task collection";
            assert skippedLineCount >= 0 : "A skipped-line count cannot be negative";
            this.tasks = tasks;
            this.skippedLineCount = skippedLineCount;
        }

        /**
         * Returns the valid tasks reconstructed from disk.
         *
         * @return the loaded tasks.
         */
        public ArrayList<Task> getTasks() {
            return tasks;
        }

        /**
         * Returns how many malformed non-blank lines were ignored.
         *
         * @return the number of skipped lines.
         */
        public int getSkippedLineCount() {
            return skippedLineCount;
        }
    }

    /** The file to which tasks are saved. */
    private final Path filePath;

    /**
     * Creates storage that writes to the given file.
     *
     * @param filePath the task data file.
     */
    public Storage(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Loads all tasks stored in the data file.
     * A missing file represents a new user with an empty task list.
     *
     * Malformed lines are skipped so that other valid tasks can still be recovered.
     *
     * @return the valid tasks and number of skipped lines.
     * @throws IOException if the file cannot be read.
     */
    public LoadResult load() throws IOException {
        ArrayList<Task> tasks = new ArrayList<>();
        if (Files.notExists(filePath)) {
            return new LoadResult(tasks, 0);
        }
        if (Files.size(filePath) > MAX_DATA_FILE_SIZE_BYTES) {
            throw new IOException("Task data file is unexpectedly large");
        }

        int skippedLineCount = 0;
        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String taskLine;
            while ((taskLine = reader.readLine()) != null) {
                if (!taskLine.isBlank()) {
                    try {
                        Task task = parseTask(taskLine);
                        boolean isDuplicate = tasks.stream()
                                .anyMatch(existingTask -> existingTask.hasSameDetails(task));
                        if (isDuplicate) {
                            skippedLineCount++;
                        } else {
                            tasks.add(task);
                        }
                    } catch (IllegalArgumentException exception) {
                        skippedLineCount++;
                    }
                }
            }
        }
        return new LoadResult(tasks, skippedLineCount);
    }

    /**
     * Reconstructs one task from its pipe-separated storage representation.
     *
     * @param taskLine one line read from the data file.
     * @return the reconstructed task.
     */
    private Task parseTask(String taskLine) {
        List<String> fields = splitFields(taskLine);
        String taskType = getRequiredField(fields, 0);
        int expectedFieldCount = switch (taskType) {
            case "T" -> 3;
            case "D" -> 4;
            case "E" -> 5;
            default -> throw new IllegalArgumentException("Unknown task type: " + taskType);
        };
        if (fields.size() != expectedFieldCount) {
            throw new IllegalArgumentException("Wrong number of task fields");
        }
        assert taskType.equals("T") || taskType.equals("D") || taskType.equals("E")
                : "The task type must have been validated before reconstruction";

        String status = getRequiredField(fields, 1);
        if (!status.equals("0") && !status.equals("1")) {
            throw new IllegalArgumentException("Invalid task status: " + status);
        }

        Task task = switch (taskType) {
            case "T" -> new Todo(getRequiredField(fields, 2));
            case "D" -> new Deadline(getRequiredField(fields, 2), parseStoredDate(getRequiredField(fields, 3)));
            case "E" -> new Event(getRequiredField(fields, 2), parseStoredDate(getRequiredField(fields, 3)),
                    parseStoredDate(getRequiredField(fields, 4)));
            default -> throw new IllegalStateException("Task type was already validated");
        };

        if (status.equals("1")) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Parses a date stored in the ISO {@code yyyy-MM-dd} format.
     *
     * @param dateText the stored date text.
     * @return the parsed date.
     * @throws IllegalArgumentException if the date is malformed or impossible.
     */
    private LocalDate parseStoredDate(String dateText) {
        try {
            return LocalDate.parse(dateText);
        } catch (DateTimeParseException exception) {
            throw new IllegalArgumentException("Invalid stored date: " + dateText, exception);
        }
    }

    /**
     * Splits a stored line at unescaped pipe characters and unescapes its fields.
     * Legacy backslashes that do not escape a pipe or another backslash are preserved.
     *
     * @param taskLine one stored task line.
     * @return the decoded fields.
     */
    private List<String> splitFields(String taskLine) {
        ArrayList<String> fields = new ArrayList<>();
        StringBuilder currentField = new StringBuilder();

        for (int index = 0; index < taskLine.length(); index++) {
            char currentCharacter = taskLine.charAt(index);
            if (currentCharacter == '\\' && index + 1 < taskLine.length()) {
                char nextCharacter = taskLine.charAt(index + 1);
                if (nextCharacter == '\\' || nextCharacter == '|') {
                    currentField.append(nextCharacter);
                    index++;
                    continue;
                }
            }

            if (currentCharacter == '|') {
                fields.add(currentField.toString().trim());
                currentField.setLength(0);
            } else {
                currentField.append(currentCharacter);
            }
        }
        fields.add(currentField.toString().trim());
        return fields;
    }

    /**
     * Returns a required, non-empty field from a parsed task line.
     *
     * @param fields all parsed fields.
     * @param index index of the required field.
     * @return the requested non-empty value.
     */
    private String getRequiredField(List<String> fields, int index) {
        if (index >= fields.size() || fields.get(index).isBlank()) {
            throw new IllegalArgumentException("Missing required task field");
        }
        return fields.get(index);
    }

    /**
     * Rewrites the data file so it represents the complete current task list.
     *
     * @param tasks the task list to save.
     * @throws IOException if the directory or file cannot be written.
     */
    public void save(TaskList tasks) throws IOException {
        Path parentDirectory = filePath.getParent();
        if (parentDirectory == null) {
            parentDirectory = Path.of(".");
        }
        Files.createDirectories(parentDirectory);
        List<String> taskLines = tasks.getTasks().stream()
                .map(Task::toDataString)
                .toList();

        Path temporaryFile = Files.createTempFile(parentDirectory, "tony-", ".tmp");
        try {
            Files.write(temporaryFile, taskLines);
            try {
                Files.move(temporaryFile, filePath, StandardCopyOption.ATOMIC_MOVE,
                        StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException exception) {
                Files.move(temporaryFile, filePath, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(temporaryFile);
        }
    }
}
