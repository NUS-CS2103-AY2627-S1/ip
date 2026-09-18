package habi;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Loads HABI data from and saves HABI data to a relative data file. */
public class Storage {
    private final Path filePath;

    /**
     * Creates storage that uses the specified path.
     *
     * @param filePath Path of the task data file.
     */
    public Storage(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Loads all HABI data, creating the data directory and file on first use.
     *
     * @return Tasks and notes reconstructed from the data file.
     * @throws HabiException If the data file cannot be read or contains invalid data.
     */
    public HabiData loadData() throws HabiException {
        try {
            createParentDirectory();
            if (Files.notExists(filePath)) {
                Files.createFile(filePath);
            }
            ArrayList<Task> tasks = new ArrayList<>();
            ArrayList<Note> notes = new ArrayList<>();
            for (String line : Files.readAllLines(filePath)) {
                if (!line.isBlank()) {
                    addStoredRecord(line, tasks, notes);
                }
            }
            return new HabiData(tasks, notes);
        } catch (IOException | RuntimeException exception) {
            throw new HabiException("OOPS! I could not load tasks from the data file.");
        }
    }

    /**
     * Loads tasks for code that does not require other stored entities.
     *
     * @return Tasks reconstructed from the data file.
     * @throws HabiException If the data file cannot be read or contains invalid data.
     */
    public ArrayList<Task> load() throws HabiException {
        return loadData().getTasks();
    }

    /**
     * Saves every stored entity after a successful data change.
     *
     * @param data Data snapshot to write to the data file.
     * @throws HabiException If the data file cannot be written.
     */
    public void save(HabiData data) throws HabiException {
        try {
            createParentDirectory();
            List<String> taskLines = data.getTasks().stream().map(Task::toDataString).toList();
            List<String> noteLines = data.getNotes().stream().map(Note::toDataString).toList();
            Files.write(filePath, java.util.stream.Stream.concat(taskLines.stream(), noteLines.stream())
                    .toList());
        } catch (IOException exception) {
            throw new HabiException("OOPS! I could not save tasks to the data file.");
        }
    }

    /**
     * Saves tasks for code that does not require other stored entities.
     *
     * @param tasks Tasks to write to the data file.
     * @throws HabiException If the data file cannot be written.
     */
    public void save(List<Task> tasks) throws HabiException {
        save(new HabiData(tasks, List.of()));
    }

    private void createParentDirectory() throws IOException {
        Path parent = filePath.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
    }

    private void addStoredRecord(String line, List<Task> tasks, List<Note> notes) {
        String[] fields = line.split("\\t", -1);
        switch (fields[0]) {
            case "N" -> addStoredNote(fields, notes);
            case "T", "D", "E" -> addStoredTask(fields, tasks);
            default -> throw new IllegalArgumentException("Unknown record type");
        }
    }

    /**
     * Reconstructs one note after verifying its fixed two-column representation.
     *
     * @param fields Tab-separated fields read from one data-file line.
     * @param notes Notes receiving the reconstructed note.
     */
    private void addStoredNote(String[] fields, List<Note> notes) {
        requireFieldCount(fields, 2);
        requireNonBlank(fields[1]);
        notes.add(new Note(fields[1]));
    }

    /**
     * Reconstructs one task after validating its type-specific persistent fields.
     *
     * @param fields Tab-separated fields read from one data-file line.
     * @param tasks Tasks receiving the reconstructed task.
     */
    private void addStoredTask(String[] fields, List<Task> tasks) {
        int expectedFieldCount = switch (fields[0]) {
            case "T" -> 3;
            case "D" -> 4;
            case "E" -> 5;
            default -> throw new IllegalArgumentException("Unknown task type");
        };
        requireFieldCount(fields, expectedFieldCount);
        requireStatus(fields[1]);
        requireNonBlank(fields[2]);
        Task task = switch (fields[0]) {
            case "T" -> new Todo(fields[2]);
            case "D" -> createStoredDeadline(fields);
            case "E" -> createStoredEvent(fields);
            default -> throw new IllegalArgumentException("Unknown task type");
        };
        if (fields[1].equals("1")) {
            task.markAsDone();
        }
        tasks.add(task);
    }

    /**
     * Creates a deadline only after its date field has been checked for blank text.
     *
     * @param fields Validated deadline fields.
     * @return The reconstructed deadline.
     */
    private Deadline createStoredDeadline(String[] fields) {
        requireNonBlank(fields[3]);
        return new Deadline(fields[2], LocalDate.parse(fields[3]));
    }

    /**
     * Creates an event only after both timing fields have been checked for blank text.
     *
     * @param fields Validated event fields.
     * @return The reconstructed event.
     */
    private Event createStoredEvent(String[] fields) {
        requireNonBlank(fields[3]);
        requireNonBlank(fields[4]);
        return new Event(fields[2], fields[3], fields[4]);
    }

    /**
     * Ensures a record has its type's exact number of tab-separated columns.
     *
     * @param fields Parsed columns.
     * @param expectedCount Number of columns required for the record type.
     */
    private void requireFieldCount(String[] fields, int expectedCount) {
        if (fields.length != expectedCount) {
            throw new IllegalArgumentException("Unexpected field count");
        }
    }

    /**
     * Ensures task completion status is one of HABI's two supported encodings.
     *
     * @param status Completion status read from the file.
     */
    private void requireStatus(String status) {
        if (!status.equals("0") && !status.equals("1")) {
            throw new IllegalArgumentException("Invalid task status");
        }
    }

    /**
     * Ensures a data-file text field carries meaningful content.
     *
     * @param text Text field read from the file.
     */
    private void requireNonBlank(String text) {
        if (text.isBlank()) {
            throw new IllegalArgumentException("Blank required field");
        }
    }
}
