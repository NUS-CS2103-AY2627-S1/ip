package pavanmaxxer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads and saves tasks using the application's text storage format.
 */
public final class Storage {
    private static final String CORRUPTED_DATA_MESSAGE =
            "Saved task data is corrupted.";

    private final Path filePath;

    /**
     * Creates storage that reads and writes the supplied file.
     *
     * @param filePath Relative or absolute task data path.
     */
    public Storage(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Loads saved tasks, creating an empty data file on first use.
     *
     * @return Loaded tasks in their saved order.
     * @throws PavanmaxxerException If the file cannot be read or is corrupt.
     */
    public ArrayList<Task> load() throws PavanmaxxerException {
        try {
            Path parent = filePath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            if (Files.notExists(filePath)) {
                Files.createFile(filePath);
                return new ArrayList<>();
            }

            ArrayList<Task> tasks = new ArrayList<>();
            for (String line : Files.readAllLines(filePath)) {
                if (!line.isBlank()) {
                    tasks.add(decodeTask(line));
                }
            }
            return tasks;
        } catch (IOException exception) {
            throw new PavanmaxxerException("Unable to load saved tasks.");
        }
    }

    /**
     * Saves the current task list, replacing the previous stored snapshot.
     *
     * @param tasks Task list to save.
     * @throws PavanmaxxerException If the file cannot be written.
     */
    public void save(TaskList tasks) throws PavanmaxxerException {
        try {
            Path parent = filePath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            List<String> lines = tasks.asList().stream()
                    .map(this::encodeTask)
                    .toList();
            Files.write(filePath, lines);
        } catch (IOException exception) {
            throw new PavanmaxxerException("Unable to save tasks.");
        }
    }

    private String encodeTask(Task task) {
        String state = task.isDone() ? "1" : "0";
        if (task instanceof Deadline deadline) {
            return "D | " + state + " | " + deadline.getDescription()
                    + " | " + deadline.getBy();
        }
        if (task instanceof Event event) {
            return "E | " + state + " | " + event.getDescription()
                    + " | " + event.getFrom() + " | " + event.getTo();
        }
        return "T | " + state + " | " + task.getDescription();
    }

    private Task decodeTask(String line) throws PavanmaxxerException {
        String[] fields = parseFields(line);
        boolean isDone = parseState(fields[1]);
        Task task = createTask(fields);
        restoreState(task, isDone);
        return task;
    }

    private String[] parseFields(String line) throws PavanmaxxerException {
        String[] fields = line.split(" \\| ", -1);
        if (fields.length < 3 || fields[2].isBlank()) {
            throw new PavanmaxxerException(CORRUPTED_DATA_MESSAGE);
        }
        return fields;
    }

    private Task createTask(String[] fields) throws PavanmaxxerException {
        try {
            return switch (fields[0]) {
                case "T" -> {
                    validateFieldCount(fields, 3);
                    yield new Todo(fields[2]);
                }
                case "D" -> {
                    validateFieldCount(fields, 4);
                    yield new Deadline(fields[2], LocalDate.parse(fields[3]));
                }
                case "E" -> {
                    validateFieldCount(fields, 5);
                    validateNonBlank(fields[3], fields[4]);
                    yield new Event(fields[2], fields[3], fields[4]);
                }
                default ->
                    throw new PavanmaxxerException(CORRUPTED_DATA_MESSAGE);
            };
        } catch (DateTimeParseException exception) {
            throw new PavanmaxxerException(CORRUPTED_DATA_MESSAGE);
        }
    }

    private void validateFieldCount(String[] fields, int expectedCount)
            throws PavanmaxxerException {
        if (fields.length != expectedCount) {
            throw new PavanmaxxerException(CORRUPTED_DATA_MESSAGE);
        }
    }

    private void validateNonBlank(String... fields)
            throws PavanmaxxerException {
        for (String field : fields) {
            if (field.isBlank()) {
                throw new PavanmaxxerException(CORRUPTED_DATA_MESSAGE);
            }
        }
    }

    private void restoreState(Task task, boolean isDone) {
        if (isDone) {
            task.markAsDone();
        }
    }

    private boolean parseState(String state) throws PavanmaxxerException {
        if (state.equals("1")) {
            return true;
        }
        if (state.equals("0")) {
            return false;
        }
        throw new PavanmaxxerException(CORRUPTED_DATA_MESSAGE);
    }
}
