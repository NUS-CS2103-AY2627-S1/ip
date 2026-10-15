package nori.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import nori.NoriException;
import nori.task.Deadline;
import nori.task.Event;
import nori.task.Task;
import nori.task.TaskList;
import nori.task.Todo;

/**
 * Loads and saves Nori tasks in a local text file.
 */
public class Storage {
    private static final String FIELD_SEPARATOR = " | ";

    private final Path filePath;

    /**
     * Creates storage backed by the given file.
     *
     * @param filePath relative or absolute path to the task data file.
     */
    public Storage(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Loads all tasks from disk, creating an empty data file when none exists.
     *
     * @return tasks stored in the data file
     * @throws NoriException if the file cannot be read or contains invalid data
     */
    public TaskList load() throws NoriException {
        createDataFileIfMissing();
        final List<String> lines;
        try {
            lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new NoriException("I couldn't read the task data file.");
        }

        TaskList tasks = new TaskList();
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.isBlank()) {
                continue;
            }
            try {
                tasks.add(parseTask(line));
            } catch (NoriException exception) {
                throw new NoriException("The task data on line " + (i + 1) + " is invalid.");
            }
        }
        return tasks;
    }

    /**
     * Replaces the data file contents with the current task list.
     *
     * @param tasks tasks to persist.
     * @throws NoriException if the data file cannot be written
     */
    public void save(TaskList tasks) throws NoriException {
        createParentDirectory();
        ArrayList<String> lines = new ArrayList<>();
        for (Task task : tasks) {
            lines.add(formatTask(task));
        }
        try {
            Files.write(filePath, lines, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException exception) {
            throw new NoriException("I couldn't save the task data file.");
        }
    }

    /** Creates the parent directory and empty data file for a first-time user. */
    private void createDataFileIfMissing() throws NoriException {
        createParentDirectory();
        if (Files.exists(filePath)) {
            return;
        }
        try {
            Files.createFile(filePath);
        } catch (IOException exception) {
            throw new NoriException("I couldn't create the task data file.");
        }
    }

    /** Creates the data file's parent directory when necessary. */
    private void createParentDirectory() throws NoriException {
        Path parentPath = filePath.getParent();
        if (parentPath == null) {
            return;
        }
        try {
            Files.createDirectories(parentPath);
        } catch (IOException exception) {
            throw new NoriException("I couldn't create the task data folder.");
        }
    }

    /** Converts one task into its storage representation. */
    private String formatTask(Task task) throws NoriException {
        String completionState = task.isDone() ? "1" : "0";
        String description = encode(task.getDescription());
        if (task instanceof Todo) {
            return String.join(FIELD_SEPARATOR, "T", completionState, description);
        }
        if (task instanceof Deadline deadline) {
            return String.join(FIELD_SEPARATOR, "D", completionState, description,
                    encode(deadline.getBy().toString()), encode(deadline.getTime().toString()));
        }
        if (task instanceof Event event) {
            return String.join(FIELD_SEPARATOR, "E", completionState, description,
                    encode(event.getFrom()), encode(event.getTo()));
        }
        throw new NoriException("I couldn't save an unknown task type.");
    }

    /** Converts one storage line into a task. */
    private Task parseTask(String line) throws NoriException {
        String[] fields = line.split(" \\| ", -1);
        if (fields.length < 3) {
            throw new NoriException("A stored task has too few fields.");
        }

        Task task = switch (fields[0]) {
            case "T" -> {
                requireFieldCount(fields, 3);
                yield new Todo(decode(fields[2]));
            }
            case "D" -> {
                requireFieldCount(fields, 5);
                yield new Deadline(decode(fields[2]), parseDate(fields[3]), parseTime(fields[4]));
            }
            case "E" -> {
                requireFieldCount(fields, 5);
                yield new Event(decode(fields[2]), decode(fields[3]), decode(fields[4]));
            }
            default -> throw new NoriException("A stored task has an unknown type.");
        };

        if (fields[1].equals("1")) {
            task.markAsDone();
        } else if (!fields[1].equals("0")) {
            throw new NoriException("A stored task has an invalid completion state.");
        }
        return task;
    }

    /** Converts an encoded ISO date into a deadline date. */
    private LocalDate parseDate(String value) throws NoriException {
        try {
            return LocalDate.parse(decode(value));
        } catch (DateTimeParseException exception) {
            throw new NoriException("A stored deadline has an invalid date.");
        }
    }

    /** Converts an encoded ISO time into a deadline time. */
    private LocalTime parseTime(String value) throws NoriException {
        try {
            return LocalTime.parse(decode(value));
        } catch (DateTimeParseException exception) {
            throw new NoriException("A stored deadline has an invalid time.");
        }
    }

    /** Verifies the number of fields used by a stored task type. */
    private void requireFieldCount(String[] fields, int expectedCount) throws NoriException {
        if (fields.length != expectedCount) {
            throw new NoriException("A stored task has the wrong number of fields.");
        }
    }

    /** Encodes arbitrary task text so it cannot conflict with storage separators. */
    private String encode(String value) {
        return Base64.getUrlEncoder().withoutPadding()
                .encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    /** Decodes task text from its storage representation. */
    private String decode(String value) throws NoriException {
        try {
            byte[] bytes = Base64.getUrlDecoder().decode(value);
            String decoded = new String(bytes, StandardCharsets.UTF_8);
            if (decoded.isBlank()) {
                throw new NoriException("A stored task contains an empty value.");
            }
            return decoded;
        } catch (IllegalArgumentException exception) {
            throw new NoriException("A stored task contains invalid text.");
        }
    }
}
