package waffles;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * Loads tasks from disk and saves tasks to disk.
 */
public class Storage {
    /** The system property used by tests or alternative launchers to choose a file. */
    private static final String DATA_FILE_PROPERTY = "waffles.data.file";

    /** The default data file, relative to the application's working directory. */
    private static final Path DEFAULT_DATA_FILE = Path.of("data", "waffles.txt");

    /** The file used for this storage instance. */
    private final Path dataFile;

    /** Creates storage using the configured or default relative data file. */
    public Storage() {
        String configuredPath = System.getProperty(DATA_FILE_PROPERTY);
        dataFile = configuredPath == null || configuredPath.isBlank()
                ? DEFAULT_DATA_FILE
                : Path.of(configuredPath);
    }

    /**
     * Creates storage at a specified path.
     *
     * @param filePath the path of the task data file
     */
    public Storage(String filePath) {
        try {
            dataFile = Path.of(Objects.requireNonNull(filePath));
        } catch (InvalidPathException exception) {
            throw new IllegalArgumentException("The task data file path is invalid.", exception);
        }
    }

    /**
     * Loads all valid tasks from the data file.
     *
     * <p>A missing file or folder is treated as an empty list. Corrupted
     * records are skipped so valid tasks can still be recovered.</p>
     *
     * @return the tasks recovered from disk
     */
    public ArrayList<Task> loadTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        try {
            if (!Files.exists(dataFile)) {
                return tasks;
            }
        } catch (SecurityException exception) {
            System.err.println("Warning: I couldn't access the saved tasks. Starting with an empty list.");
            return tasks;
        }

        try (BufferedReader reader = Files.newBufferedReader(dataFile, StandardCharsets.UTF_8)) {
            reader.lines()
                    .map(this::parseTask)
                    .filter(Objects::nonNull)
                    .forEach(tasks::add);
        } catch (IOException | UncheckedIOException | SecurityException exception) {
            System.err.println("Warning: I couldn't load the saved tasks. Starting with an empty list.");
        }
        return tasks;
    }

    /**
     * Saves the supplied tasks, creating the parent folder if necessary.
     *
     * @param tasks the tasks to save
     */
    public void saveTasks(List<Task> tasks) {
        try {
            Path parent = dataFile.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            try (BufferedWriter writer = Files.newBufferedWriter(
                    dataFile, StandardCharsets.UTF_8)) {
                for (Task task : tasks) {
                    writer.write(formatTask(task));
                    writer.newLine();
                }
            }
        } catch (IOException | SecurityException exception) {
            System.err.println("Warning: I couldn't save the task list to disk.");
        }
    }

    /** Converts a task to its escaped, pipe-separated record. */
    private String formatTask(Task task) {
        String type;
        StringJoiner fields = new StringJoiner("|");
        if (task instanceof Event event) {
            type = "E";
            fields.add(escape(event.getDescription()))
                    .add(escape(event.getFrom().toString()))
                    .add(escape(event.getTo().toString()));
        } else if (task instanceof Deadline deadline) {
            type = "D";
            fields.add(escape(deadline.getDescription()))
                    .add(escape(deadline.getBy().toString()));
        } else {
            type = "T";
            fields.add(escape(task.getDescription()));
        }
        return type + "|" + (task.isDone() ? "1" : "0") + "|" + fields;
    }

    /** Parses one saved record, returning null when it is corrupted. */
    private Task parseTask(String line) {
        List<String> fields = splitRecord(line);
        if (fields.size() < 3 || !fields.get(1).equals("0") && !fields.get(1).equals("1")) {
            return null;
        }

        String type = fields.get(0);
        String description = fields.get(2);
        if (description.isBlank()) {
            return null;
        }

        Task task;
        try {
            if (type.equals("T") && fields.size() == 3) {
                task = new Todo(description);
            } else if (type.equals("D") && fields.size() == 4 && !fields.get(3).isBlank()) {
                task = new Deadline(description, LocalDate.parse(fields.get(3)));
            } else if (type.equals("E") && fields.size() == 5
                    && !fields.get(3).isBlank() && !fields.get(4).isBlank()) {
                task = new Event(description, LocalDate.parse(fields.get(3)),
                        LocalDate.parse(fields.get(4)));
            } else {
                return null;
            }
        } catch (DateTimeParseException | IllegalArgumentException exception) {
            return null;
        }

        if (fields.get(1).equals("1")) {
            task.markAsDone();
        }
        return task;
    }

    /** Splits one record while preserving escaped pipe characters. */
    private List<String> splitRecord(String line) {
        ArrayList<String> fields = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        boolean escaped = false;
        for (int i = 0; i < line.length(); i++) {
            char character = line.charAt(i);
            if (escaped) {
                if (character == '|' || character == '\\') {
                    field.append(character);
                } else {
                    return List.of();
                }
                escaped = false;
            } else if (character == '\\') {
                escaped = true;
            } else if (character == '|') {
                fields.add(field.toString());
                field.setLength(0);
            } else {
                field.append(character);
            }
        }
        if (escaped) {
            return List.of();
        }
        fields.add(field.toString());
        return fields;
    }

    /** Escapes the special characters used by the record format. */
    private String escape(String value) {
        return value.replace("\\", "\\\\").replace("|", "\\|");
    }
}
