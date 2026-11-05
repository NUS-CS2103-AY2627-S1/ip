package larp;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads tasks from and saves tasks to a local text file.
 */
public class Storage {
    private static final String FIELD_SEPARATOR = " | ";

    private final Path filePath;

    /**
     * Creates a storage manager for the specified data file.
     *
     * @param filePath Path of the task data file.
     */
    public Storage(String filePath) {
        this.filePath = Path.of(filePath);
    }

    /**
     * Loads all saved tasks, creating an empty data file when none exists.
     *
     * @return Tasks stored in the data file.
     * @throws LarpException If the file cannot be read or contains invalid data.
     */
    public List<Task> load() throws LarpException {
        createDataFileIfMissing();

        try {
            List<String> lines = Files.readAllLines(this.filePath, StandardCharsets.UTF_8);
            List<Task> tasks = new ArrayList<>();
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                if (!line.isBlank()) {
                    tasks.add(parseTask(line, i + 1));
                }
            }
            return tasks;
        } catch (IOException e) {
            throw new LarpException("I could not read the task data file.");
        }
    }

    /**
     * Replaces the data file contents with the current task list.
     *
     * @param tasks Task list to save.
     * @throws LarpException If the data cannot be written.
     */
    public void save(TaskList tasks) throws LarpException {
        createDataFileIfMissing();

        List<String> lines = new ArrayList<>();
        for (Task task : tasks.getTasks()) {
            lines.add(formatTask(task));
        }

        try {
            Files.write(this.filePath, lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new LarpException("I could not save the task data file.");
        }
    }

    /**
     * Creates the data file and its parent directories when they do not exist.
     *
     * @throws LarpException If the data file cannot be created.
     */
    private void createDataFileIfMissing() throws LarpException {
        try {
            Path parentDirectory = this.filePath.getParent();
            if (parentDirectory != null) {
                Files.createDirectories(parentDirectory);
            }
            if (Files.notExists(this.filePath)) {
                Files.createFile(this.filePath);
            }
        } catch (IOException e) {
            throw new LarpException("I could not create the task data file.");
        }
    }

    /**
     * Reconstructs a task from one line of saved data.
     *
     * @param line Saved task data.
     * @param lineNumber One-based line number used in error messages.
     * @return Task represented by the saved data.
     * @throws LarpException If the saved data is invalid.
     */
    private Task parseTask(String line, int lineNumber) throws LarpException {
        String[] fields = line.split(" \\| ", -1);
        if (fields.length < 3) {
            throw invalidDataError(lineNumber);
        }

        boolean isDone;
        if (fields[1].equals("1")) {
            isDone = true;
        } else if (fields[1].equals("0")) {
            isDone = false;
        } else {
            throw invalidDataError(lineNumber);
        }

        return switch (fields[0]) {
            case "T" -> {
                requireFieldCount(fields, 3, lineNumber);
                yield new Todo(fields[2], isDone);
            }
            case "D" -> {
                requireFieldCount(fields, 4, lineNumber);
                try {
                    LocalDate by = LocalDate.parse(fields[3]);
                    yield new Deadline(fields[2], by, isDone);
                } catch (DateTimeParseException e) {
                    throw invalidDataError(lineNumber);
                }
            }
            case "E" -> {
                requireFieldCount(fields, 5, lineNumber);
                yield new Event(fields[2], fields[3], fields[4], isDone);
            }
            default -> throw invalidDataError(lineNumber);
        };
    }

    /**
     * Converts a task into its persistent text representation.
     *
     * @param task Task to convert.
     * @return Line of text suitable for saving.
     * @throws LarpException If the task type is unsupported.
     */
    private String formatTask(Task task) throws LarpException {
        String status = task.isDone() ? "1" : "0";
        if (task instanceof Todo) {
            return String.join(FIELD_SEPARATOR, "T", status, task.getDescription());
        }
        if (task instanceof Deadline deadline) {
            return String.join(FIELD_SEPARATOR, "D", status,
                    task.getDescription(), deadline.getBy().toString());
        }
        if (task instanceof Event event) {
            return String.join(FIELD_SEPARATOR, "E", status,
                    task.getDescription(), event.getFrom(), event.getTo());
        }
        throw new LarpException("I could not save an unknown task type.");
    }

    /**
     * Verifies that saved task data contains the expected number of fields.
     *
     * @param fields Saved task fields.
     * @param expectedCount Required number of fields.
     * @param lineNumber One-based line number used in error messages.
     * @throws LarpException If the field count is incorrect.
     */
    private void requireFieldCount(String[] fields, int expectedCount, int lineNumber)
            throws LarpException {
        if (fields.length != expectedCount) {
            throw invalidDataError(lineNumber);
        }
    }

    /**
     * Creates a consistent error for invalid saved data.
     *
     * @param lineNumber One-based number of the invalid line.
     * @return Exception describing the invalid line.
     */
    private LarpException invalidDataError(int lineNumber) {
        return new LarpException("The task data is invalid at line " + lineNumber + ".");
    }
}
