package goat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Saves the current task list to a text file.
 */
public class Storage {
    private final Path filePath;

    /**
     * Creates storage that writes to the given file path.
     *
     * @param filePath path of the task data file
     */
    public Storage(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Loads all tasks from the data file. A missing file represents an empty task list.
     *
     * @return tasks stored in the data file
     * @throws IOException if the file cannot be read
     * @throws GoatException if a saved record is malformed
     */
    public List<Task> load() throws IOException, GoatException {
        List<Task> tasks = new ArrayList<>();
        if (!Files.exists(filePath)) {
            return tasks;
        }

        List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.isBlank()) {
                continue;
            }
            try {
                tasks.add(parseTask(line));
            } catch (GoatException exception) {
                throw new GoatException("Invalid data on line " + (i + 1) + ": "
                        + exception.getMessage());
            }
        }
        return tasks;
    }

    /**
     * Converts one saved record back into its task subtype.
     *
     * @param line saved task record
     * @return reconstructed task
     * @throws GoatException if the record has an unknown type or invalid fields
     */
    private Task parseTask(String line) throws GoatException {
        List<String> fields = splitFields(line);
        if (fields.size() < 2) {
            throw new GoatException("expected a task type and completion status.");
        }

        boolean isDone;
        if (fields.get(1).equals("0")) {
            isDone = false;
        } else if (fields.get(1).equals("1")) {
            isDone = true;
        } else {
            throw new GoatException("completion status must be 0 or 1.");
        }

        Task task;
        switch (fields.get(0)) {
            case "T":
                requireFieldCount(fields, 3, "todo");
                task = new Todo(requireText(fields.get(2), "todo description"));
                break;
            case "D":
                requireFieldCount(fields, 4, "deadline");
                task = new Deadline(requireText(fields.get(2), "deadline description"),
                        TaskDate.parse(requireText(fields.get(3), "deadline date"),
                                "The saved deadline date"));
                break;
            case "E":
                requireFieldCount(fields, 5, "event");
                String description = requireText(fields.get(2), "event description");
                LocalDate startDate = TaskDate.parse(
                        requireText(fields.get(3), "event start date"),
                        "The saved event start date");
                LocalDate endDate = TaskDate.parse(
                        requireText(fields.get(4), "event end date"),
                        "The saved event end date");
                TaskDate.requireStartBeforeEnd(startDate, endDate, "The saved event");
                task = new Event(description, startDate, endDate);
                break;
            default:
                throw new GoatException("unknown task type '" + fields.get(0) + "'.");
        }

        if (isDone) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Splits a record at unescaped pipe delimiters and restores escaped field characters.
     *
     * @param line saved task record
     * @return decoded fields
     */
    private List<String> splitFields(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        for (int i = 0; i < line.length(); i++) {
            char current = line.charAt(i);
            if (current == '\\' && i + 1 < line.length()) {
                char next = line.charAt(i + 1);
                if (next == '\\' || next == '|') {
                    field.append(next);
                    i++;
                    continue;
                }
            }
            if (current == '|') {
                fields.add(field.toString().trim());
                field.setLength(0);
            } else {
                field.append(current);
            }
        }
        fields.add(field.toString().trim());
        return fields;
    }

    /**
     * Validates the number of fields used by a saved task subtype.
     */
    private void requireFieldCount(List<String> fields, int expectedCount, String taskType)
            throws GoatException {
        if (fields.size() != expectedCount) {
            throw new GoatException("a " + taskType + " record needs " + expectedCount
                    + " fields, but found " + fields.size() + ".");
        }
    }

    /**
     * Validates that a required saved text field is not empty.
     */
    private String requireText(String text, String fieldName) throws GoatException {
        if (text.isEmpty()) {
            throw new GoatException(fieldName + " cannot be empty.");
        }
        return text;
    }

    /**
     * Replaces the data file with a serialized copy of the current task list.
     * Creates the parent directory when it does not exist yet.
     *
     * @param tasks tasks to save
     * @throws IOException if the directory or file cannot be written
     */
    public void save(List<Task> tasks) throws IOException {
        Path absoluteFilePath = filePath.toAbsolutePath();
        Path parentDirectory = absoluteFilePath.getParent();
        Files.createDirectories(parentDirectory);

        List<String> taskData = new ArrayList<>();
        for (Task task : tasks) {
            taskData.add(task.toFileString());
        }

        Path temporaryFile = Files.createTempFile(parentDirectory,
                absoluteFilePath.getFileName().toString(), ".tmp");
        try {
            Files.write(temporaryFile, taskData, StandardCharsets.UTF_8);
            replaceDataFile(temporaryFile, absoluteFilePath);
        } catch (IOException exception) {
            try {
                Files.deleteIfExists(temporaryFile);
            } catch (IOException cleanupException) {
                exception.addSuppressed(cleanupException);
            }
            throw exception;
        }
    }

    /**
     * Replaces the old data file atomically when the file system supports it.
     */
    private void replaceDataFile(Path temporaryFile, Path absoluteFilePath) throws IOException {
        try {
            Files.move(temporaryFile, absoluteFilePath,
                    StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException exception) {
            Files.move(temporaryFile, absoluteFilePath, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
