package floppy;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads tasks from disk and saves changes to an OS-independent path.
 */
public class Storage {
    private static final String FIELD_SEPARATOR = " | ";

    private final Path filePath;

    /**
     * Creates storage that reads from and writes to the specified relative path.
     *
     * @param filePath Path of the task data file.
     */
    public Storage(String filePath) {
        this.filePath = Path.of(filePath);
    }

    /**
     * Loads saved tasks, returning an empty list when the data file does not exist.
     *
     * @return Tasks restored from the data file.
     * @throws FloppyException If the file cannot be read or contains invalid data.
     */
    public ArrayList<Task> load() throws FloppyException {
        try {
            if (Files.notExists(filePath)) {
                return new ArrayList<>();
            }
            List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
            ArrayList<Task> tasks = new ArrayList<>();
            for (int i = 0; i < lines.size(); i++) {
                if (!lines.get(i).isBlank()) {
                    tasks.add(parseTask(lines.get(i), i + 1));
                }
            }
            return tasks;
        } catch (IOException | SecurityException exception) {
            throw new FloppyException("I couldn't read the saved tasks.");
        }
    }

    /**
     * Saves all tasks, creating the parent data directory when necessary.
     *
     * @param tasks Tasks to persist.
     * @throws FloppyException If the data directory or file cannot be written.
     */
    public void save(List<Task> tasks) throws FloppyException {
        try {
            Path parentDirectory = filePath.getParent();
            if (parentDirectory != null) {
                Files.createDirectories(parentDirectory);
            }
            ArrayList<String> lines = new ArrayList<>();
            for (Task task : tasks) {
                lines.add(formatTask(task));
            }
            Files.write(filePath, lines, StandardCharsets.UTF_8);
        } catch (IOException | SecurityException exception) {
            throw new FloppyException("I couldn't save the task list.");
        }
    }

    /**
     * Converts one stored line back into a task.
     *
     * @param line Stored task record.
     * @param lineNumber One-based source line used in error messages.
     * @return Task represented by the record.
     * @throws FloppyException If the record has an unknown or invalid format.
     */
    private Task parseTask(String line, int lineNumber) throws FloppyException {
        assert lineNumber > 0 : "Stored task line numbers must be one-based";

        String[] fields = line.split(" \\| ", -1);
        try {
            Task task = switch (fields[0]) {
                case "T" -> requireFieldCount(fields, 3, new Todo(fields[2]));
                case "D" -> requireFieldCount(fields, 4,
                        new Deadline(fields[2], LocalDate.parse(fields[3])));
                case "E" -> requireFieldCount(fields, 5,
                        new Event(fields[2], LocalDate.parse(fields[3]), LocalDate.parse(fields[4])));
                case "P" -> requireFieldCount(fields, 5,
                        new PeriodTask(fields[2], LocalDate.parse(fields[3]), LocalDate.parse(fields[4])));
                default -> throw new IllegalArgumentException();
            };
            if (fields[1].equals("1")) {
                task.markAsDone();
            } else if (!fields[1].equals("0")) {
                throw new IllegalArgumentException();
            }
            return task;
        } catch (ArrayIndexOutOfBoundsException | IllegalArgumentException | DateTimeException exception) {
            throw new FloppyException("Saved task data is invalid on line " + lineNumber + ".");
        }
    }

    /**
     * Checks a stored record before returning its constructed task.
     *
     * @param fields Fields in the stored record.
     * @param expectedCount Required number of fields.
     * @param task Task constructed from the fields.
     * @return The supplied task when the record has the correct field count.
     */
    private Task requireFieldCount(String[] fields, int expectedCount, Task task) {
        assert expectedCount > 0 : "A stored task must contain fields";
        assert task != null : "A parsed stored task must be constructed before validation";

        if (fields.length != expectedCount) {
            throw new IllegalArgumentException();
        }
        return task;
    }

    /**
     * Converts a task into its stable storage representation.
     *
     * @param task Task to store.
     * @return One-line storage record for the task.
     */
    private String formatTask(Task task) {
        assert task != null : "The task list must not contain null tasks";

        String commonFields = task.getTaskType().getSymbol()
                + FIELD_SEPARATOR + (task.isDone() ? "1" : "0")
                + FIELD_SEPARATOR + task.getDescription();
        return switch (task.getTaskType()) {
            case TODO -> commonFields;
            case DEADLINE -> commonFields + FIELD_SEPARATOR + ((Deadline) task).getDueDate();
            case EVENT -> commonFields + FIELD_SEPARATOR + ((Event) task).getStartDate()
                    + FIELD_SEPARATOR + ((Event) task).getEndDate();
            case PERIOD -> commonFields + FIELD_SEPARATOR + ((PeriodTask) task).getStartDate()
                    + FIELD_SEPARATOR + ((PeriodTask) task).getEndDate();
        };
    }
}
