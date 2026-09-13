package yappy.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import yappy.task.Deadline;
import yappy.task.Event;
import yappy.task.Task;
import yappy.task.TaskList;
import yappy.task.Todo;

/**
 * Loads and saves Yappy tasks using a text file on disk.
 */
public class Storage {
    private final Path dataFile;
    private int skippedRecordCount;

    /**
     * Creates a storage component that reads and writes the specified file.
     */
    public Storage(Path dataFile) {
        this.dataFile = dataFile;
    }

    /**
     * Loads all valid task records from the data file.
     * A missing file represents a user who has not saved any tasks yet.
     */
    public List<Task> load() throws IOException {
        List<Task> tasks = new ArrayList<>();
        skippedRecordCount = 0;
        if (Files.notExists(dataFile)) {
            return tasks;
        }

        for (String line : Files.readAllLines(dataFile, StandardCharsets.UTF_8)) {
            if (line.isBlank()) {
                continue;
            }
            try {
                tasks.add(parseStoredTask(line));
            } catch (IllegalArgumentException e) {
                skippedRecordCount++;
            }
        }
        return tasks;
    }

    /**
     * Returns how many malformed records were skipped by the most recent load.
     */
    public int getSkippedRecordCount() {
        return skippedRecordCount;
    }

    /**
     * Writes the complete task list, creating its parent directory when needed.
     */
    public void save(TaskList tasks) throws IOException {
        Path parentDirectory = dataFile.getParent();
        if (parentDirectory != null) {
            Files.createDirectories(parentDirectory);
        }

        List<String> records = new ArrayList<>();
        for (int i = 0; i < tasks.size(); i++) {
            records.add(tasks.get(i).toDataString());
        }
        Files.write(dataFile, records, StandardCharsets.UTF_8);
    }

    /**
     * Parses one task record written by {@link Task#toDataString()}.
     */
    private Task parseStoredTask(String line) {
        String[] fields = line.split(" \\| ", -1);
        if (fields.length < 3) {
            throw new IllegalArgumentException("Too few fields");
        }

        boolean isDone;
        if (fields[1].equals("1")) {
            isDone = true;
        } else if (fields[1].equals("0")) {
            isDone = false;
        } else {
            throw new IllegalArgumentException("Invalid task status");
        }

        Task task;
        switch (fields[0]) {
            case "T":
                requireFieldCount(fields, 3);
                task = new Todo(decodeDescription(fields[2]));
                break;
            case "D":
                requireFieldCount(fields, 4);
                task = new Deadline(decodeDescription(fields[2]), parseStoredDate(fields[3]));
                break;
            case "E":
                requireFieldCount(fields, 5);
                task = parseStoredEvent(fields);
                break;
            default:
                throw new IllegalArgumentException("Unknown task type");
        }

        if (isDone) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Parses an event record and rejects a date range that could not be created through Yappy.
     */
    private Event parseStoredEvent(String[] fields) {
        LocalDate from = parseStoredDate(fields[3]);
        LocalDate to = parseStoredDate(fields[4]);
        if (to.isBefore(from)) {
            throw new IllegalArgumentException("Event end date is before its start date");
        }
        return new Event(decodeDescription(fields[2]), from, to);
    }

    /**
     * Verifies that a stored task record has exactly the expected number of fields.
     */
    private void requireFieldCount(String[] fields, int expectedCount) {
        if (fields.length != expectedCount) {
            throw new IllegalArgumentException("Unexpected field count");
        }
    }

    /**
     * Decodes one Base64 text field from a stored task record.
     */
    private String decode(String text) {
        return new String(Base64.getDecoder().decode(text), StandardCharsets.UTF_8);
    }

    /**
     * Decodes and validates the required description field of a stored task.
     */
    private String decodeDescription(String text) {
        String description = decode(text);
        if (description.isBlank()) {
            throw new IllegalArgumentException("Empty task description");
        }
        return description;
    }

    /**
     * Parses an ISO date stored in a Base64 data-file field.
     */
    private LocalDate parseStoredDate(String text) {
        try {
            return LocalDate.parse(decode(text));
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid stored date", e);
        }
    }
}
